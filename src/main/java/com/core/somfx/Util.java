/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.somfx;

import com.core.somcluster.*;
import com.core.somfx.SOMController;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.joda.time.DateTime;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author gonza
 */
public class Util {
    
    private static SOMController mainController;
    private static Stage principal;
    
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = 
        new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

    public static String encrypt(String valueToEnc) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encValue);
        return encryptedValue;
    }

    public static String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

    public static Stage getPrincipal() {
        return principal;
    }

    public static void setPrincipal(Stage principal) {
        Util.principal = principal;
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        
        return (double) tmp / factor;
    }
    
    public static String now(){
        DateTime n = new DateTime(new Date());
        return n.getDayOfMonth() + "/" + n.getMonthOfYear() + "/"
                + n.getYear() + " " + (n.getHourOfDay() < 10 ? "0" + n.getHourOfDay() : n.getHourOfDay())
                + ":" + (n.getMinuteOfHour() < 10? "0" + n.getMinuteOfHour(): n.getMinuteOfHour());
    }
    
    public static String dia(Date d){
        String result = "Desconocido";
        if (d != null) {
            DateTime date = new DateTime(d);
            switch(date.getDayOfWeek()){
                case 7: result = "Domingo"; break;
                case 1: result = "Lunes"; break;
                case 2: result = "Martes"; break;
                case 3: result = "Miercoles"; break;
                case 4: result = "Jueves"; break;
                case 5: result = "Viernes"; break;
                case 6: result = "Sabado"; break;
            }
        }
        return result;
    }
    
    public static void alert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initModality(Modality.WINDOW_MODAL);

        alert.showAndWait();
    }
    
    public static void alert(String title, String header, String content, Window owner){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(owner);

        alert.showAndWait();
    }
    
    public static void alert(String title, String header, String content, Node c){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initModality(Modality.WINDOW_MODAL);
        if (c != null) {
            Scene s = (Scene)c.getScene();
            Window owner = s.getWindow();
            alert.initOwner(owner);
        }

        alert.showAndWait();
    }
    
    public static void error(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        alert.showAndWait();
    }
    
    public static void error(String title, String header, String content, Node c){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Scene s = (Scene)c.getScene();
        alert.initOwner(s.getWindow());
        
        alert.showAndWait();
    }
    

    public static void showWindow(String title, String fxml, Class clazz, boolean isModal){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                s.setTitle(title);
                s.setResizable(false);
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                s.getIcons().add(new Image("/img/meka.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static void showWindow(String title, String fxml, Class clazz, boolean isModal, boolean isClosable){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                s.setTitle(title);
                s.setResizable(false);
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
//                    s.initModality(Modality.NONE);
                    s.initStyle(StageStyle.DECORATED);
                }
                if (isClosable) {
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                s.getIcons().add(new Image("/img/meka.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static void showMainWindowMaximized(String title, String fxml, Class clazz, boolean isModal, boolean isClosable){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                saveState(loader);
                s.setTitle(title);
                s.getScene().getStylesheets().add("/styles/Styles.css");
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
//                    s.initModality(Modality.NONE);
                    s.initStyle(StageStyle.DECORATED);
                }
                if (isClosable) {
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();

                s.setX(bounds.getMinX());
                s.setY(bounds.getMinY());
                s.setWidth(bounds.getWidth());
                s.setHeight(bounds.getHeight());
                s.getIcons().add(new Image("/img/meka.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static void showWindow(String title, String fxml, Class clazz, boolean isModal, boolean isClosable, boolean isResizable){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                s.setTitle(title);
                s.setResizable(isResizable);
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
//                    s.initModality(Modality.NONE);
                    s.initStyle(StageStyle.DECORATED);
                }
                if (isClosable) {
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                s.getIcons().add(new Image("/img/meka.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static void showMainWindow(String title, String fxml, Class clazz, boolean isModal, boolean isClosable, boolean isResizable){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                s.setTitle(title);
                s.setResizable(isResizable);
                s.getScene().getStylesheets().add("/styles/Styles.css");
                saveState(loader);
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
//                    s.initModality(Modality.NONE);
                    s.initStyle(StageStyle.DECORATED);
                }
                if (isClosable) {
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                s.getIcons().add(new Image("/img/meka.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    //hcp
    private static final String[] a = new String[]{
        "T37B96IK4E",
        "K4LAIGM8IO",
        "AUVFRHWHQQ",
        "JZ1DIC7IXK",
        "BH33RZJC36",
        "BBJ6RV4MI2",
        "JTHGI7STCG",
        "TJV29JQOVQ",
        "JZ1DIC7IXK",
        "8UOIRWL94A",
        "1W9F0S0650",
        "2CX6058AWC",
        "BBJ6RV4MI2",
        "BBJ6RV4MI2",
        "JZ1DIC7IXK",
        "1W9F0S0650",
        "TJV29JQOVQ",
        "K4LAIGM8IO",
        "2CX6058AWC",
        "BBJ6RV4MI2",
        "8UOIRWL94A",
        "TEB59FBZAM",
        "JTHGI7STCG",
        "2IH309N0HG",
        "2IH309N0HG",
        "2O100E1Q2K",
        "2CX6058AWC",
        "BH33RZJC36",
        "SXNE923UJA"
    };
    
    private static final String[] b = new String[]{
        "TJV29JQOVQ",
        "2CX6058AWC",
        "2CX6058AWC",
        "BH33RZJC36"
    };
    
    //u
    private static final String[] d = new String[]{
        "KQSYIY92V4",
        "2CX6058AWC",
        "21TC0WEVQ4",
        "2CX6058AWC",
        "BBJ6RV4MI2",
        "BS6XR8CR9E",
        "JTHGI7STCG",
        "I4EDIVAZW8",
        "908FR0ZYPE",
        "R30E9L7BHY"
    };
    
    //pw
    private static final String[] c = new String[]{
        "908FR0ZYPE",
        "QXGH9GSLWU",
        "TEB59FBZAM",
        "2CX6058AWC",
        "JZ1DIC7IXK",
        "KA57IL0Y3S",
        "JTHGI7STCG",
        "2IH309N0HG",
        "SXNE923UJA",
        "JZ1DIC7IXK",
        "JTHGI7STCG",
        "KA57IL0Y3S",
        "2CX6058AWC",
        "BH33RZJC36",
        "KQSYIY92V4",
        "KL91ITUDA0",
        "BBJ6RV4MI2",
        "JTHGI7STCG",
        "1W9F0S0650",
        "JTHGI7STCG",
        "BBJ6RV4MI2",
        "1W9F0S0650",
        "K4LAIGM8IO",
        "2CX6058AWC",
        "BBJ6RV4MI2",
        "SXNE923UJA",
        "JZ1DIC7IXK",
        "2IH309N0HG",
        "JZ1DIC7IXK",
        "2IH309N0HG",
        "TEB59FBZAM",
        "JZ1DIC7IXK",
        "BH33RZJC36",
        "JTHGI7STCG",
        "SXNE923UJA",
        "JTHGI7STCG"
    };

    private static String gkey = 
        "MIIEpQIBAAKCAQEA3U+R4ygDChkgYJAQfCbNhsOspKH/rjW317qPR5zwFrYwTAjt" +
        "3Be3Do6H3XHitEiqhA+HSugTPeyg2w7MWa68nLRCcnB4fgeS25F58KVKeZniYg9g" +
        "TdM+svggApVjC0p5pgbWRC9bm+gjv4koQU2FidfywYiQDiO5aZfFgWymplOykkM/" +
        "zIenaM14REJ5+5nocAB8dg4Vd/7Q3aDnEb+euswct3OxYDB4D2NLaGZDxZFfz7xh" +
        "1YahuP8TXqP3wkbp17E/TKSzKKKAfewyC7sAakYpIUBOPIku/StZ1Jq4K5e7lCb3";

    public static String getGkey() {
        return gkey;
    }
    
    public static String[] getA() {
        return a;
    }

    public static String[] getB() {
        return b;
    }
    
    public static String[] getD() {
        return d;
    }

    public static String[] getC() {
        return c;
    }
    

    public static String fecha(Date fechaAdquisicion) {
        String result = "Sin especificar";
        if (fechaAdquisicion != null) {
            DateTime d = new DateTime(fechaAdquisicion);
            result = d.getDayOfMonth() + "/" + d.getMonthOfYear() + "/" + d.getYear();
        }
        return result;
    }
    
    public static String fechaLarga(Date fecha) {
        String result = "Sin especificar";
        if (fecha != null) {
            DateTime d = new DateTime(fecha);
            result = d.getDayOfMonth() + "/" + d.getMonthOfYear() + "/" + d.getYear()
                    + " " + (d.getHourOfDay() < 10 ? "0" : "") + d.getHourOfDay() + ":" + (d.getMinuteOfHour() < 10 ? "0" : "" ) + 
                    d.getMinuteOfHour() + ":" + (d.getSecondOfMinute() < 10 ? "0" : "") +
                    d.getSecondOfMinute()
                    ;
        }
        return result;
    }
    
    public static void closeWindow(Node c){
        Stage stage = (Stage) c.getScene().getWindow();
        stage.close();
    }
    
    public static String fechaYhora(Date d){
        DateTime n = new DateTime(d);
        return n.getDayOfMonth() + "/" + n.getMonthOfYear() + "/"
                + n.getYear() + " " + (n.getHourOfDay() < 10 ? "0" + n.getHourOfDay() : n.getHourOfDay())
                + ":" + (n.getMinuteOfHour() < 10? "0" + n.getMinuteOfHour(): n.getMinuteOfHour());
    }
    
    
    public static void saveState(FXMLLoader loader) throws IOException {
        if (loader.getController() instanceof SOMController) {
            SOMController mc = loader.getController();
            Util.setMainController(mainController);
        }
    }
    
    public static void exception(Exception ex,String title, String header, String content, Node c){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (c != null) {
            alert.initOwner(c.getScene().getWindow());
        }

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("La excepcion caturada fue:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static SOMController getMainController() {
        return mainController;
    }

    public static void setMainController(SOMController mainController) {
        Util.mainController = mainController;
    }
    
    public static String leerFichero(String dir) throws FileNotFoundException, IOException{
        String result = "";
        FileReader fr = new FileReader(dir);
        BufferedReader bf = new BufferedReader(fr);
        String line = "";
        while ((line = bf.readLine()) != null) {
            result = result + line;
        }
        return result;
    }
}
