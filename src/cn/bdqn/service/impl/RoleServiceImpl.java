package cn.bdqn.service.impl;

import cn.bdqn.dao.RoleDao;
import cn.bdqn.exception.LoginFailException;
import cn.bdqn.pojo.Role;
import cn.bdqn.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private DataSource dataSource;

    @Resource
    private RoleDao roleDao;
    @Override
    public Role findById(Long id,Connection conn) {
        return roleDao.getById(id,conn);
    }
}
