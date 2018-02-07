package Program;

import gnu.io.*;
import gnu.io.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import jssc.*;

public class Controller implements Initializable {
    @FXML
    public ComboBox<String> baudRateList;
    @FXML
    public ComboBox<String> comPortList;
    @FXML
    public TextField commandFld;
    @FXML
    public TextArea outputFld;
    @FXML
    public Button sendBtn;
    @FXML
    public Button connectBtn;

    private BufferedReader inputStream;
    private OutputStream outputStream;
    private OutputStream output;
    public static SerialPort serialPort;
    private String PortName = null;
    private String BaudRate = null;

    private static final String PORT_NAMES[] = {
            "COM3", // Windows
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comPortList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> PortName = newValue);
        baudRateList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> BaudRate = newValue);
        ObservableList<String> baudList = FXCollections.observableArrayList("4800","9600","19200","115200");
        baudRateList.setItems(baudList);

    }

    //send sudah bisa
    public void sendEvent(javafx.event.ActionEvent actionEvent) {
        if(PortName != null){
            try{
                output.write(commandFld.getText().getBytes());
                output.flush();
                commandFld.setText("");
            }catch (Exception e){

            }
        }
    }

    //cari port
    public void portSearch(javafx.event.ActionEvent actionEvent){
        java.util.Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()){
            CommPortIdentifier CommPortNames = ports.nextElement();
            comPortList.getItems().add(CommPortNames.getName());
        }
    }

    //koneksi
    public void connEvent(javafx.event.ActionEvent actionEvent) throws Exception {
        if(PortName!=null&&BaudRate!=null){
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(PortName);
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            try{
                serialPort = (gnu.io.SerialPort) commPort;
                serialPort.setSerialPortParams(Integer.valueOf(BaudRate),8,1,0);
                inputStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                outputStream = serialPort.getOutputStream();
                output = outputStream;
                serialPort.addEventListener(new gnu.io.SerialPortEventListener() {
                    @Override
                    public void serialEvent(gnu.io.SerialPortEvent event) {
                        try {
                            if(event.getEventType()== gnu.io.SerialPortEvent.DATA_AVAILABLE){
                                String data = inputStream.readLine();
                                outputFld.appendText(data+System.lineSeparator());
                            }
                        }catch (IOException e){

                        }

                    }
                });
                serialPort.notifyOnDataAvailable(true);
            }catch (Exception e){

            }
        }else{
            new Alert(Alert.AlertType.ERROR,"You haven't select any COM port or baud rate or both").showAndWait();
        }
    }
}




