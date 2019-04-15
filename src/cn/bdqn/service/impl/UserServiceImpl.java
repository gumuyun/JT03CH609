package cn.bdqn.service.impl;

import cn.bdqn.dao.UserDao;
import cn.bdqn.exception.LoginFailException;
import cn.bdqn.pojo.Role;
import cn.bdqn.pojo.User;
import cn.bdqn.service.RoleService;
import cn.bdqn.service.UserService;
import cn.bdqn.util.PageSupport;
import org.springframework.stereotype.Service;
import sun.org.mozilla.javascript.internal.EcmaError;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private DataSource dataSource;
    @Resource
    private UserDao userDao;

    @Resource
    private RoleService roleService;

    @Override
    public int add(User user) {
        Connection conn =null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            int count = userDao.add(user, conn);
            conn.commit();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn!=null){
                try {
                    if (conn!=null){
                        conn.rollback();
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return -1;
        }finally {
            try {
                if (conn!=null){
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public int update(User user) {
        Connection conn =null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            int count = userDao.update(user, conn);
            conn.commit();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn!=null){
                try {
                    if (conn!=null){
                        conn.rollback();
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return -1;
        }finally {
            try {
                if (conn!=null){
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public PageSupport<User> findUsersByUserNameAndRoleIdByPage(String userName, Integer userRole, int pageIndex, int pageSize) {
        Connection connection =null;
        PageSupport<User> pageSupport =new PageSupport<>();
        try {
            connection = dataSource.getConnection();
            int totalCount = userDao.count(userName, userRole, connection);
            pageSupport.setTotalCount(totalCount);
            pageSupport.setPageSize(pageSize);
            pageSupport.setCurrentPageNo(pageIndex);

            if (totalCount>0){
                List<User> list = userDao.getUsersByUserNameAndRoleIdByPage(userName, userRole, pageSupport.getStartRow(), pageSupport.getPageSize(), connection);
                if (list!=null){
                    for(User user:list){
                        Role role = roleService.findById(user.getUserRole().longValue(),connection);
                        user.setRole(role);
                    }
                }

                pageSupport.setList(list);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }finally {
            if (connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return pageSupport;
    }

    @Override
    public User login(String userCode, String userPassword) {
        Connection conn =null;
        try {
            conn = dataSource.getConnection();
            return userDao.login(userCode,userPassword,conn);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoginFailException(e.getMessage());
        }finally {
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public User findUserById(Integer userId) {
        Connection conn =null;
        try {
            conn = dataSource.getConnection();
            User user = userDao.getUserById(userId, conn);
            if (user!=null){
                Role role = roleService.findById(user.getUserRole().longValue(),conn);
                user.setRole(role);
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoginFailException(e.getMessage());
        }finally {
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int findByUserCode(String userCode) {
        Connection conn =null;
        try {
            conn = dataSource.getConnection();
            int count = userDao.existsUserCode(userCode,conn);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();

        }finally {
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
