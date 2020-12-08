/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import client.control.ClientControl;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.RootPaneUI;
import jdk.nashorn.internal.scripts.JO;
import model.Users;
import server.ServerControl;


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

public class ViewGameFrm extends JFrame {

    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;    
    private Image image;
    private MyButton lastButton;
    private int width, height; 
    private JButton sample; 
    private JLabel timeLabel= new JLabel("00:00:00:00");
    HashMap<Integer,MyButton>hashmap= new HashMap<Integer, MyButton>();
//    ClientControl control = new ClientControl();
    Icon samicon1;
    
    private Timer timer;
    
    private List<MyButton> buttons;
    private List<Point> solution;

    private final int NUMBER_OF_BUTTONS = 25;
    private final int DESIRED_WIDTH = 400;

    
    
    public ViewGameFrm(Users u, ObjectInputStream ois, ObjectOutputStream oos, List<Integer> a, Integer img) {
        String imgg= String.valueOf(img);
        samicon1 = new ImageIcon("src/Image/"+imgg+".jpg");
        initUI(u, ois, oos, a, img);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                try {
                    oos.writeUTF("end match");
                    oos.writeObject(u);
                    oos.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ViewGameFrm.class.getName()).log(Level.SEVERE, null, ex);
                }
//                control.sendData("end match", u);
            }
        });
    }

    private void initUI(Users u, ObjectInputStream ois, ObjectOutputStream oos, List<Integer> a, Integer img) {
        
        solution = new ArrayList<>();
        
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(0, 3));
        solution.add(new Point(0, 4));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(1, 3));
        solution.add(new Point(1, 4));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(2, 3));
        solution.add(new Point(2, 4));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));
        solution.add(new Point(3, 3));
        solution.add(new Point(3, 4));
        solution.add(new Point(4, 0));
        solution.add(new Point(4, 1));
        solution.add(new Point(4, 2));
        solution.add(new Point(4, 3));
        solution.add(new Point(4, 4));
        sample=new JButton(samicon1);  
        
        buttons = new ArrayList<>();

        panel = new JPanel();
        
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(5, 5, 0, 0));
        panel.setBounds(500, 800, 500, 800);

        try {
            source = loadImage(img);
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h,
                    BufferedImage.TYPE_INT_ARGB);

        } catch (IOException ex) {
            Logger.getLogger(ViewGameFrm.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);
        // sample
        JLabel ttlabel= new JLabel("Ảnh mẫu:");
        Image image2 = createImage(new FilteredImageSource(resized.getSource(), new CropImageFilter(0, 0, 400, 400)));      
        this.sample = new MyButton(image2);        
        JPanel panel2 = new JPanel();
        panel2.add(ttlabel, BorderLayout.NORTH);
        panel2.add(this.sample, BorderLayout.CENTER);
        add(panel2, BorderLayout.EAST);
        //end sample
        
        // timer   
     //   timeLabel = new JLabel("00:00:00:00");
        timeLabel.setFont(new Font("Arial", 1, 20));
        JPanel panel3 = new JPanel();
        panel3.add(timeLabel, BorderLayout.CENTER);
        add(panel3, BorderLayout.NORTH);
        timer = new Timer(10, new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                timeLabel.setText(nextTime(timeLabel));
            }
        });
        timer.start();
        //end timer
        
        JButton test = new JButton("cheat");
        test.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int w=win(img);
                System.out.println(timeLabel.getText());
                if(w==1){
                    try {    
                        oos.writeUTF("Calculate");
                        oos.writeObject(u);
                        oos.writeUTF(timeLabel.getText());
                        oos.flush();
//                        String rp= ois.readUTF();
//                        if(rp.equals("result")){
//                            String kq;
//                            try {
//                                kq = (String)ois.readObject();
//                                if(kq.equals("YOU WIN")){
//                                    int x=JOptionPane.showConfirmDialog(rootPane,kq+
//                                        " .Bạn muốn chơi lại không?" ,"Thông Báo",JOptionPane.YES_NO_OPTION);
//                                    if(x==0){
//                                        oos.writeUTF("play again");
//                                        oos.writeObject(u);
//                                        oos.flush();
//                                        
//                                    }else if(x==1){
//                                        oos.writeUTF("end match");
//                                        oos.writeObject(u);
//                                        oos.flush();
//                                    }
//                                }else if(kq.equals("YOU LOSE")){
//                                    JOptionPane.showMessageDialog(rootPane, kq);
//                                }
//                            } catch (ClassNotFoundException ex) {
//                                Logger.getLogger(ViewGameFrm.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                            
//                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ViewGameFrm.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                }
            }
        });
        add(test, BorderLayout.SOUTH);
        
        int count =1;
        for (int i = 0; i < 5; i++) {

            for (int j = 0; j < 5; j++) {

                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 5, i * height / 5,
                                (width / 5), height / 5)));
                
                MyButton button = new MyButton(image);
                button.putClientProperty("position", new Point(i, j));
                hashmap.put(count, button);
                count++;
                
                if (i == 4 && j == 4) {
                    lastButton = new MyButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
//                    buttons.add(button);
                }
            }
        }
        for(int i=0; i<a.size();i++){
            MyButton bt= hashmap.get(a.get(i));
            buttons.add(bt);
        }
//        Collections
//        Collections.shuffle(buttons);
        buttons.add(lastButton);

        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {

            MyButton btn = buttons.get(i);
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction());
        }
        
        pack();
        setTitle("Puzzle: "+u.getUsername());
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private void shuffleArray(int[] array)
        {
            int index;
            Random random = new Random();
            for (int i = array.length - 1; i > 0; i--)
            {
                index = random.nextInt(i + 1);
                if (index != i)
                {
                    array[index] ^= array[i];
                    array[i] ^= array[index];
                    array[index] ^= array[i];
                }
            }
            for (int i = array.length - 1; i > 0; i--)
            {
                System.out.println(array[i]);
            }
        }
    
    private int getNewHeight(int w, int h) {

        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private BufferedImage loadImage(int img) throws IOException {
        String imgg= String.valueOf(img);
        BufferedImage bimg = ImageIO.read(new File("src/Image/"+imgg+".jpg"));

        return bimg;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width,
            int height, int type) throws IOException {

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    private class ClickAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {

            int lidx = 0;
            
            for (MyButton button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }

            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if ((bidx - 1 == lidx) || (bidx + 1 == lidx)
                    || (bidx - 5 == lidx) || (bidx + 5 == lidx)) {
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        private void updateButtons() {

            panel.removeAll();

            for (JComponent btn : buttons) {

                panel.add(btn);
            }

            panel.validate();
        }
    }

    private void checkSolution() {

        List<Point> current = new ArrayList<>();

        for (JComponent btn : buttons) {
            current.add((Point) btn.getClientProperty("position"));
        }

        if (compareList(solution, current)) {
            timer.stop();
            JOptionPane.showMessageDialog(rootPane, "Bạn đã hoàn thành trong "+ this.timeLabel.getText()+" !",
                    "Chúc mừng", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private int win(Integer img){
            panel.removeAll();
            panel.setBorder(BorderFactory.createLineBorder(Color.gray));
            panel.setLayout(new GridLayout(5, 5, 0, 0));
            buttons.clear();
            
            try {
                source = loadImage(img);
                int h = getNewHeight(source.getWidth(), source.getHeight());
                resized = resizeImage(source, DESIRED_WIDTH, h,
                        BufferedImage.TYPE_INT_ARGB);
                
            } catch (IOException ex) {
                Logger.getLogger(ViewGameFrm.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            
            for (int i = 0; i < 5; i++) {
                
                for (int j = 0; j < 5; j++) {
                    
                    image = createImage(new FilteredImageSource(resized.getSource(),
                            new CropImageFilter(j * width / 5, i * height / 5,
                                    (width / 5), height / 5)));
                    
                    MyButton button = new MyButton(image);
                    button.putClientProperty("position", new Point(i, j));
                    
                    if (i == 4 && j == 4) {
                        lastButton = new MyButton();
                        lastButton.setBorderPainted(false);
                        lastButton.setContentAreaFilled(false);
                        lastButton.setLastButton();
                        lastButton.putClientProperty("position", new Point(i, j));
                    } else {
                        buttons.add(button);
                    }
                }
            }
            buttons.add(lastButton);
            
            for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
                
                MyButton btn = buttons.get(i);
                panel.add(btn);
                btn.setBorder(BorderFactory.createLineBorder(Color.gray));
                btn.addActionListener(new ClickAction());
            }
            add(panel);
            
            timer.stop();
            
            JOptionPane.showMessageDialog(rootPane, "Bạn đã hoàn thành trong "+ this.timeLabel.getText()+" !",
                    "Chúc mừng", JOptionPane.INFORMATION_MESSAGE);
//            System.out.println(timeLabel.getText());
            return 1;
    }

    public static boolean compareList(List ls1, List ls2) {
        
        return ls1.toString().contentEquals(ls2.toString());
    }

    
    public String nextTime(JLabel lb) {
		String str[] = lb.getText().split(":");
		int tt = Integer.parseInt(str[3]);
		int s = Integer.parseInt(str[2]);
		int m = Integer.parseInt(str[1]);
		int h = Integer.parseInt(str[0]);
		String kq = "";
		int sum = tt + s * 100 + m * 60 * 100 + h * 60 * 60 * 100 + 1;
		if (sum % 100 > 9)
			kq = ":" + sum % 100 + kq;
		else
			kq = ":0" + sum % 100 + kq;
		sum /= 100;
		
		if (sum % 60 > 9)
			kq = ":" + sum % 60 + kq;
		else
			kq = ":0" + sum % 60 + kq;
		sum /= 60;
		
		if (sum % 60 > 9)
			kq = ":" + sum % 60 + kq;
		else
			kq = ":0" + sum % 60 + kq;
		sum /= 60;
		if (sum > 9)
			kq = sum + kq;
		else
			kq = "0" + sum +kq;
		return kq;
	}
    
//    public static void main(String[] args) {
//
//        EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                ViewGameFrm puzzle = new ViewGameFrm(ClientControl control);
//                puzzle.setVisible(true);
//            }
//        });
//    }
}
