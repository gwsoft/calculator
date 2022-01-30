package gw.prp.calculator.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import gw.prp.calculator.main.CalcApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
  }

  @FXML
  public void btnSignClick() {
  }
  
  @FXML
  public void btnPercClick() {
  }
  
  @FXML
  public void btnDivClick() {
  }
  
  @FXML
  public void btnDigitClick(ActionEvent e) {
    System.out.println("Digit" + e.getSource());
  }
  
  @FXML
  public void btnMultClick() {
  }
  
  @FXML
  public void btnMinusClick() {
  }
  
  @FXML
  public void btnPlusClick() {
  }
  
  @FXML
  public void btnDotClick() {
  }
  
  @FXML
  public void btnResultClick() {
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
              System.out.println("Received: " + msg);
              field.setText(msg);
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

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }
  
  private void sendMessage(String message) {
    outputPrintWriter.println(message);
  }

  @Override
  public void close() throws Exception {
    // TODO Auto-generated method stub
    
  }
}
