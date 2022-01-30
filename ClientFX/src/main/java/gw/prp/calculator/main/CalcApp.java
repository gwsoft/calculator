package gw.prp.calculator.main;

import java.io.IOException;

import gw.prp.calculator.controller.CalcController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CalcApp extends Application {

  private Stage primaryStage;
  
  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    mainWindow();
  }

  public void mainWindow() {
    try {
      FXMLLoader loader = new FXMLLoader(CalcApp.class.getResource("../fxml/calculator.fxml"));
      BorderPane pane = loader.load();
      primaryStage.setMinWidth(265.0);
      primaryStage.setMaxWidth(315.0);
      primaryStage.setMinHeight(360.0);
      primaryStage.setMaxHeight(410.0);
      Scene scene = new Scene(pane);
      CalcController calcController = loader.getController();
      calcController.setMain(this);
      calcController.setHost("localhost");
      calcController.setPort(9200);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    launch(args);
  }

}
