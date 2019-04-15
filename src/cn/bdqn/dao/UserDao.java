package cn.bdqn.dao;

import cn.bdqn.pojo.User;

import java.sql.Connection;
import java.util.List;

public interface UserDao {

    int add(User user,Connection conn);

    User login(String userCode, String userPassword, Connection conn);

    List<User> getUsersByUserNameAndRoleIdByPage(String userName,Integer userRole,int startRow,int pageSize, Connection conn);

    int count(String userName,Integer userRole, Connection conn);

    User getUserById(Integer userId, Connection conn);

    int update(User user,Connection conn);

    int existsUserCode(String userCode,Connection conn);
}
