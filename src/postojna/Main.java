package postojna;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    String mainpath = "C:\\Users\\Antonio\\Desktop\\";

    public static void emptyFolderFull(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                for (File file1 : file.listFiles()) {
                    file1.delete();
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Postojna Cave");
        primaryStage.getIcons().add(new Image("file:icona.png"));
        primaryStage.setScene(new Scene(root, 700, 250));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        File temp = new File(mainpath + "temp");
        if (temp.listFiles().length > 0) {
            emptyFolderFull(temp);
        }
        System.out.println("Stage is closing");
        System.exit(0);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
