/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author ----LaiNhuTung----
 */
public class ViewGame extends JFrame {
    private JPanel panel;
    private JPanel panel2;
    private JButton btl;
    public ViewGame(){
//        this.list=list;
//         for(int i=1;i<=7;i++){
//            String s="src/Image/"+i+".png";
//            JButton btl= new JButton(new ImageIcon(s));
//            list.add(btl);
//        }
//        init(list);
    }
    public void init(ArrayList<JButton> list){
        panel=new JPanel();
        panel2=new JPanel();
        for(int i=0;i<7;i++){
            panel.add(list.get(i));
            btl=new JButton(new ImageIcon("src/Image/up.png"));
            panel2.add(btl);
        }
        this.add(panel,BorderLayout.SOUTH);
        this.add(panel2,BorderLayout.NORTH);
        this.setSize(800, 400);
    }    
//    public static void main(String[] args) {
//        ArrayList<JButton> list=new ArrayList<>();
//        for(int i=1;i<=7;i++){
//            String s="src/Image/"+i+".png";
//            JButton btl= new JButton(new ImageIcon(s));
//            list.add(btl);
//        }
//        ViewGame game=new ViewGame();
//        game.init(list);
//        game.setVisible(true);
//    }
}
