/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.control;

import server.model.Users;
import client.view.ViewBXHScore;
import client.view.ViewBXHTime;
import client.view.ViewGameFrm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import server.model.ResultGame;

/**
 *
 * @author tieng
 */
public class ClientControl extends Thread {

    ViewGameFrm game;
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

    public void sendEmailtoServer(String email) {
        try {
            Users users = new Users();
            users.setEmail(email);
            oos.writeUTF("sendEmailtoServer");
            oos.writeObject(users);
            oos.flush();
        } catch (Exception e) {
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

                String rq = ois.readUTF();
                System.out.println(rq);
                if (rq.equals("online")) {
                    Users u1 = (Users) ois.readObject();
                    setModel(u1);
                } else if (rq.equals("bxhScore")) {
                    ArrayList<Users> list = (ArrayList<Users>) ois.readObject();
                    ViewBXHScore vbxh = new ViewBXHScore(list);
                    vbxh.setVisible(true);
                    vbxh.setLocationRelativeTo(tblFriends);
                } else if (rq.equals("bxhTime")) {
                    ArrayList<ResultGame> list = (ArrayList<ResultGame>) ois.readObject();
                    ViewBXHTime vbxh = new ViewBXHTime(list);
                    vbxh.setVisible(true);
                    vbxh.setLocationRelativeTo(tblFriends);

                } else if (rq.equals("challenge")) {
                    Users u2 = (Users) ois.readObject();
                    Users u1 = (Users) ois.readObject();
                    int x = JOptionPane.showConfirmDialog(tblFriends, "Bạn có muốn chơi cùng " + u1.getUsername() + " không?", "Thông báo", JOptionPane.YES_NO_OPTION);
                    if (x == 0) {
                        System.out.println("dong y thach dau");
                        sendData("accept", u2);
                        sendData(u1);
                    } else if (x == 1) {
                        System.out.println("Khong dong y thach dau");
                        sendData("not accept", u2);
                        sendData(u1);
                    }
                } else if (rq.equals("begin")) {
                    Users player = (Users) ois.readObject();
                    List<Integer> a = (List<Integer>) ois.readObject();
                    Integer img = (Integer) ois.readObject();
                    game = new ViewGameFrm(player, ois, oos, a, img);
                    game.setVisible(true);
                    game.setLocationRelativeTo(tblFriends);
                } else if (rq.equals("result")) {
                    String kq;
                    try {
                        kq = (String) ois.readObject();
                        if (kq.equals("thang")) {
                            System.out.print(kq);
                            int x = JOptionPane.showConfirmDialog(game, user.getUsername() + "win"
                                    + " .Bạn muốn chơi lại không?", "Thông Báo", JOptionPane.YES_NO_OPTION);
                            if (x == 0) {
                                sendData("play again", user);
                                System.out.print("co choi");
                                game.dispose();
                            } else if (x == 1) {
                                sendData("quit", user);
                                System.out.print("eo choi");
                                game.dispose();
                            }
                        } else if (kq.equals("thua")) {
                            System.out.print(kq);
                            int x = JOptionPane.showConfirmDialog(game, user.getUsername() + "thua"
                                    + " .Bạn muốn chơi lại không?", "Thông Báo", JOptionPane.YES_NO_OPTION);
                            if (x == 0) {
                                sendData("play again", user);
                                System.out.print("co choi");
                                game.dispose();
                            } else if (x == 1) {
                                sendData("quit", user);
                                System.out.print("eo choi");
                                game.dispose();
                            }
                        }
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(ViewGameFrm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (rq.equals("not accept")) {
                    Users u = (Users) ois.readObject();
                    JOptionPane.showMessageDialog(tblFriends, u.getUsername() + " không thèm chơi với bạn!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setModel(Users ul) {
        vcData = new Vector();
        ArrayList<Users> lu = ul.getLu();
        vcHead = new Vector();
        vcHead.add("Player");
        vcHead.add("Points");
        vcHead.add("Status");
        for (Users u : lu) {
            if (u.getUsername() != getUser().getUsername() && u.getId() != this.getUser().getId()) {
                Vector row = new Vector();
                row.add(u.getUsername());
                row.add(u.getPoints());
                if (u.getIsOnl() == 1) {
                    row.add("Online");
                } else if (u.getIsOnl() == 0) {
                    row.add("Offline");
                } else {
                    row.add("Busy");
                }
                vcData.add(row);
            }
        }
        tblFriends.setModel(new DefaultTableModel(vcData, vcHead));
        tblFriends.setEnabled(true);
    }
}
