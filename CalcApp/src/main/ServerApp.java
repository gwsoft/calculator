package main;

import java.io.IOException;

import server.CalcServer;

public class ServerApp {

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      return;
    }
    
    int port = Integer.parseInt(args[0]);
    CalcServer cs = new CalcServer(port);
    cs.start();
  }

}
