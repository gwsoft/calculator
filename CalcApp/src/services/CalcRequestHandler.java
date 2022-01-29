package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import server.CalcServer;

public class CalcRequestHandler extends Thread {
  private CalcServer server;
  private Socket clientSocket;
  private int requestId;
  private String requestName;
  private BufferedReader inputBufferedReader;
  private PrintWriter outputPrintWriter;
  
  public CalcRequestHandler(CalcServer calcServer, Socket socket) throws IOException {
    server = calcServer;
    clientSocket = socket;
    registerRequest();
  }
  
  private void registerRequest() throws IOException {
    InputStream inStream = clientSocket.getInputStream();
    OutputStream outStream = clientSocket.getOutputStream();
    
    inputBufferedReader = new BufferedReader(new InputStreamReader(inStream));
    boolean autoFlush = true;
    outputPrintWriter = new PrintWriter(outStream, autoFlush);
    
    requestId = new Random().nextInt(Integer.MAX_VALUE);
    requestName = /*clientSocket.getLocalAddress() + ":" +*/ String.format("<%d>", clientSocket.getPort());
    server.getClients().putIfAbsent(requestId, outputPrintWriter);
    outputPrintWriter.println("Ready to serve. Waiting for your input.");
    System.out.println("request from: " + requestName);
    System.out.println("UID: " + String.valueOf(requestId));
  }
  
  @Override
  public void run() {
    try
    {
      while (true)
      {
        String requestedFormula = inputBufferedReader.readLine();
        if (requestedFormula == null) { throw new IOException(); }
        if (!requestedFormula.isEmpty())
        {
          
          JShellService jss = new JShellService(requestId, requestedFormula);
          Thread t = new Thread(jss);
          t.start();
          
          t.join();
          
          // wys³anie wyniku do wszystkich klientów poza nadawc¹ (wersja z czatu)
//        server.getClients().entrySet().stream().filter(entry -> entry.getKey() != requestId)
//        .forEach(entry -> sendMessage(entry.getValue(), requestedFormula));

          String jssResponse = jss.getResponse();
          String resultMsg;
          if (jssResponse == null) {
            resultMsg = "B³¹d obliczeñ!";
          } else {
            resultMsg = jssResponse;
          }
          
          // wys³anie wyniku do nadawcy
          server.getClients().entrySet().stream().filter(entry -> entry.getKey() == requestId)
            .forEach(entry -> sendResult(entry.getValue(), resultMsg));
          // zamkniêcie po³¹czenia z nadawc¹
          server.getClients().entrySet().stream().filter(entry -> entry.getKey() == requestId)
          .forEach(entry -> entry.getValue().close());
        }
      }
    } catch (IOException | InterruptedException e) {
      System.out.println("User reset connection.");
    } finally {
      server.getClients().remove(requestId);
      try
      {
        clientSocket.close();
        System.out.println("User removed.");
      } catch (IOException e) { e.printStackTrace(); }
    }
  }
  
  private void sendResult(PrintWriter output, String result) {
    //final char SEP = (char) 31;
    final char SEP = '=';
    String serverMsg = "RESULT:" + requestName + SEP + result;
    System.out.println(serverMsg);
    output.println(serverMsg);
  }

}
