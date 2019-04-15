package cn.bdqn.dao.impl;

import cn.bdqn.dao.BaseDao;
import cn.bdqn.dao.UserDao;
import cn.bdqn.pojo.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class UserDaoImpl extends BaseDao<User> implements UserDao {

    @Override
    public int existsUserCode(String userCode,Connection conn) {
        String sql="select count(1) from smbms_user where userCode=?";
        return super.executeQueryCount(sql,conn,userCode);
    }

    @Override
    public User getUserById(Integer userId, Connection conn) {
        String sql="select * from smbms_user where id=?";
        return super.executeQueryUnique(sql,conn,userId);
    }

    @Override
    public int update(User user, Connection conn) {

        String sql="UPDATE `smbms_user` SET `userName`=?,`gender`=?,`birthday`=?,`phone`=?,`address`=?,`userRole`=?,`modifyBy`=?,`modifyDate`=? WHERE `id`=?";
        return super.executeUpdate(sql,conn,user.getUserName(),user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),user.getUserRole(),user.getModifyBy(),user.getModifyDate(),user.getId());
    }

    @Override
    public int add(User user, Connection conn) {
        String sql="INSERT INTO `smbms_user` (`userCode`,`userName`,`userPassword`,`gender`,`birthday`,`phone`,`address`,`userRole`,`createdBy`,`creationDate`,`idPicPath`) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        return super.executeUpdate(sql,conn,user.getUserCode(),user.getUserName(),user.getUserPassword(),user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),user.getUserRole(),user.getCreatedBy(),user.getCreationDate(),user.getIdPicPath());
    }

    @Override
    public User login(String userCode, String userPassword, Connection conn) {
        String sql="SELECT su.*,sr.roleName FROM smbms_user su,smbms_role sr WHERE su.userRole=sr.id AND su.userCode=? AND su.userPassword=?";
        return super.executeQueryUnique(sql,conn,userCode,userPassword);
    }

    @Override
    public List<User> getUsersByUserNameAndRoleIdByPage(String userName, Integer userRole, int startRow, int pageSize, Connection conn) {
        //SELECT su.*,sr.roleName FROM smbms_user su,smbms_role sr WHERE su.userRole=sr.`id` AND su.userName=? AND su.userRole=?;
        StringBuffer sbf=new StringBuffer("SELECT su.*,sr.roleName FROM smbms_user su,smbms_role sr WHERE su.userRole=sr.id ");
        List<Object> params=new ArrayList<>();
        if (userRole!=null && userRole!=0){
            sbf.append(" and userRole =? ");
            params.add(userRole);
        }
        if (userName!=null && !"".equals(userName)){
            sbf.append(" and userName LIKE CONCAT('%',?,'%')");
            params.add(userName);
        }
        sbf.append(" order by creationDate desc ");
        sbf.append(" limit ?,? ");
        params.add(startRow);
        params.add(pageSize);
        return super.executeQuery(sbf.toString(),conn,params.toArray());
    }

    @Override
    public int count(String userName, Integer userRole, Connection conn) {
        //String sql="SELECT COUNT(1) FROM smbms_user WHERE userRole =? AND userName LIKE CONCAT('%',?,'%')";
        StringBuffer sbf=new StringBuffer("SELECT COUNT(1) FROM smbms_user where 1=1 ");
        List<Object> params=new ArrayList<>();

        if (userRole!=null && userRole!=0){
            sbf.append(" and userRole =? ");
            params.add(userRole);
        }
        if (userName!=null && !"".equals(userName)){
            sbf.append(" and userName LIKE CONCAT('%',?,'%')");
            params.add(userName);
        }

        return super.executeQueryCount(sbf.toString(),conn,params.toArray());
    }

    @Override
    public User getEntity(ResultSet rs) {
        User user=null;
        if (rs!=null){
            try {
                int id = rs.getInt("id");
                int gender = rs.getInt("gender");
                int userRole = rs.getInt("userRole");
                int createdBy = rs.getInt("createdBy");
                int modifyBy = rs.getInt("modifyBy");
                String userCode = rs.getString("userCode");
                String userName = rs.getString("userName");
                String userPassword = rs.getString("userPassword");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String idPicPath = rs.getString("idPicPath");
                /*String userRoleName = rs.getString("roleName");*/
                Date birthday = rs.getDate("birthday");
                Timestamp creationTimestamp = rs.getTimestamp("creationDate");
                Timestamp modifyTimestamp = rs.getTimestamp("modifyDate");
                Date creationDate=creationTimestamp==null?null:new Date(creationTimestamp.getTime());
                Date modifyDate=modifyTimestamp==null?null:new Date(modifyTimestamp.getTime());

                Calendar calendar = Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                calendar.setTime(birthday);
                int age= year-calendar.get(Calendar.YEAR);

                user=new User(id,userCode,userName,userPassword,gender,birthday,phone,address,userRole,createdBy,creationDate,modifyBy,modifyDate,age,null);
                user.setIdPicPath(idPicPath);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
