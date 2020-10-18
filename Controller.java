package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class Controller {
    Auth auth = new Auth();
    public javafx.scene.image.ImageView ImageView;
    @FXML
    private ImageView ImageViewelo;

    public void mousePressedLogIn(MouseEvent mouseEvent) {
        String URL = "https://accounts.spotify.com/authorize?client_id=556d5601795442069fdf24e5129cf220&redirect_uri=http://localhost:8080&response_type=code";
        try {
            Desktop.getDesktop().browse(new URL(URL).toURI());
            auth.getAccessCode();
            auth.getAccessToken();
            if (auth.access) {
                Main.menu();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cursorHand(MouseEvent mouseEvent) {
        ImageViewelo.setCursor(Cursor.HAND);
    }

    public void cursorNormal(MouseEvent mouseEvent) {
        ImageViewelo.setCursor(Cursor.DEFAULT);
    }




}


class getUserInfo {
    public static String userName = "test";
    public static URL userPhotoUrl;

    public static void getUserInfo() {
        String apiLink = Auth.RESOURCE_URL + "/v1/me";
        Request request = new Request();
        String json = request.request(apiLink);
        try {
            JsonObject userNameJson = JsonParser.parseString(json).getAsJsonObject();
            String userStringName = userNameJson.get("display_name").getAsString();
            JsonObject imageUrl = JsonParser.parseString(json).getAsJsonObject();
            JsonArray imageUrlA = imageUrl.getAsJsonArray("images");
            String userImageUrl = "";
            for (JsonElement pa : imageUrlA) {
                JsonObject url = pa.getAsJsonObject();
                userImageUrl = url.get("url").getAsString();
            }
            userName = userStringName;
            userPhotoUrl = new URL(userImageUrl);

        } catch (Exception e) {
            try {
                JsonObject errorMessage = JsonParser.parseString(json).getAsJsonObject();
                String errorMessageString = errorMessage.get("message").getAsString();
                System.out.println(errorMessageString);
            } catch (Exception e2) {
                e.printStackTrace();
            }
        }

    }

}

class exitBox {
    static boolean answer;

    public static void exit() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Exit");
        window.setMinWidth(250);
        Label label = new Label("Do you really want to quit?");

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");


        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });

        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, yesButton, noButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        if (answer) {
            System.exit(0);
        }

    }

}

class Request {
    private String jsonRequest = "";

    public String request(String linkToApi) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + Auth.ACCESS_TOKEN)
                .uri(URI.create(linkToApi))
                .GET()
                .build();

        try {

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            assert response != null;
            jsonRequest = response.body();

        } catch (InterruptedException | IOException e) {
            System.out.println("Error response");
        }
        return jsonRequest;
    }
}

class Auth {
    public boolean end = false;
    public static String SERVER_PATH = "https://accounts.spotify.com";
    public static String REDIRECT_URI = "http://localhost:8080";
    public static String CLIENT_ID = "556d5601795442069fdf24e5129cf220";
    public static String CLIENT_SECRET = "2af9c6ba66134c698bc2e8cfd171fbc6";
    public static String ACCESS_TOKEN = "";
    public static String ACCESS_CODE = "";
    public static String responseFinal = "";
    public static String RESOURCE_URL = "https://api.spotify.com";
    public boolean access = false;

    //Getting access code
    public void getAccessCode() {
        //Creating a line to go to in the browser
        String uri = SERVER_PATH + "/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code";
        System.out.println("use this link to request the access code:");
        System.out.println(uri);

        //Creating a server and listening to the request.
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            server.start();
            server.createContext("/",
                    exchange -> {
                        String query = exchange.getRequestURI().getQuery();
                        String request;
                        if (query != null && query.contains("code")) {
                            ACCESS_CODE = query.substring(5);
                            System.out.println("code received");
                            //System.out.println(ACCESS_CODE);
                            request = "Got the code. Return back to your program.";
                        } else {
                            request = "Not found authorization code. Try again.";
                        }
                        exchange.sendResponseHeaders(200, request.length());
                        exchange.getResponseBody().write(request.getBytes());
                        exchange.getResponseBody().close();
                    });

            System.out.println("waiting for code...");
            while (ACCESS_CODE.length() == 0) {
                Thread.sleep(100);
            }
            server.stop(5);

        } catch (IOException | InterruptedException e) {
            System.out.println("Server error");
        }
    }


    //Getting access_token based on access_code

    public void getAccessToken() {

        System.out.println("making http request for access_token...");
        System.out.println("response:");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(SERVER_PATH + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code"
                                + "&code=" + ACCESS_CODE
                                + "&client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&redirect_uri=" + REDIRECT_URI))
                .build();

        try {

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assert response != null;
            responseFinal = response.body();
            //System.out.println(responseFinal);
            System.out.println("---SUCCESS---");
            getAccessTokenFinal();
            end = true;

        } catch (InterruptedException | IOException e) {
            System.out.println("Error response");
        }
    }

    public void getAccessTokenFinal() {
        String json = responseFinal;
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        ACCESS_TOKEN = jo.get("access_token").getAsString();
        access = true;
    }
}
