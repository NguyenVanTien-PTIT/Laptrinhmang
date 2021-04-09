/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.control;

import server.model.Users;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.model.ResultGame;

/**
 *
 * @author tieng
 */
    public class DBAccess implements Serializable {

    private Connection con;

    public DBAccess() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/btl";
            con = DriverManager.getConnection(dbURL, "root", "142871134");

        } catch (SQLException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean checkUser(Users u) {
        String sql = "SELECT * FROM users WHERE username=? AND pass=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPass());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u.setId(rs.getInt("id"));
                u.setHoten(rs.getString("hoten"));
                u.setPoints(rs.getFloat("points"));
                u.setTotaltime(rs.getLong("totaltime"));
                u.setGames(rs.getInt("games"));
                String sql1 = "UPDATE users SET isOnl=1 WHERE id=?";
                PreparedStatement ps1 = con.prepareStatement(sql1);
                ps1.setInt(1, rs.getInt("id"));
                ps1.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean signup(Users u) {
        String sql = "SELECT * FROM users WHERE username=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                String sql1 = "INSERT INTO users(hoten,username,pass,points,isonl,status,games,totaltime,email) VALUES(?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps1 = con.prepareStatement(sql1);
                ps1.setString(1, u.getHoten());
                ps1.setString(2, u.getUsername());
                ps1.setString(3, u.getPass());
                ps1.setFloat(4, 0);
                ps1.setInt(5, 0);
                ps1.setInt(6, 0);
                ps1.setInt(7, 0);
                ps1.setInt(8, 0);
                ps1.setString(9, u.getEmail());
                ps1.executeUpdate();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
       public Users checkEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            Users user = new Users();
            if (rs.next()) {
                user.setUsername(rs.getString("username"));
                user.setHoten(rs.getString("hoten"));
                
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        return null;
    }
//    update Digitalwallet set moneyaccumulated = ? where idwallet = ?
     public boolean UpdatePassacount(Users users) {
        String sql = "Update users set pass=? where username=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, users.getPass());
            ps.setString(2, users.getUsername());
            int rs = ps.executeUpdate();
            if (rs>0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    
    
  

    public ArrayList<Users> listUsers() {
        String sql1 = "SELECT * FROM users";
        ArrayList<Users> lu = new ArrayList<>();
        try {
            PreparedStatement ps1 = con.prepareStatement(sql1);
            ResultSet rs1 = ps1.executeQuery();
            ArrayList<Integer> temp1 = new ArrayList<>();
            while (rs1.next()) {
                temp1.add(rs1.getInt("id"));
                //System.out.println(rs1.getInt("id2"));
            }
            for (Integer i : temp1) {
                String sql3 = "SELECT * FROM users  WHERE id=?";
                PreparedStatement ps3 = con.prepareStatement(sql3);
                ps3.setInt(1, i);
                ResultSet rs3 = ps3.executeQuery();
                if (rs3.next()) {
                    Users u3 = new Users();
                    u3.setId(rs3.getInt("id"));
                    u3.setUsername(rs3.getString("username"));
                    u3.setHoten(rs3.getString("hoten"));
                    u3.setPoints(rs3.getFloat("points"));
                    u3.setIsOnl(rs3.getInt("isOnl"));
                    u3.setStatus(rs3.getInt("status"));
                    //System.out.println(u3.getHoten());
                    lu.add(u3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lu;
    }

    //lay BXH
    //point giam
    public ArrayList<Users> getListRank() {
        String sql1 = "SELECT * FROM users ORDER BY points DESC";
        ArrayList<Users> lu = new ArrayList<>();
                                                 
        try {
            PreparedStatement ps1= con.prepareStatement(sql1);
            ResultSet rs1 = ps1.executeQuery();
            while(rs1.next()){
//                System.out.println(i);
                Users user= new Users();
                user.setId(rs1.getInt("id"));
                user.setUsername(rs1.getString("username"));
                user.setHoten(rs1.getString("hoten"));
                user.setPoints(rs1.getFloat("points"));
                lu.add(user);
            } 
            return lu;
        } catch (SQLException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    //thoi gian giam
    public ArrayList<ResultGame> getListTime() {
        String sql1 = "SELECT * FROM resultgame ORDER BY winner_fi_time ASC";
        ArrayList<ResultGame> list = new ArrayList<>();
                                                 
        try {
            PreparedStatement ps1= con.prepareStatement(sql1);
            ResultSet rs1 = ps1.executeQuery();
            while(rs1.next()){
//                System.out.println(i);
                ResultGame kq = new ResultGame();
                kq.setIdRsGame(rs1.getInt("idResult"));
                kq.setId1(rs1.getString("winner"));
                kq.setId2(rs1.getString("loser"));
                kq.setTimeWin(rs1.getString("winner_fi_time"));
                kq.setTimeLose(rs1.getString("loser_fi_time"));
//                user.setUsername(rs1.getString("username"));
//                user.setHoten(rs1.getString("hoten"));
//                user.setPoints(rs1.getFloat("points"));
                list.add(kq);
            } 
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
  
    
    public void logOut(Users p) {
        String sql = "UPDATE users SET isOnl=0 WHERE hoten=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, p.getHoten());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void updatePoints(Users u,float p){
        try{
            String sql="UPDATE users SET points=? WHERE username=?";
            PreparedStatement ps=con.prepareStatement(sql);
            float temp=u.getPoints()+p;
            ps.setFloat(1, temp);
            ps.setString(2, u.getUsername());
            ps.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void updateStatus(Users u,int status){
        String sql = "UPDATE users SET isOnl=? WHERE username=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setString(2, u.getUsername());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int insertResult(Users u, Users u2){
        String sql = "INSERT INTO resultgame(winner, loser, winner_fi_time, loser_fi_time) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u2.getUsername());
            ps.setString(3, u.getFi_time());
            ps.setString(4, u2.getFi_time());
            ps.executeUpdate();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
