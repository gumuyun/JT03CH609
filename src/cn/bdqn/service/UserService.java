package cn.bdqn.service;

import cn.bdqn.pojo.User;
import cn.bdqn.util.PageSupport;

import java.sql.Connection;
import java.util.List;

public interface UserService {

    User findUserById(Integer userId) ;

    User login(String userCode, String userPassword);

    int update(User user);

    int findByUserCode(String userCode);

    int add(User user);

    PageSupport<User> findUsersByUserNameAndRoleIdByPage(String userName, Integer userRole, int pageIndex, int pageSize);
}
