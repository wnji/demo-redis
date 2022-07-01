package com.redis.demoredis.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Instant;
import java.util.*;

@Service
public class UserDao {

    @Autowired
    private  JdbcTemplate jdbcTemplate;

    public Boolean addUser(String name,String pid){

        try {
            String query1="SELECT count(*) from user where name=? and pid=?";
            int rs=  jdbcTemplate.execute(query1, new PreparedStatementCallback<Integer>() {
                @Override
                public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    ps.setString(1,name);
                    ps.setString(2,pid);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int i = rs.getInt(1);
                        return i;
                    }
                    return 0;
                }
            });
            if(rs>0){
                return false;
            }
            String query="INSERT INTO user(name,pid)VALUES(?,?)";
            return jdbcTemplate.execute(query, new PreparedStatementCallback<Boolean>() {
                @Override
                public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    ps.setString(1,name);
                    ps.setString(2,pid);
                    if(ps.executeUpdate()>0){
                        return true;
                    }
                    return false;
                }
            });

        }catch (Exception e){

        }
        return false;


    }
    public List<Map<String,String>> getUserList(){
        List<Map<String,String>> list=new ArrayList<>();
        try {
            String query="SELECT name,pid,type FROM user ";
            return jdbcTemplate.execute(query, new PreparedStatementCallback<List<Map<String,String>>>() {
                @Override
                public List<Map<String,String>> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

                    ResultSet rs=  ps.executeQuery();
                    int ii=0;
                    while (rs.next()){
                        Map<String,String> map=new HashMap<>();
                            map.put("name",ii+rs.getString("name")+" "+rs.getString("pid"));
                         //   String pid=rs.getString("pid");
                           // pid= pid.substring(0,4)+"****"+pid.substring(14,pid.length());
                          //  map.put("pid",pid);
                       // map.put("type",rs.getString("type"));
                        ii++;
                        list.add(map);
                    }
                    return list;
                }
            });

        }catch (Exception e){
        }
        return list;

    }


    public int getUserCount(){
        try {
            String query1="SELECT count(*) from user ";
            return  jdbcTemplate.execute(query1, new PreparedStatementCallback<Integer>() {
                @Override
                public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int i = rs.getInt(1);
                        return i;
                    }
                    return 0;
                }
            });

        }catch (Exception e){

        }
        return 0;
    }
}
