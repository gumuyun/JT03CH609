package cn.bdqn.dao;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {
    private Logger logger=Logger.getLogger("BaseDao");


    /**
     *  executeQuery
     * @param sql
     * @param conn
     * @param params
     * @return
     */
    public List<T> executeQuery(String sql,Connection conn,Object... params){//可变参数，0-N参数
        List<T> list=new ArrayList<>();

        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps = conn.prepareStatement(sql);
            if (params!=null && params.length>0){
                for (int i=0;i<params.length;i++){
                    ps.setObject((i+1),params[i]);
                }
            }
            rs = ps.executeQuery();//user   userId userName.....
            while (rs.next()){
                //获得,实体对象
                T entity = getEntity(rs);
                list.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("执行查询出错！");
        }finally {
            logger.debug("executeQuery:---"+sql);
            closeResource(null,ps,rs);
        }
        return list;

    }

    public T executeQueryUnique(String sql,Connection conn,Object... params){//可变参数，0-N参数

        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps = conn.prepareStatement(sql);
            if (params!=null && params.length>0){
                for (int i=0;i<params.length;i++){
                    ps.setObject((i+1),params[i]);
                }
            }
            rs = ps.executeQuery();//user   userId userName.....
            if (rs.next()){
                //获得,实体对象
               return getEntity(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("执行查询出错！");
        }finally {
            logger.debug("executeQueryUnique:---"+sql);
            closeResource(null,ps,rs);
        }
        return null;

    }


    public int executeQueryCount(String sql,Connection conn,Object... params){//可变参数，0-N参数
        int totalCount=0;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps = conn.prepareStatement(sql);
            if (params!=null && params.length>0){
                for (int i=0;i<params.length;i++){
                    ps.setObject((i+1),params[i]);
                }
            }
            rs = ps.executeQuery();//user   userId userName.....
            if (rs.next()){
                totalCount=rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("执行查询出错！");
        }finally {
            logger.debug("executeQueryCount:---"+sql);
            closeResource(null,ps,rs);
        }
        return totalCount;

    }

    public abstract T getEntity(ResultSet rs);

    //CUD  事务？？？？？？------业务？？？？？
    public int executeUpdate(String sql,Connection conn,Object... params){
        int count=0;
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            if (params!=null && params.length>0){
                for (int i=0;i<params.length;i++){
                    ps.setObject((i+1),params[i]);
                }
            }
            count= ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("执行更新出错！");
        }finally {
            closeResource(null,ps,null);
        }
        return count;
    }


    //关闭连接
    public static void closeResource(Connection conn, PreparedStatement ps, ResultSet rs){
        try {
            if (rs!=null){
                rs.close();
            }
            if (ps!=null){
                ps.close();
            }
            if (conn!=null){
                conn.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
