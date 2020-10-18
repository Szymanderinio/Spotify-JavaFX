package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    static Stage window;
    static Parent start;

    static Parent menu;
    static Scene menuScene;

    static Parent New;
    static Scene NewScene;

    static Parent Featured;
    static Scene FeaturedScene;

    @Override
    public void start(Stage primaryStage) throws Exception{

        window = primaryStage;
        start = FXMLLoader.load(getClass().getResource("FXML/main.fxml"));



        window.setTitle("Spotfy & JavaFX");
        window.setScene(new Scene(start, 800, 600));
        window.show();
        window.setOnCloseRequest(e -> {
            e.consume();
            exitBox.exit();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }



    public static void menu() throws IOException {
        menu = FXMLLoader.load(Main.class.getResource("./FXML/menu.fxml"));
        menuScene = new Scene(menu, 800, 600);
        window.setOnCloseRequest(e -> {
            e.consume();
            exitBox.exit();
        });
        window.setScene(menuScene);
    }

    public static void New() throws IOException {
        New = FXMLLoader.load(Main.class.getResource("./FXML/new.fxml"));
        NewScene = new Scene(New, 800, 600);
        window.setOnCloseRequest(e -> {
            e.consume();
            exitBox.exit();
        });
        window.setScene(NewScene);
    }

    public static void Featured() throws IOException {
        Featured = FXMLLoader.load(Main.class.getResource("./FXML/featured.fxml"));
        FeaturedScene = new Scene(Featured, 800, 600);
        window.setOnCloseRequest(e -> {
            e.consume();
            exitBox.exit();
        });
        window.setScene(FeaturedScene);
    }
}

