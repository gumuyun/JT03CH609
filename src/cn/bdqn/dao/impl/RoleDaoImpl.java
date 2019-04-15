package cn.bdqn.dao.impl;

import cn.bdqn.dao.BaseDao;
import cn.bdqn.dao.RoleDao;
import cn.bdqn.pojo.Role;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RoleDaoImpl extends BaseDao<Role> implements RoleDao {

    @Override
    public Role getById(Long id, Connection conn) {
        String sql="select * from smbms_role where id=?";
        return super.executeQueryUnique(sql,conn,id);
    }

    @Override
    public Role getEntity(ResultSet rs) {
        Role role=null;
        if (rs!=null){
            try {
                long id = rs.getLong("id");
                String roleName = rs.getString("roleName");
                String roleCode = rs.getString("roleCode");
                role=new Role();
                role.setId(id);
                role.setRoleName(roleName);
                role.setRoleCode(roleCode);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return role;
    }
}
