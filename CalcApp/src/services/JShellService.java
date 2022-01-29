package services;

import java.util.List;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

public class JShellService implements Runnable {
  private int requestId;
  private String requestedFormula;
  private String response;
  
  public JShellService(int requestId, String requestedFormula) {
    this.requestId = requestId;
    this.requestedFormula = requestedFormula;
    response = null;
  }

  @Override
  public void run() {
    calc();
  }

  public String getResponse() {
    return this.response;
  }
  
  private void calc() {
    JShell jshell = JShell.create();
    try (jshell) {
      List<SnippetEvent> events = jshell.eval(this.requestedFormula);
      for (SnippetEvent e : events) {
        if (e.causeSnippet() == null) {
          switch (e.status()) {
          case VALID:
            if (e.value() != null) {
              this.response = e.value();
              System.out.printf("%s = %s\n", this.requestedFormula, this.response);
            }
            break;
          default:
            System.out.println("Error\n");
            this.response = null;
            break;
          }
        }
      }
    }
  }
  
}
