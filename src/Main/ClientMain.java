/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;
import client.view.LoginView;
/**
 *
 * @author ----LaiNhuTung----
 */
public class ClientMain {
    public static void main(String[] args) {
        LoginView view=new LoginView();
        view.setVisible(true);
        view.setLocationRelativeTo(null);
    }
}
