/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 *
 * @author ----LaiNhuTung----
 */
public class FriendsList implements Serializable{
    private Users user;
    private ArrayList<Users>lf;

    public FriendsList() {
        lf=new ArrayList<>();
    }

    public FriendsList(Users user) {
        this.user = user;
        lf=new ArrayList<>();
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public ArrayList<Users> getLf() {
        return lf;
    }

    public void setLf(ArrayList<Users> lf) {
        this.lf = lf;
    }
}
