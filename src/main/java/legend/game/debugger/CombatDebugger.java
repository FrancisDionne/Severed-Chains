package legend.game.debugger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombatDebugger extends Application {
  @Override
  public void start(final Stage stage) throws Exception {
    final Parent root = FXMLLoader.load(this.getClass().getResource("combat_debugger.fxml"));
    final Scene scene = new Scene(root);
    scene.getStylesheets().add(this.getClass().getResource("combat_debugger.css").toExternalForm());

    stage.setTitle("Combat Debugger");
    stage.setScene(scene);
    stage.setX(Debugger.getStage().getX() + ((Debugger.getStage().getWidth() - root.prefWidth(-1)) / 2));
    stage.setY(Debugger.getStage().getY() + ((Debugger.getStage().getHeight() - root.prefHeight(-1)) / 2));
    stage.show();
  }

  public static ArrayList<String> log = new ArrayList<String>();
  public static boolean effecting;

  public static void AddLog(final String line, final int frame, final float x, final float y, final float zMin, final float zMax, final float zShift) {
    log.add("Frame: " + frame + ": " + line + ", x: " + x + ", y: " + y + ", zMin: " + zMin + ", zMax: " + zMax + ", zShift: " + zShift);
  }

  public static void WriteLog() throws IOException {

    int i = 0;
    String path = "effects_log_0.txt";

    while (true) {
      final File f = new File(path);
      if(f.exists() && !f.isDirectory()) {
        path = "effects_log_" + (++i) + ".txt";
      } else {
        break;
      }
    }

    final Path file = Paths.get(path);
    Files.write(file, log, StandardCharsets.UTF_8);
    effecting = false;
    log.clear();
  }
}
