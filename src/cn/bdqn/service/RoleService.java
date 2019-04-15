package cn.bdqn.service;

import cn.bdqn.pojo.Role;

import java.sql.Connection;

public interface RoleService {

    Role findById(Long id,Connection conn);
}
