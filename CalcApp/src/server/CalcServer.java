package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import services.CalcRequestHandler;

public class CalcServer {
  private ConcurrentHashMap<Integer, PrintWriter> clients;
  private int port;
  private ServerSocket serverSocket;
  
  public CalcServer(int port) {
    this.port = port;
    clients = new ConcurrentHashMap<Integer, PrintWriter>();
  }
  
  public ConcurrentHashMap<Integer, PrintWriter> getClients() {
    return clients;
  }

  public void start() throws IOException {
    serverSocket = new ServerSocket(port);
    System.out.printf("The server is running and listening on port %d...\n", port);
    while (true) {
      Socket socket = serverSocket.accept();
      CalcRequestHandler crh = new CalcRequestHandler(this, socket);
      crh.start();
    }
  }
}
