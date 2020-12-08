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
import model.ResultGame;

/**
 *
 * @author tieng
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
    HashMap<String, Handler> useronline;
    Object lock;
    ArrayList<Pair<Handler, Handler>> pairs;

    public ServerControl() throws MessagingException {
        lock = new Object();
        db = new DBAccess();
        openConnection();
        useronline = new HashMap<>();
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
                    useronline.put(u.getUsername(), handler);

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
                if (!db.signup(u)) {
                    System.out.println(u);
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

                    checkEmail.setPass(newpass);
                    System.out.println(checkEmail.getUsername() + checkEmail.getPass());
                    boolean UpdatePassacount = new DBAccess().UpdatePassacount(checkEmail);

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
            for (Map.Entry<String, Handler> entry : useronline.entrySet()) {
                Handler value = entry.getValue();
                value.getOos().writeUTF("online");
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
            Pair<Handler, Handler> dualUser = null;
            while (true) {
                try {
                    String rq = ois.readUTF();
                    System.out.println(rq);
                    if (rq.equals("log out")) {
                        Users u = (Users) ois.readObject();
                        db.logOut(u);
                        useronline.remove(u.getUsername());
                        updateOnlineUsers();
                        break;
                    } else if (rq.equals("bxhScore")) {
                        synchronized (lock) {
                            Users u = (Users) ois.readObject();
                            ArrayList<Users> list = db.getListRank();
                            System.out.println(list.size());
                            useronline.get(u.getUsername()).getOos().writeUTF("bxhScore");
                            useronline.get(u.getUsername()).getOos().writeObject(list);
                            useronline.get(u.getUsername()).getOos().flush();
                        }
                    } else if (rq.equals("bxhTime")) {
                        synchronized (lock) {
                            Users u = (Users) ois.readObject();
                            ResultGame rg = new ResultGame();
                            ArrayList<ResultGame> list = db.getListTime();
                            useronline.get(u.getUsername()).getOos().writeUTF("bxhTime");
                            useronline.get(u.getUsername()).getOos().writeObject(list);
                            useronline.get(u.getUsername()).getOos().flush();
                        }
                    } else if (rq.equals("challenge")) {
                        synchronized (lock) {
                            Users u1 = (Users) ois.readObject();
                            Users u2 = (Users) ois.readObject();

                            useronline.get(u2.getUsername()).getOos().writeUTF("challenge");
                            useronline.get(u2.getUsername()).getOos().writeObject(u2);
                            useronline.get(u2.getUsername()).getOos().writeObject(u1);
                        }

                    } else if (rq.equals("accept")) {

                        synchronized (lock) {
                            Set<Integer> set = new HashSet<>();
                            Random generator = new Random();
                            List<Integer> a = new ArrayList<>();
                            int size = 0;
                            while (true) {
                                Integer value = generator.nextInt(24) + 1;
                                set.add(value);
                                if (set.size() > size) {
                                    a.add(value);
                                    size = set.size();
                                }
                                if (set.size() == 24) {
                                    break;
                                }
                            }
                            Random generator2 = new Random();
                            Integer value2 = generator.nextInt(2) + 1;
                            Users u2 = (Users) ois.readObject();
                            Users u1 = (Users) ois.readObject();
                            useronline.get(u2.getUsername()).getOos().writeUTF("begin");
                            useronline.get(u2.getUsername()).getOos().writeObject(u2);
                            useronline.get(u2.getUsername()).getOos().writeObject(a);
                            useronline.get(u2.getUsername()).getOos().writeObject(value2);
                            useronline.get(u1.getUsername()).getOos().writeUTF("begin");
                            useronline.get(u1.getUsername()).getOos().writeObject(u1);
                            useronline.get(u1.getUsername()).getOos().writeObject(a);
                            useronline.get(u1.getUsername()).getOos().writeObject(value2);
                            pairs.add(new Pair<>(useronline.get(u1.getUsername()), useronline.get(u2.getUsername())));
                            db.updateStatus(u1, 2);
                            db.updateStatus(u2, 2);
                            updateOnlineUsers();
                        }
                        // updateOnlineUsers();
                    } else if (rq.equals("not accept")) {
                        synchronized (lock) {
                            Users thisu = (Users) ois.readObject();
                            Users u = (Users) ois.readObject();
                            useronline.get(thisu.getUsername()).getOos().writeUTF("not accept");
                            useronline.get(thisu.getUsername()).getOos().writeObject(u);

                        }

                    } else if (rq.equals("Calculate")) {
                        synchronized (lock) {
                            Users user = (Users) ois.readObject();
                            String time = ois.readUTF();
                            Handler temp = useronline.get(user.getUsername());

                            for (Pair<Handler, Handler> i : pairs) {
                                if (i.getKey().getUser().getUsername().equals(temp.getUser().getUsername())) {
                                    i.getKey().getUser().setFi_time(time);
                                    dualUser = i;
                                    i.getKey().getUser().setCheck(1);
                                    System.out.println("1");
                                    break;
                                }
                                if (i.getValue().getUser().getUsername().equals(temp.getUser().getUsername())) {
                                    i.getValue().getUser().setFi_time(time);
                                    dualUser = i;
                                    i.getValue().getUser().setCheck(1);
                                    System.out.println("2");
                                    break;
                                }
                            }

//                            }
                            if (dualUser.getKey().getUser().getCheck() == 1 && dualUser.getValue().getUser().getCheck() == 1) {
                                String t1 = dualUser.getKey().getUser().getFi_time();
                                String t2 = dualUser.getValue().getUser().getFi_time();
                                System.out.println("vao");
                                String[] arStr = t1.split("\\:");
                                String[] arStr2 = t2.split("\\:");
                                for (int i = 0; i < 4; i++) {
                                    if (Integer.parseInt(arStr[i]) > Integer.parseInt(arStr2[i])) {

                                        dualUser.getValue().oos.writeUTF("result");
                                        dualUser.getValue().oos.writeObject("thang");
                                        dualUser.getKey().oos.writeUTF("result");
                                        dualUser.getKey().oos.writeObject("thua");
                                        db.updatePoints(dualUser.getKey().getUser(), 1);
                                        int t = db.insertResult(dualUser.getValue().getUser(), dualUser.getKey().getUser());
                                        System.out.println(t);
                                        break;
                                    } else if (Integer.parseInt(arStr[i]) < Integer.parseInt(arStr2[i])) {
                                        dualUser.getKey().oos.writeUTF("result");
                                        dualUser.getKey().oos.writeObject("thang");
                                        dualUser.getValue().oos.writeUTF("result");
                                        dualUser.getValue().oos.writeObject("thua");
                                        db.updatePoints(dualUser.getKey().getUser(), 1);
                                        int t = db.insertResult(dualUser.getKey().getUser(), dualUser.getValue().getUser());
                                        System.out.println(t);
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (rq.equals("play again")) {
                        synchronized (lock) {
                            Set<Integer> set = new HashSet<>();
                            Random generator = new Random();
                            List<Integer> a = new ArrayList<>();
                            int size = 0;
                            while (true) {
                                Integer value = generator.nextInt(24) + 1;
                                set.add(value);
                                if (set.size() > size) {
                                    a.add(value);
                                    size = set.size();
                                }
                                if (set.size() == 24) {
                                    break;
                                }
                            }
                            Random generator2 = new Random();
                            Integer value2 = generator.nextInt(2) + 1;
                            Users user = (Users) ois.readObject();
                            Handler temp = useronline.get(user.getUsername());
                            if (dualUser.getKey().equals(temp)) {
                                dualUser.getKey().getUser().setCheck(2);
                            }
                            if (dualUser.getValue().equals(temp)) {
                                dualUser.getValue().getUser().setCheck(2);
                            }
                            if (dualUser.getKey().getUser().getCheck() == 2 && dualUser.getValue().getUser().getCheck() == 2) {
                                dualUser.getKey().oos.writeUTF("begin");
                                dualUser.getKey().oos.writeObject(dualUser.getKey().getUser());
                                dualUser.getKey().oos.writeObject(a);
                                dualUser.getKey().oos.writeObject(value2);
                                dualUser.getValue().oos.writeUTF("begin");
                                dualUser.getValue().oos.writeObject(dualUser.getValue().getUser());
                                dualUser.getValue().oos.writeObject(a);
                                dualUser.getValue().oos.writeObject(value2);
                                dualUser.getKey().getUser().setCheck(0);
                                dualUser.getValue().getUser().setCheck(0);
                            }
                            if (dualUser.getKey().getUser().getCheck() == 3) {
                                db.updateStatus(dualUser.getValue().getUser(), 1);
                                updateOnlineUsers();
                            } else if (dualUser.getValue().getUser().getCheck() == 3) {
                                db.updateStatus(dualUser.getKey().getUser(), 1);
                                updateOnlineUsers();
                            }
                        }
                    } else if (rq.equals("quit")) {
                        System.out.println(lock);
                        synchronized (lock) {
                            Users user = (Users) ois.readObject();
                            Handler temp = useronline.get(user.getUsername());
                            for (Pair<Handler, Handler> i : pairs) {
                                if (i.getKey().equals(temp)) {
                                    i.getKey().getUser().setCheck(3);
                                    if (i.getValue().getUser().getCheck() != 3) {
                                        i.getValue().oos.writeUTF("not accept");
                                        i.getValue().oos.writeObject(i.getKey().getUser());
                                        db.updateStatus(i.getValue().getUser(), 1);
                                        updateOnlineUsers();
                                        i.getValue().getUser().setCheck(3);
                                    }
                                    db.updateStatus(i.getKey().getUser(), 1);
                                    updateOnlineUsers();
                                    break;
                                }
                                if (i.getValue().equals(temp)) {
                                    i.getValue().getUser().setCheck(3);
                                    if (i.getKey().getUser().getCheck() != 3) {
                                        i.getKey().oos.writeUTF("not accept");
                                        i.getKey().oos.writeObject(i.getValue().getUser());
                                        db.updateStatus(i.getKey().getUser(), 1);
                                        updateOnlineUsers();
                                        i.getKey().getUser().setCheck(3);
                                    }
                                    db.updateStatus(i.getValue().getUser(), 1);
                                    updateOnlineUsers();
                                    break;
                                }
                            }
                            for (Pair<Handler, Handler> i : pairs) {
                                if (i.getValue().getUser().getCheck() == 3 && i.getKey().getUser().getCheck() == 3) {
                                    pairs.remove(i);
                                    break;
                                }
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
