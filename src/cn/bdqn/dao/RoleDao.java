package cn.bdqn.dao;

import cn.bdqn.pojo.Role;

import java.sql.Connection;

public interface RoleDao {

    Role getById(Long id, Connection conn);
}
