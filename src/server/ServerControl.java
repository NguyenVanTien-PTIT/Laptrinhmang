/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
//import Model.Matranhinh;

import java.awt.Button;
import java.awt.Image;
import model.FriendsList;
import model.Users;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import model.Card;
import sun.misc.BASE64Encoder;

/**
 *
 * @author ----LaiNhuTung----
 */
public class ServerControl {

    private int port = 1080;
    private ServerSocket serverSocket;
    private DBAccess db;
    HashMap<String, Handler> clientMap;
    Object lock;
    ArrayList<Pair<Handler, Handler>> pairs;
    
    public ServerControl() throws MessagingException {
        lock = new Object();
        db = new DBAccess();
       
        openConnection();
        
        clientMap = new HashMap<>();
        pairs = new ArrayList<>();
        while (true) {
            listening();
        }
    }

    public void openConnection() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnection() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   

    private void listening() throws MessagingException {
        try {
            Socket client = serverSocket.accept();
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            String rc = ois.readUTF();
            Users u = (Users) ois.readObject();
            if (rc.equals("login")) {
                System.out.println(u.getUsername());
                if (db.checkUser(u)) {

                    Handler handler = new Handler(u, lock, ois, oos);
                    handler.setSocket(client);
                    clientMap.put(u.getHoten(), handler);

                    oos.writeUTF("Login Successfully");
                    oos.writeObject(u);
                    oos.flush();

                    handler.start();

                    updateOnlineUsers();
                } else {
                    oos.writeUTF("Login Fail");
                    oos.flush();
                }
            } else if (rc.equals("signup")) {
                if (!db.checkUserExist(u)) {
                    oos.writeObject("Signup Successfully");
                    oos.flush();
                } else {
                    oos.writeObject("Signup Fail");
                    oos.flush();
                }
            } else if (rc.equals("sendEmailtoServer")) {
                Properties pro = System.getProperties();
                pro.put("mail.smtp.host", "smtp.gmail.com");
                pro.put("mail.smtp.port", "465");
                pro.put("mail.smtp.auth", "true");
                pro.put("mail.smtp.socketFactory.port", "465");
                pro.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

                int numberOfCharactor = 8;
                CreatePassword rand = new CreatePassword();
                
                        Session session = Session.getInstance(pro, new javax.mail.Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication("leanhtung.le.7@gmail.com", "b3k0jl0v3");
                    }
                });
               
                String newpass = rand.randomPassword(numberOfCharactor);
                
                Users checkEmail = new DBAccess().checkEmail(u.getEmail());
                System.out.println(checkEmail.getUsername());
                if (checkEmail != null) {
                    try {
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress("leanhtung.le.7@gmail.com"));
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(u.getEmail()));
                        message.setSubject("Repass");
                        message.setText("New Password : " + newpass);
                        Transport.send(message);

                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
//                    String pass = null;
//                    try {
//                        MessageDigest md;
//                        md = MessageDigest.getInstance("MD5");
//                        byte[] passAdmin = md.digest(newpass.getBytes());
//                        BASE64Encoder encoder = new BASE64Encoder();
//                        pass = encoder.encode(passAdmin);
//                    } catch (Exception e) {
//                    }
                       
                    checkEmail.setPass(newpass);
                    System.out.println(checkEmail.getUsername()+checkEmail.getPass());
                    boolean UpdatePassacount = new DBAccess().UpdatePassacount(checkEmail);
//                    boolean saveinfomationaccount = new dao.DAO().updateaccount(infoAccount);
                    if (UpdatePassacount) {
                        oos.writeObject("sendEmailtoServer Successfully");
                    oos.flush();
                    } else {
                        oos.writeObject("sendEmailtoServer Failing");
                    oos.flush();
                    }

                } else {
                   oos.writeObject("sendEmailtoServer Failing Because Email not found");
                    oos.flush();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateOnlineUsers() {
        try {
            for (Map.Entry<String, Handler> entry : clientMap.entrySet()) {
                Handler value = entry.getValue();
                value.getOos().writeUTF("online user");
                FriendsList fl = new FriendsList(value.getUser());
                fl.setLf(db.listFr(value.getUser()));
                value.getOos().writeObject(fl);
                value.getOos().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Handler extends Thread {

        Object lock;
        ObjectInputStream ois;
        ObjectOutputStream oos;
        Socket socket;
        Users user;

        public Handler(Users user, Object lock, ObjectInputStream ois, ObjectOutputStream oos) {
            this.user = user;
            this.lock = lock;
            this.oos = oos;
            this.ois = ois;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public ObjectInputStream getOis() {
            return ois;
        }

        public ObjectOutputStream getOos() {
            return oos;
        }

        public Users getUser() {
            return user;
        }

        private void closeSocket() {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public void run() {
            Pair<Handler, Handler> temp_pair = null;
            while (true) {
                try {
                    String rq = ois.readUTF();
                    System.out.println(rq);
                    if (rq.equals("log out")) {
                        Users u = (Users) ois.readObject();
                        db.logOut(u);
                        clientMap.remove(u.getHoten());
                        updateOnlineUsers();
                        break;
                    }  else if (rq.equals("challenge")) {
                        synchronized (lock) {
                            Users thisu = (Users) ois.readObject();
                            Users u = (Users) ois.readObject();
//                            clientMap.get(thisu.getHoten()).getOos().writeUTF("challenge");
                            clientMap.get(u.getHoten()).getOos().writeUTF("challenge");
                            clientMap.get(u.getHoten()).getOos().writeObject(thisu);
                            clientMap.get(u.getHoten()).getOos().writeObject(u);
                        }
//                        updateOnlineUsers();
                    } else if (rq.equals("accept")) {
//                        synchronized(lock){
//                            Users thisu = (Users) ois.readObject();
//                            Matranhinh mt = new Matranhinh(4, 31);
//                            db.sinhngaunhien(mt);
//                            Users u = (Users) ois.readObject();
//                            clientMap.get(thisu.getHoten()).getOos().writeUTF("accept");
//                            clientMap.get(thisu.getHoten()).getOos().writeObject(u);
//                            clientMap.get(thisu.getHoten()).getOos().writeObject(mt);
//                            clientMap.get(u.getHoten()).getOos().writeUTF("accept");
//                            clientMap.get(u.getHoten()).getOos().writeObject(thisu);
//                            clientMap.get(u.getHoten()).getOos().writeObject(mt);
//                            pairs.add(new Pair<>(clientMap.get(thisu.getHoten()),clientMap.get(u.getHoten())));
//                            db.updateStatus(u,2);
//                            db.updateStatus(thisu, 2);
//                            updateOnlineUsers();
//                        }
                        //updateOnlineUsers();
                        synchronized (lock) {
                            ArrayList<JButton> list = new ArrayList<>();
                            ArrayList<JButton> list1 = new ArrayList<>();
                            ArrayList<JButton> list2 = new ArrayList<>();
                            for (int i = 1; i <= 14; i++) {
                                JButton button;
                                String s = "src/Image/" + i + ".png";
                                button = new JButton(new ImageIcon(s));
                                list.add(button);
                            }
                            Collections.shuffle(list);
                            for (int i = 0; i < 14; i++) {
                                if (i < 7) {
                                    list1.add(list.get(i));
                                } else {
                                    list2.add(list.get(i));
                                }
                            }
                            Card card = new Card();
                            card.setList(list);
                            card.setList1(list1);
                            card.setList2(list2);
                            Users thisu = (Users) ois.readObject();
                            Users u = (Users) ois.readObject();
                            clientMap.get(thisu.getHoten()).getOos().writeUTF("accept1");
                            clientMap.get(thisu.getHoten()).getOos().writeObject(u);
                            clientMap.get(thisu.getHoten()).getOos().writeObject(card);
                            clientMap.get(u.getHoten()).getOos().writeUTF("accept2");
                            clientMap.get(u.getHoten()).getOos().writeObject(thisu);
                            clientMap.get(u.getHoten()).getOos().writeObject(card);
                            pairs.add(new Pair<>(clientMap.get(thisu.getHoten()), clientMap.get(u.getHoten())));
                            db.updateStatus(u, 2);
                            db.updateStatus(thisu, 2);
                            updateOnlineUsers();
                        }
                        // updateOnlineUsers();
                    } else if (rq.equals("not accept")) {
                        synchronized (lock) {
                            Users thisu = (Users) ois.readObject();
                            Users u = (Users) ois.readObject();
                            clientMap.get(thisu.getHoten()).getOos().writeUTF("not accept");
                            clientMap.get(thisu.getHoten()).getOos().writeObject(u);
//                            clientMap.get(u.getHoten()).getOos().writeUTF("not accept");
//                            clientMap.get(u.getHoten()).getOos().writeObject(thisu);
                        }
                        //updateOnlineUsers();
                    } else if (rq.equals("Calculate")) {
                        synchronized (lock) {
                            Users user = (Users) ois.readObject();
                            Handler temp = clientMap.get(user.getHoten());

                            for (Pair<Handler, Handler> i : pairs) {
                                if (i.getKey().getUser().getHoten().equals(temp.getUser().getHoten())) {
                                    i.getKey().getUser().setFi_time(user.getFi_time());
                                    temp_pair = i;
                                    i.getKey().getUser().setCheck(1);
                                    System.out.println("1");
                                    break;
                                }
                                if (i.getValue().getUser().getHoten().equals(temp.getUser().getHoten())) {
                                    i.getValue().getUser().setFi_time(user.getFi_time());
                                    temp_pair = i;
                                    i.getValue().getUser().setCheck(1);
                                    System.out.println("2");
                                    break;
                                }
                            }
//                            if(check==1){
//                                temp.getOos().writeUTF("Wait");
//                                updateOnlineUsers();
//                            }
                            System.out.println(temp_pair.getKey().getUser().getCheck());
                            System.out.println(temp_pair.getValue().getUser().getCheck());
                            if (temp_pair.getKey().getUser().getCheck() == 1 && temp_pair.getValue().getUser().getCheck() == 1) {
                                long t1 = temp_pair.getKey().getUser().getFi_time();
                                long t2 = temp_pair.getValue().getUser().getFi_time();
                                System.out.println("vao");
                                if (t1 < t2) {
                                    temp_pair.getKey().oos.writeUTF("result");
                                    temp_pair.getKey().oos.writeObject("YOU WIN");
                                    temp_pair.getValue().oos.writeUTF("result");
                                    temp_pair.getValue().oos.writeObject("YOU LOSE");
                                    db.updatePoints(temp_pair.getKey().getUser(), 1);
                                    System.out.println(temp_pair.getKey().getUser().getHoten() + "win");
                                } else if (t1 > t2) {
                                    temp_pair.getKey().oos.writeUTF("result");
                                    temp_pair.getKey().oos.writeObject("YOU LOSE");
                                    temp_pair.getValue().oos.writeUTF("result");
                                    temp_pair.getValue().oos.writeObject("YOU WIN");
                                    db.updatePoints(temp_pair.getValue().getUser(), 1);
                                    System.out.println(temp_pair.getKey().getUser().getHoten() + "lose");
                                    //System.out.println("lose");
                                } else {
                                    temp_pair.getKey().oos.writeUTF("result");
                                    temp_pair.getKey().oos.writeObject("TIE");
                                    temp_pair.getValue().oos.writeUTF("result");
                                    temp_pair.getValue().oos.writeObject("TIE");
                                    db.updatePoints(temp_pair.getKey().getUser(), (float) 0.5);
                                    db.updatePoints(temp_pair.getValue().getUser(), (float) 0.5);
                                    //
                                }
                                temp_pair.getKey().getUser().setFi_time(-1);
                                temp_pair.getValue().getUser().setFi_time(-1);
                                temp_pair.getKey().getUser().setCheck(0);
                                temp_pair.getValue().getUser().setCheck(0);

//                                if(temp_pair.getKey().getUser().getCheck()==2&&temp_pair.getValue().getUser().getCheck()==2){
//                                    temp_pair.getKey().oos.writeUTF("accept");
//                                    temp_pair.getKey().oos.writeObject(temp_pair.getKey().getUser());
//                                    temp_pair.getValue().oos.writeUTF("accept");
//                                    temp_pair.getValue().oos.writeObject(temp_pair.getValue().getUser());
//                                }
//                                else if(temp_pair.getKey().getUser().getCheck()==3||temp_pair.getValue().getUser().getCheck()==3){
//                                    temp_pair.getKey().oos.writeUTF("not accept");
//                                    temp_pair.getKey().oos.writeObject(temp_pair.getKey().getUser());
//                                    temp_pair.getValue().oos.writeUTF("not accept");
//                                    temp_pair.getValue().oos.writeObject(temp_pair.getValue().getUser());
//                                }
//                                pairs.remove(temp_pair);
                            }

                        }

                    } else if (rq.equals("play again")) {
                        synchronized (lock) {
                            Users user = (Users) ois.readObject();
                            Handler temp = clientMap.get(user.getHoten());
                            if (temp_pair.getKey().equals(temp)) {
                                temp_pair.getKey().getUser().setCheck(2);
                            }
                            if (temp_pair.getValue().equals(temp)) {
                                temp_pair.getValue().getUser().setCheck(2);
                            }
                            if (temp_pair.getKey().getUser().getCheck() == 2 && temp_pair.getValue().getUser().getCheck() == 2) {
                                temp_pair.getKey().oos.writeUTF("accept");
                                temp_pair.getKey().oos.writeObject(temp_pair.getKey().getUser());
                                temp_pair.getValue().oos.writeUTF("accept");
                                temp_pair.getValue().oos.writeObject(temp_pair.getValue().getUser());
                                temp_pair.getKey().getUser().setCheck(0);
                                temp_pair.getValue().getUser().setCheck(0);

                            }
                        }
                    } else if (rq.equals("quit")) {
                        synchronized (lock) {
                            Users user = (Users) ois.readObject();
                            Handler temp = clientMap.get(user.getHoten());
                            if (temp_pair.getKey().equals(temp)) {
                                temp_pair.getKey().getUser().setCheck(3);
                            }
                            if (temp_pair.getValue().equals(temp)) {
                                temp_pair.getValue().getUser().setCheck(3);
                            }
                            if (temp_pair.getKey().getUser().getCheck() == 3 || temp_pair.getValue().getUser().getCheck() == 3) {
                                temp_pair.getKey().oos.writeUTF("not accept");
                                temp_pair.getKey().oos.writeObject(temp_pair.getKey().getUser());
                                temp_pair.getValue().oos.writeUTF("not accept");
                                temp_pair.getValue().oos.writeObject(temp_pair.getValue().getUser());
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ServerControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }
}
