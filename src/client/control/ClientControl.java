/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.control;
//import View.GameFrm;
import model.FriendsList;
import model.Users;
import client.view.ChallengeFrm;
import client.view.ViewGame;
import client.view.ViewGameFrm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Card;

/**
 *
 * @author ----LaiNhuTung----
 */
public class ClientControl extends Thread{
     private int port = 1080;
    private String host = "localhost";
    private Socket mySocket;
    private Users user;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Vector vcData;
    Vector vcHead;
    JTable tblFriends;
    
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

 

    public void setTblFriends(JTable tblFriends) {
        this.tblFriends = tblFriends;
    }

    public ClientControl() {
        openConnection();
        try {
            oos = new ObjectOutputStream(mySocket.getOutputStream());
            ois = new ObjectInputStream(mySocket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openConnection() {
        try {
            mySocket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void closeConnection() {
        try {
            mySocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendData(String rq, Object o) {
        try {
            oos.writeUTF(rq);
            oos.writeObject(o);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendData(String rq) {
        try {
            oos.writeUTF(rq);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(Object o) {
        try {
            oos.writeObject(o);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveText() {
        String rs = null;
        try {
            rs = ois.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public Object receiveData() {
        Object rs = null;
        try {
            rs = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    @Override
    public void run() {
        while (true) {
            try {
           //     GameFrm game = null;
                ViewGameFrm game=null;
                String rq = ois.readUTF();
                if (rq.equals("online user")) {
                    FriendsList fl = (FriendsList) ois.readObject();
                    setModel(fl);
                } else if (rq.equals("Add friend successfully")) {
                    JOptionPane.showMessageDialog(tblFriends, rq);
                } else if (rq.equals("Add friend fail")) {
                    JOptionPane.showMessageDialog(tblFriends, rq);
                } else if (rq.equals("Value doesn't exist")) {
                    JOptionPane.showMessageDialog(tblFriends, rq);
                } else if (rq.equals("challenge")) {
                    Users thisu = (Users) ois.readObject();
                    Users user = (Users) ois.readObject();
                    ChallengeFrm c = new ChallengeFrm();
                    c.setVisible(true);
                    c.setLocationRelativeTo(tblFriends);
                    c.getTxt1().setText(c.getTxt1().getText()+user.getHoten());
                    c.getYes().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
//                            System.out.println("donh y thach dau");
                            sendData("accept", thisu);
                            sendData(user);
                            c.dispose();
                        }
                    });
                    c.getNo().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
//                            System.out.println("Khong dong y thach dau");
                            sendData("not accept",thisu);
                            sendData(user);
                            c.dispose();
                        }
                    });
                    
                } else if(rq.equals("accept1")){
//                    System.out.println("loading game...");
//                    Users u=(Users) ois.readObject();
//                    //JOptionPane.showMessageDialog(tblFriends, "READY?");
//                    Matranhinh mt=(Matranhinh) ois.readObject();
//                    game = new GameFrm(mt);
//                    game.setLbname("Name: "+u.getHoten());
//                    game.setVisible(true);
//                    game.setLocationRelativeTo(tblFriends);
//
//                    while(true){
//                        sleep(1000);
//                        if(game.getFi_Time()!=-1){
//                            //System.out.println("2");
//                            Long time=game.getFi_Time();
//                            this.user.setFi_time(time);
//                            sendData("Calculate",this.user);
//                            String rq1=ois.readUTF();
//                            if(rq1.equals("result")){
//                                String rs=(String) ois.readObject();
//                                int n=JOptionPane.showConfirmDialog (null, rs+"\nWould You Like To Play Again?","Game",JOptionPane.YES_NO_OPTION);
//                                if(n==0){
//                                    sendData("play again", this.user);
//                                    game.dispose();
//                                }
//                                else{
//                                    sendData("quit", this.user);
//                                    game.dispose();
//
//                                }
//                            }
//                            break;
//                        }
//                    }
                   System.out.println("loading game...");
                    Users u=(Users) ois.readObject();
                    //JOptionPane.showMessageDialog(tblFriends, "READY?");
                    Card card=(Card) ois.readObject();
                    game=new ViewGameFrm();
                    //game.init(card.getList1());
                  //  game = new GameFrm(mt);
                   // game.setLbname("Name: "+u.getHoten());
                    game.setVisible(true);
                    game.setLocationRelativeTo(tblFriends);
                }else if(rq.equals("accept2")){
                    System.out.println("loading game...");
                    Users u=(Users) ois.readObject();
                    //JOptionPane.showMessageDialog(tblFriends, "READY?");
                    Card card=(Card) ois.readObject();
                    game=new ViewGameFrm();
                  //  game.init(card.getList2());
                  //  game = new GameFrm(mt);
                   // game.setLbname("Name: "+u.getHoten());
                    game.setVisible(true);
                    game.setLocationRelativeTo(tblFriends);
                }
                else if(rq.equals("not accept")){
                    Users u=(Users) ois.readObject();
                    JOptionPane.showMessageDialog(tblFriends, u.getHoten()+" has refused your challenge!");
                }
//                else if(rq.equals("Wait")){
//                    JOptionPane.showMessageDialog(tblFriends, "Wait For the opponent...");
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setModel(FriendsList fl) {
        vcData = new Vector();
        ArrayList<Users> lf = fl.getLf();
        vcHead = new Vector();
        vcHead.add("Player");
        vcHead.add("Points");
        vcHead.add("Status");
        for (Users u : lf) {
            if (u.getHoten() != getUser().getHoten()) {
                Vector row = new Vector();
                row.add(u.getHoten());
                row.add(u.getPoints());
                if (u.getIsOnl() == 1) {
                    row.add("Online");
                } else if(u.getIsOnl() == 0){
                    row.add("Offline");
                }
                else{
                    row.add("Busy");
                }
                vcData.add(row);
            }
        }
        tblFriends.setModel(new DefaultTableModel(vcData, vcHead));
        tblFriends.setEnabled(true);

    }

}
