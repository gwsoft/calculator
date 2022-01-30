package gw.prp.calculator.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gw.prp.calculator.main.CalcApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CalcController implements AutoCloseable {
  private CalcApp calcApp;
  
  @FXML private Button digit0;
  @FXML private Button digit1;
  @FXML private Button digit2;
  @FXML private Button digit3;
  @FXML private Button digit4;
  @FXML private Button digit5;
  @FXML private Button digit6;
  @FXML private Button digit7;
  @FXML private Button digit8;
  @FXML private Button digit9;
  @FXML private Button btnAC;
  @FXML private Button btnSign;
  @FXML private Button btnPerc;
  @FXML private Button btnDiv;
  @FXML private Button btnMult;
  @FXML private Button btnMinus;
  @FXML private Button btnPlus;
  @FXML private Button btnDot;
  @FXML private Button btnResult;
  
  @FXML private TextField field;
  
  private String host;                                  // adres servera
  private int port;                                     // port servera
  private Socket socket;                                // obiekt gniazda
  private BufferedReader inputBufferedReader;           // bufor wejściowy (dane odebrane z serwera)
  private PrintWriter outputPrintWriter;                // bufor wyjściowy (dane do wysłania)
  
  public void setMain(CalcApp calcApp) {
    this.calcApp = calcApp;
  }
  
  @FXML
  public void handleButton() {
    System.out.println("Button pressed.");
  }

  @FXML
  public void btnClearClick() {
    field.clear();
  }

  @FXML
  public void btnSignClick() {
    String formula = field.getText();
    String sign = "-";
    field.setText(String.format("%s(%s)", sign, formula));
  }
  
  @FXML
  public void btnPercClick() {
    field.setText(field.getText() + "%");
  }
  
  @FXML
  public void btnDivClick() {
    field.setText(field.getText() + "/");
  }
  
  @FXML
  public void btnDigitClick(ActionEvent e) {
//    String formula = field.getText();
//    StringBuilder sb = new StringBuilder(formula.strip());
//    Button b = (Button) e.getSource();
//    Double digitDbl = Double.valueOf(b.getText());
//    String strDouble = String.format("%.1f", digitDbl);
//    sb.append(strDouble);
//    field.setText(sb.toString());

    Button b = (Button) e.getSource();
    field.setText(field.getText() + b.getText());
  }
  
  @FXML
  public void btnMultClick() {
    field.setText(field.getText() + "*");
  }
  
  @FXML
  public void btnMinusClick() {
    field.setText(field.getText() + "-");
  }
  
  @FXML
  public void btnPlusClick() {
    field.setText(field.getText() + "+");
  }
  
  @FXML
  public void btnDotClick() {
    field.setText(field.getText() + ".");
  }
  
  @FXML
  public void btnResultClick() throws IOException {
    if (field.getLength() == 0) return;

    String formula = field.getText().strip();
    System.out.printf("Sending to server: %s\n", formula);
    field.clear();
    
    try {
      socket = new Socket(host, port);
      
      InputStream inStream = socket.getInputStream();
      OutputStream outStream = socket.getOutputStream();
      boolean autoFlush = true;
      inputBufferedReader = new BufferedReader(new InputStreamReader(inStream));
      outputPrintWriter = new PrintWriter(outStream, autoFlush);
      outputPrintWriter.println(formula);
      
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try
          {
            //while (true)
            {
              inputBufferedReader.readLine(); // przywitanie od serwera
              String msg = inputBufferedReader.readLine(); // wynik
              String formattedResponse = formatResponse(msg);
              System.out.println("Received: " + msg);
              System.out.println("Formatted: " + formattedResponse);
              if (formattedResponse == null) {
                Platform.runLater(new Runnable() {

                  @Override
                  public void run() {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Jshell Error");
                    alert.setContentText("Nieprawidłowa formuła:\n" + formula);
                    alert.showAndWait();
                  }
               
                });
              }
              else
                field.setText(formatResponse(msg));
            }
          } catch (IOException e) {
            System.out.println(e.getMessage());
          }
        }
      };
      new Thread(task).start();
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    //sendMessage(formula);
  }

  private String formatFormula(String msg) {
    return msg;
  }
  
  private String formatResponse(String msg) {
    Pattern pattern = Pattern.compile("RESULT:<\\d+>\\=(\\d+)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(msg);
    boolean matchFound = matcher.find();
    if(matchFound) {
      System.out.println("matcher.group(0)=" + matcher.group(0));
      System.out.println("matcher.group(1)=" + matcher.group(1));
      return matcher.group(1);
    } else {
      System.out.println("ERROR");
      return null;
    }
  }
  
  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }
  

  @Override
  public void close() throws Exception {
    // TODO Auto-generated method stub
    
  }
}
