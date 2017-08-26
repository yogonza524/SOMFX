/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.somfx;

//import com.core.controller.GrafoController;
//import com.core.controllers.SOMManager;
//import com.core.entity.Grafo;
import com.core.somcluster.HyperbolicFunction;
import com.core.somcluster.Matrix;
import com.core.somcluster.SOMCluster;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.lang.ArrayUtils;
import org.controlsfx.control.PopOver;

/**
 * FXML Controller class
 *
 * @author gonza
 */
public class SOMController implements Initializable {

    @FXML private AnchorPane base;
    
    @FXML private TitledPane t1;
    @FXML private Tab test_tab;
    @FXML private Tab train_tab;
    @FXML private Accordion ac;
    @FXML private Accordion act1;
    @FXML private Accordion act2;
    @FXML private Accordion act3;
    @FXML private TitledPane tact1;
    @FXML private TitledPane tact2;
    @FXML private TitledPane tact3;
    @FXML private CheckBox pesos_check;
    @FXML private ComboBox funcion_vecindad_combo;
    @FXML private TextField entradas_text;
    @FXML private TextField epocas_text;
    @FXML private TextField neuronas_text;
    @FXML private TextArea neuronas_test_text;
    @FXML private TextArea patrones_train_text;
    @FXML private TextArea patrones_test_text;
    @FXML private TextArea pesos_text;
    @FXML private Button entrenar_btn;
    @FXML private Button borrar_btn;
    @FXML private Button run_test_button;
    @FXML private Label pesos_label;
    @FXML private TextArea result_text;
    @FXML private TextArea salida_test_text;
    @FXML private TabPane tab;
    
    @FXML private MenuItem cargar_patrones_menuitem;
    @FXML private MenuItem cargar_pesos_menuitem;
    @FXML private MenuItem patrones_test_menuitem;
    @FXML private MenuItem nueva_simulacion_menuitem;
    @FXML private MenuItem ejecutar_entrenamiento_menuitem;
    @FXML private MenuItem ejecutar_test_menuitem;
    @FXML private MenuItem guardar_patrones_menuitem;
    @FXML private MenuItem guardar_pesos_menuitem;
    @FXML private MenuItem guardar_patrones_test_menuitem;
    @FXML private Label patrones_train_label;
    @FXML private Hyperlink question_train;
    
    private PopOver patronesEntrenamientoPopover;
    
    private SOMCluster som;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initConfig();
        initButtons();
        initListeners();
        initComboBoxes();
        initPopovers();
    }    

    private void initConfig() {
        this.ac.setExpandedPane(t1);
//        pesos_label.setVisible(false);
//        pesos_text.setVisible(false);
        pesos_check.setSelected(false);
        funcion_vecindad_combo.getSelectionModel().selectLast();
        this.act1.setExpandedPane(tact1);
        this.act2.setExpandedPane(tact2);
        this.act3.setExpandedPane(tact3);
    }

    private void initButtons() {
        entrenar_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                train();
            }
        });
        borrar_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                result_text.setText("");
                pesos_text.setText("");
                patrones_train_text.setText("");
                entradas_text.setText("");
                epocas_text.setText("");
                neuronas_text.setText("");
                test_tab.setDisable(true);
            }
        });
        run_test_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!patrones_test_text.getText().isEmpty()) {
                    double[][] patronesTest = crearArreglo(patrones_test_text.getText().replaceAll("\\s+", "")
                        .replaceAll("\\n+", "").split(";"),
                        salida_test_text
                );
                    String resultado = ejecutarTest(patronesTest);
                    salida_test_text.setText(resultado);
                }
                else{
                    salida_test_text.setText("Entrene la red primero");
                }
            }
        });
    }

    private void train() {
        if (neuronas_text.getText().isEmpty()) {
            String result = "Debe ingresar una cantidad de neuronas";
            result += "\nPor ejemplo: cantidad = 4, entonces la red contendra 4 neuronas en total";
            result_text.setText(result);
            return;
        }
        if(epocas_text.getText().isEmpty()){
            String result = "Debe ingresar una cantidad de epocas";
            result += "\nPor ejemplo: epocas = 5, entonces la red se estabilizara luego de 5 ciclos de entrenamiento";
            result_text.setText(result);
            return;
        }
        if (entradas_text.getText().isEmpty()) {
            String result = "Debe ingresar una cantidad de entradas para el entrenamiento";
            result += "\nPor ejemplo: entradas = 3, entonces [4,2,5] es un patron valido de entrenamiento";
            result_text.setText(result);
            return;
        }
        if (patrones_train_text.getText().isEmpty()) {
            String result = "Debe ingresar algun patron de entrenamiento";
            result += "\nPor ejemplo: patrones = [1,2,4],[6,1,3], entonces la red tendra 2 patrones de entrenamiento validos";
            result_text.setText(result);
            return;
        }
        if (funcion_vecindad_combo.getSelectionModel().getSelectedIndex() < 0) {
            String result = "Debe elegir si desea utilizar una funcion de vecindad";
            result_text.setText(result);
            return;
        }
        //Valores correctos
        Integer dimension = Integer.valueOf(entradas_text.getText());
        boolean formatoPatronesEntrenamiento = SOMManager.validarDimensionArreglo(dimension, patrones_train_text.getText());
        if (!formatoPatronesEntrenamiento) {
            String result = "Los patrones ingresados no tienen una dimension correcta";
            result += "\nPor ejemplo: 1.3,2.6 ; 5.6,22,55,66 (2 patrones) es incorrecto Dimension(p1) = 2 y Dimension(p2) = 4";
            result_text.setText(result);
            return;
        }

        if (!pesos_check.isSelected()) {
            boolean formatoPesos = SOMManager.validarDimensionArreglo(dimension, pesos_text.getText());
            if (!formatoPesos) {
                String result = "Los pesos ingresados no tienen una dimension correcta";
                result += "\nPor ejemplo: 1.8,3.6 ; 7.6,12,5,1 (2 patrones) es incorrecto Dimension(p1) = 2 y Dimension(p2) = 4";
                result_text.setText(result);
                return;
            }
        }

        //Correcto, realizar operaciones solicitadas
        Integer epocas = Integer.valueOf(epocas_text.getText());
        Boolean pesosAleatorios = pesos_check.isSelected();
        Boolean utilizarFuncionVecindad = funcion_vecindad_combo.getSelectionModel().getSelectedIndex() == 0;
        Integer clusters = Integer.valueOf(neuronas_text.getText());
        try {
            double[][] patrones = crearArreglo(patrones_train_text.getText().replaceAll("\\s+","").replaceAll("\\n+","").split(";"),
                    result_text);

            Platform.runLater(new Runnable() {
            @Override
            public void run() {
                crearCluster(dimension, 
                    epocas, 
                    pesosAleatorios, 
                    utilizarFuncionVecindad, 
                    clusters,
                    patrones,
                    (pesosAleatorios ? null : crearArreglo(pesos_text.getText().replaceAll("\\s+","").replaceAll("\\n+","").split(";"), result_text)));
                    test_tab.setDisable(false);
                    mostrarNeuronasTest(som, neuronas_test_text);
                }

                private void mostrarNeuronasTest(SOMCluster som, TextArea neuronas_test_text) {
                    double[][] W = som.getW();
                    String neuronas = Matrix.getMatrix(W);
                    neuronas_test_text.setText(neuronas);
                }
            });

        } catch (NumberFormatException e) {
//                    result_text.setText("Imposible formatear ese numero, verifiquelo\n" + e.getMessage().replaceAll("\\)", ")\n") + 
//                            "\n\n" + result_text.getText());
        }
    }
    
    private double[][] crearArreglo(String[] patrones, TextArea resultArea) throws NumberFormatException {
        int totalPatrones = patrones.length;
        int dimension = patrones[0].split(",").length;
        double[][] result = new double[totalPatrones][dimension];

        for (int i = 0; i < totalPatrones; i++) {
            String[] patron = patrones[i].split(",");
            double[] patronDouble = new double[dimension];
            for (int j = 0; j < patron.length; j++) {
                try {
                    patronDouble[j] = Double.valueOf(patron[j]);
                } catch (NumberFormatException e) {
                    resultArea.setText("Imposible formatear ese numero, verifiquelo\n" + 
                            e.getMessage()
                                    .replaceAll("\\)", ")\n")
                                    .replaceAll("For input string", "Cadena de entrada")
                                    .replaceAll("multiple points", "Puntos multiples")
                            +
                            ": " + patron[j] + "\n\n" + resultArea.getText());
                    throw new NumberFormatException("Imposible formatear ese numero, veriquelo");
                }
            }
            result[i] = patronDouble;
        }

        return result;
    }

    private void crearCluster(Integer dimension, Integer epocas, Boolean pesosAleatorios, Boolean utilizarFuncionVecindad, Integer clusters, double[][] patrones, double[][] pesos) {
        SOMCluster.SOMClusterBuilder somBuilder = new SOMCluster.SOMClusterBuilder()
                .dimension(dimension)
                .epochs(epocas)
                .clusters(clusters)
                ;
        if (utilizarFuncionVecindad) {
            somBuilder.neighborhoodFunction(new HyperbolicFunction())
                    .updateNeighborhood(true)
                    ;
        }
        if (!pesosAleatorios) {
            somBuilder.weights(pesos);
        }

        som = somBuilder.build();

        //Entrenamiento
        som.train(patrones);
        result_text.setText(som.getTrainingLOG()); //Muestro el entrenamiento

    }
    
    private void initListeners() {
        entradas_text.textProperty().addListener(new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    entradas_text.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        epocas_text.textProperty().addListener(new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    epocas_text.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        neuronas_text.textProperty().addListener(new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    neuronas_text.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        pesos_check.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    pesos_label.setVisible(true);
                    pesos_text.setVisible(true);
                }
                else{
                    pesos_label.setVisible(false);
                    pesos_text.setVisible(false);
                }
            }
        });
        base.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    Util.closeWindow(ac);
                }
            }
        });
        
        cargar_patrones_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    String patrones = cargarArrayDesdeArchivo();
                    patrones_train_text.setText(patrones);
                } catch (IOException ex) {
                    Logger.getLogger(SOMController.class.getName()).log(Level.SEVERE, null, ex);
                    result_text.setText("Excepcion capturada, verifique la pila: \n" + ex.getMessage()); 
                }
            }
        });
        cargar_pesos_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    String patrones = cargarArrayDesdeArchivo();
                    pesos_text.setText(patrones);
                } catch (IOException ex) {
                    Logger.getLogger(SOMController.class.getName()).log(Level.SEVERE, null, ex);
                    result_text.setText("Excepcion capturada, verifique la pila: \n" + ex.getMessage()); 
                }
            }
        });
        patrones_test_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    String patrones = cargarArrayDesdeArchivo();
                    patrones_test_text.setText(patrones);
                } catch (IOException ex) {
                    Logger.getLogger(SOMController.class.getName()).log(Level.SEVERE, null, ex);
                    result_text.setText("Excepcion capturada, verifique la pila: \n" + ex.getMessage()); 
                }
            }
        });
        nueva_simulacion_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                borrar(neuronas_text, epocas_text, entradas_text);
                borrar(patrones_train_text, pesos_text);
            }
        });
        ejecutar_entrenamiento_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tab.getSelectionModel().select(train_tab);
                train();
            }
        });
        ejecutar_test_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                test();
            }
        });
        
        guardar_patrones_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String patrones = formatArray(patrones_train_text.getText());
                tab.getSelectionModel().selectFirst();
                try {
                    guardarArrayAArchivo(patrones.replaceAll(";", ";\n"), result_text);
                } catch (IOException ex) {
                    result_text.setText("Excepcion capturada:\n" + ex.getMessage().replaceAll("\\)", "\\)\n") + result_text.getText());
                    Logger.getLogger(SOMController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        guardar_pesos_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String patrones = formatArray(pesos_text.getText());
                tab.getSelectionModel().selectFirst();
                try {
                    guardarArrayAArchivo(patrones.replaceAll(";", ";\n"), result_text);
                } catch (IOException ex) {
                    result_text.setText("Excepcion capturada:\n" + ex.getMessage().replaceAll("\\)", "\\)\n") + result_text.getText());
                    Logger.getLogger(SOMController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        guardar_patrones_test_menuitem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String patrones = formatArray(patrones_test_text.getText());
                tab.getSelectionModel().selectFirst();
                try {
                    guardarArrayAArchivo(patrones.replaceAll(";", ";\n"), result_text);
                } catch (IOException ex) {
                    result_text.setText("Excepcion capturada:\n" + ex.getMessage().replaceAll("\\)", "\\)\n") + result_text.getText());
                    Logger.getLogger(SOMController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        question_train.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
                patronesEntrenamientoPopover.show(question_train);   
            }
        });
    }

    private void initComboBoxes() {
        funcion_vecindad_combo.getItems().clear();
        
        funcion_vecindad_combo.getItems().add("Si");
        funcion_vecindad_combo.getItems().add("No");
    }
 
    private String cargarArrayDesdeArchivo() throws IOException{
        String result = "";
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
        File file = chooser.showOpenDialog(base.getScene().getWindow());
        if (file != null) {
          if (file.getName().endsWith(".txt")) {
              String path = file.getAbsolutePath();
              String array = Util.leerFichero(path);
              if (array != null && !array.isEmpty()) {
                  String[] values = array.replaceAll("\\s+", "").replaceAll("\\n+", "").split(";");
                  for(String v : values){
                      result += v + ";\n"; 
                  }
              }
          } else {
            Util.error("Extensión incorrecta", "Verifique la extension", file.getName() + " no tiene la extension correcta", act1);
          }
        }
        return result;
    }
    
    private String guardarArrayAArchivo(String array, TextArea resultado) throws IOException{
        String result = "";
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
        File file = chooser.showSaveDialog(base.getScene().getWindow());
        if (file != null) {
          file = new File(file.getAbsolutePath() + ".txt");
          String path = file.getAbsolutePath();
          try{
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                writer.print(array);
                writer.close();
          } catch (Exception e) { 
               resultado.setText("Problemas al almacenar el vector\n" + e.getMessage().replaceAll("\\)", "\n"));
          }
        }
        return result;
    }
    
    private void test(){
        tab.getSelectionModel().select(test_tab);
        if (som != null && som.getTrainingLOG() != null && !som.getTrainingLOG().isEmpty()) {
            String[] patrones = patrones_test_text.getText()
                    .replaceAll("\\s+", "") 
                    .replaceAll("\\n+", "")
                    .split(";");
            if (!patrones_test_text.getText().isEmpty()) {
                double[][] testPattern = crearArreglo(patrones, salida_test_text);
                String resultado = ejecutarTest(testPattern);
                salida_test_text.setText(resultado);
            }
            else{
                salida_test_text.setText("Ingrese un patron para testear");
            }
        }
        else{
            salida_test_text.setText("Primero debe entrenar la red");
        }
    }
    
    private String formatArray(String sinFormato){
        return sinFormato
                .replaceAll("\\s+", "") 
                .replaceAll("\\n+", "");
    }
    
    private String ejecutarTest(double[][] patrones){
        String result = ""; 
        if (patrones != null) {
            result = "Resultado del test\n"; 
            for(double[] patron : patrones){
                int neurona = som.test(patron);
                result += "\ntest(" + ArrayUtils.toString(patron).replaceAll("\\{", "").replaceAll("\\}", "") + ") activa la neurona " 
                        + neurona + " "
                        + "[" + ArrayUtils.toString(som.getW()[neurona]).replaceAll("\\{", "").replaceAll("\\}", "") + "]"
                        ;
            }
        }
        return result;
    }
    
    private void borrar(TextArea... text){
        for(TextArea t : text){
            t.setText("");
        }
    }
    
    private void borrar(TextField... fields){
        for(TextField t : fields){
            t.setText("");
        }
    }

    private void initPopovers() {
        patronesEntrenamientoPopover = new PopOver();
        
        BorderPane b = new BorderPane();
        b.setPadding(new Insets(10, 20, 10, 20));
        VBox vbox = new VBox();
        Label title = new Label("Configuracion correcta");
        Label content = new Label("Aqui un ejemplo de configuracion");
        Label content1 = new Label("de patrones de entrenamiento de dimension 2");
        content.setPadding(new Insets(5, 0, 0, 0));
        content1.setPadding(new Insets(5, 0, 0, 0));
        content.setWrapText(true);
        vbox.getChildren().addAll(title, content, content1, new ImageView(new Image("/img/config1.png")));
        b.setCenter(vbox);
        
        patronesEntrenamientoPopover.setContentNode(b);
    }
}
