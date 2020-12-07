/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JButton;

/**
 *
 * @author ----LaiNhuTung----
 */
public class Card implements Serializable {
    private ArrayList<JButton> list;
    private ArrayList<JButton> list1;
    private ArrayList<JButton> list2;

    public Card() {
        list=new ArrayList<>();
        list1=new ArrayList<>();
        list2=new ArrayList<>();
    }

    public Card(ArrayList<JButton> list, ArrayList<JButton> list1, ArrayList<JButton> list2) {
        this.list = list;
        this.list1 = list1;
        this.list2 = list2;
    }

    public ArrayList<JButton> getList() {
        return list;
    }

    public void setList(ArrayList<JButton> list) {
        this.list = list;
    }

    public ArrayList<JButton> getList1() {
        return list1;
    }

    public void setList1(ArrayList<JButton> list1) {
        this.list1 = list1;
    }

    public ArrayList<JButton> getList2() {
        return list2;
    }

    public void setList2(ArrayList<JButton> list2) {
        this.list2 = list2;
    }

    
}
