/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
//import Model.Matranhinh;
import java.awt.Button;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Users;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
/**
 *
 * @author ----LaiNhuTung----
 */
class MyButton extends JButton {

    private boolean isLastButton;

    public MyButton() {

        super();

        initUI();
    }

    public MyButton(Image image) {

        super(new ImageIcon(image));

        initUI();
    }

    private void initUI() {

        isLastButton = false;
        BorderFactory.createLineBorder(Color.gray);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.yellow));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    public void setLastButton() {
        
        isLastButton = true;
    }

    public boolean isLastButton() {

        return isLastButton;
    }
}

public class ServerControl {
     private int port = 1080;
    private ServerSocket serverSocket;
    private DBAccess db;
    HashMap<String, Handler> clientMap;
    Object lock;
    ArrayList<Pair<Handler,Handler>>pairs;
    public ServerControl() throws MessagingException {
        lock = new Object();
        db = new DBAccess();
        openConnection();
        clientMap = new HashMap<>();
        pairs=new ArrayList<>();
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
                    clientMap.put(u.getUsername(), handler);

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
            }else if (rc.equals("sendEmailtoServer")) {
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
                Users ul = new Users();
                ul.setLu(db.listUsers());
                value.getOos().writeObject(ul);
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
            Pair<Handler,Handler> temp_pair=null;
            while (true) {
                try {
                    String rq = ois.readUTF();
                    System.out.println(rq);
                    if (rq.equals("log out")) {
                        Users u = (Users) ois.readObject();
                        db.logOut(u);
                        clientMap.remove(u.getUsername());
                        updateOnlineUsers();
                        break;
                    }
                    else if (rq.equals("end match")){
                        Users u = (Users) ois.readObject();
                        db.endMatch(u);
//                        clientMap.remove(u.getUsername());
//                        updateOnlineUsers();
                        break;
                    }
                    else if (rq.equals("add friend")) {
                        Users thisu = (Users) ois.readObject();
                        Users u = (Users) ois.readObject();
                        if (db.checkUserExist2(u)) {
                            if (db.addFriend(thisu, u)) {
                                synchronized (lock) {
                                    clientMap.get(thisu.getUsername()).getOos().writeUTF("Add friend successfully");
                                }
                                updateOnlineUsers();
                            } else {
                                synchronized (lock) {
                                    clientMap.get(thisu.getUsername()).getOos().writeUTF("Add friend fail");
                                }
                                updateOnlineUsers();
                            }
                        } else {
                            synchronized (lock) {
                                clientMap.get(thisu.getUsername()).getOos().writeUTF("Value doesn't exist");
                            }
                            updateOnlineUsers();
                        }
                    } else if(rq.equals("challenge")){
                        synchronized(lock){
                            Users thisu = (Users) ois.readObject();
                            Users u = (Users) ois.readObject();
//                            clientMap.get(thisu.getUsername()).getOos().writeUTF("challenge");
                            clientMap.get(u.getUsername()).getOos().writeUTF("challenge");
                            clientMap.get(u.getUsername()).getOos().writeObject(thisu);
                            clientMap.get(u.getUsername()).getOos().writeObject(u);
                        }
//                        updateOnlineUsers();
                    } 
                    
                    else if(rq.equals("accept")){
//                        synchronized(lock){
//                            Users thisu = (Users) ois.readObject();
//                            Matranhinh mt = new Matranhinh(4, 31);
//                            db.sinhngaunhien(mt);
//                            Users u = (Users) ois.readObject();
//                            clientMap.get(thisu.getUsername()).getOos().writeUTF("accept");
//                            clientMap.get(thisu.getUsername()).getOos().writeObject(u);
//                            clientMap.get(thisu.getUsername()).getOos().writeObject(mt);
//                            clientMap.get(u.getUsername()).getOos().writeUTF("accept");
//                            clientMap.get(u.getUsername()).getOos().writeObject(thisu);
//                            clientMap.get(u.getUsername()).getOos().writeObject(mt);
//                            pairs.add(new Pair<>(clientMap.get(thisu.getUsername()),clientMap.get(u.getUsername())));
//                            db.updateStatus(u,2);
//                            db.updateStatus(thisu, 2);
//                            updateOnlineUsers();
//                        }
                        //updateOnlineUsers();
                        synchronized(lock){
                            Set<Integer> set = new HashSet<>();
                            Random generator = new Random();
                            List<Integer> a = new ArrayList<>();
                            int size = 0;
                            while(true){
                                Integer value = generator.nextInt(24) + 1;
                                set.add(value);
                                if(set.size()>size) {
                                    a.add(value);
                                    size = set.size();
                                }
                                if(set.size()==24) break;
                            }
                            Random generator2 = new Random();
                            Integer value2 = generator.nextInt(2) + 1;
                            System.out.println(value2);
                            Users thisu = (Users) ois.readObject();
                            Users u = (Users) ois.readObject();
                            clientMap.get(thisu.getUsername()).getOos().writeUTF("begin");
                            clientMap.get(thisu.getUsername()).getOos().writeObject(u);
                            clientMap.get(thisu.getUsername()).getOos().writeObject(a);
                            clientMap.get(thisu.getUsername()).getOos().writeObject(value2);
                            clientMap.get(u.getUsername()).getOos().writeUTF("begin");
                            clientMap.get(u.getUsername()).getOos().writeObject(thisu);
                            clientMap.get(u.getUsername()).getOos().writeObject(a);
                            clientMap.get(u.getUsername()).getOos().writeObject(value2);
                            pairs.add(new Pair<>(clientMap.get(thisu.getUsername()),clientMap.get(u.getUsername())));
                            db.updateStatus(u,2);
                            db.updateStatus(thisu, 2);
                            updateOnlineUsers();
                        }
                       // updateOnlineUsers();
                    }
                    else if(rq.equals("not accept")){
                        synchronized(lock){
                            Users thisu = (Users) ois.readObject();
                            Users u = (Users) ois.readObject();
                            clientMap.get(thisu.getUsername()).getOos().writeUTF("not accept");
                            clientMap.get(thisu.getUsername()).getOos().writeObject(u);
//                            clientMap.get(u.getUsername()).getOos().writeUTF("not accept");
//                            clientMap.get(u.getUsername()).getOos().writeObject(thisu);
                        }
                        //updateOnlineUsers();
                    }
                    else if(rq.equals("Calculate")){
                        synchronized(lock){
                            Users user = (Users) ois.readObject();
                            String time = ois.readUTF();
                            Handler temp=clientMap.get(user.getUsername());
                            
                            for(Pair<Handler,Handler>i:pairs){
                                if(i.getKey().getUser().getUsername().equals(temp.getUser().getUsername())){
                                    i.getKey().getUser().setFi_time(time);
                                    temp_pair = i;
                                    i.getKey().getUser().setCheck(1);
                                    System.out.println("1");
                                    break;
                                }
                                if(i.getValue().getUser().getUsername().equals(temp.getUser().getUsername())){
                                    i.getValue().getUser().setFi_time(time);
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
                            if(temp_pair.getKey().getUser().getCheck()==1&&temp_pair.getValue().getUser().getCheck()==1){
                                String t1=temp_pair.getKey().getUser().getFi_time();
                                String t2=temp_pair.getValue().getUser().getFi_time();
                                System.out.println("vao");
                                String[] arStr = t1.split("\\:");
                                String[] arStr2 = t2.split("\\:");
                                for (int i=0;i<4;i++) {
                                    if(Integer.parseInt(arStr[i])> Integer.parseInt(arStr2[i])){
                                        temp_pair.getKey().oos.writeUTF("result");
                                        temp_pair.getKey().oos.writeObject("YOU LOSE");
                                        temp_pair.getValue().oos.writeUTF("result");
                                        temp_pair.getValue().oos.writeObject("YOU WIN");
                                        db.updatePoints(temp_pair.getKey().getUser(), 1);
                                        System.out.println(temp_pair.getKey().getUser().getUsername()+"win");
                                        break;
                                    }else{
                                        if(Integer.parseInt(arStr[i])<Integer.parseInt(arStr2[i])){
                                            temp_pair.getKey().oos.writeUTF("result");
                                            temp_pair.getKey().oos.writeObject("YOU WIN");
                                            temp_pair.getValue().oos.writeUTF("result");
                                            temp_pair.getValue().oos.writeObject("YOU LOSE");
                                            db.updatePoints(temp_pair.getKey().getUser(), 1);
                                            System.out.println(temp_pair.getKey().getUser().getUsername()+"win");
                                            break;
                                        }else{
                                            temp_pair.getKey().oos.writeUTF("result");
                                            temp_pair.getKey().oos.writeObject("TIE");
                                            temp_pair.getValue().oos.writeUTF("result");
                                            temp_pair.getValue().oos.writeObject("TIE");
                                            db.updatePoints(temp_pair.getKey().getUser(), (float) 0.5);
                                            db.updatePoints(temp_pair.getValue().getUser(), (float) 0.5);
                                        }
                                    }
                                }
//                                if(t1<t2){
//                                    temp_pair.getKey().oos.writeUTF("result");
//                                    temp_pair.getKey().oos.writeObject("YOU WIN");
//                                    temp_pair.getValue().oos.writeUTF("result");
//                                    temp_pair.getValue().oos.writeObject("YOU LOSE");
//                                    db.updatePoints(temp_pair.getKey().getUser(), 1);
//                                    System.out.println(temp_pair.getKey().getUser().getUsername()+"win");
//                                }
//                                else if(t1>t2){
//                                    temp_pair.getKey().oos.writeUTF("result");
//                                    temp_pair.getKey().oos.writeObject("YOU LOSE");
//                                    temp_pair.getValue().oos.writeUTF("result");
//                                    temp_pair.getValue().oos.writeObject("YOU WIN");
//                                    db.updatePoints(temp_pair.getValue().getUser(), 1);
//                                    System.out.println(temp_pair.getKey().getUser().getUsername()+"lose");
//                                    //System.out.println("lose");
//                                }
//                                else{
//                                    temp_pair.getKey().oos.writeUTF("result");
//                                    temp_pair.getKey().oos.writeObject("TIE");
//                                    temp_pair.getValue().oos.writeUTF("result");
//                                    temp_pair.getValue().oos.writeObject("TIE");
//                                    db.updatePoints(temp_pair.getKey().getUser(), (float) 0.5);
//                                    db.updatePoints(temp_pair.getValue().getUser(), (float) 0.5);
//                                    //
//                                }
//                                temp_pair.getKey().getUser().setFi_time(-1);
//                                temp_pair.getValue().getUser().setFi_time(-1);
//                                temp_pair.getKey().getUser().setCheck(0);
//                                temp_pair.getValue().getUser().setCheck(0);

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
                        
                    }
                    else if(rq.equals("play again")){
                        synchronized(lock){
                            Users user = (Users) ois.readObject();
                            Handler temp=clientMap.get(user.getUsername());
                            if(temp_pair.getKey().equals(temp)){
                                temp_pair.getKey().getUser().setCheck(2);
                            }
                            if(temp_pair.getValue().equals(temp)){
                                temp_pair.getValue().getUser().setCheck(2);
                            }
                            if(temp_pair.getKey().getUser().getCheck()==2&&temp_pair.getValue().getUser().getCheck()==2){
                                temp_pair.getKey().oos.writeUTF("accept");
                                temp_pair.getKey().oos.writeObject(temp_pair.getKey().getUser());
                                temp_pair.getValue().oos.writeUTF("accept");
                                temp_pair.getValue().oos.writeObject(temp_pair.getValue().getUser());
                                temp_pair.getKey().getUser().setCheck(0);
                                temp_pair.getValue().getUser().setCheck(0);

                            }
                        }
                    }
                    else if(rq.equals("quit")){
                        synchronized(lock){
                            Users user = (Users) ois.readObject();
                            Handler temp=clientMap.get(user.getUsername());
                            if(temp_pair.getKey().equals(temp)){
                                temp_pair.getKey().getUser().setCheck(3);
                            }
                            if(temp_pair.getValue().equals(temp)){
                                temp_pair.getValue().getUser().setCheck(3);
                            }
                            if(temp_pair.getKey().getUser().getCheck()==3||temp_pair.getValue().getUser().getCheck()==3){
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
