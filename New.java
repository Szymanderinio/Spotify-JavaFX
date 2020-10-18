package sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class New {
    Request request = new Request();
    String apiLink = Auth.RESOURCE_URL + "/v1/browse/new-releases";
    String json = request.request(apiLink);
    Song[] songs = new Song[20];
    int currentSong = 0;

    @FXML
    private Text TitleOfSong;

    @FXML
    private Text AuthorsOfSong;

    @FXML
    private ImageView ImageOfSong;

    @FXML
    private Hyperlink LinkToSong;


    public void initialize() {
        fetchSongs();
        setSongOnView();
    }

    public void backToMenu() {
        try {
            Main.menu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchSongs() {
        try {
            JsonObject playlists = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("albums");
            JsonArray ja = playlists.getAsJsonArray("items");
            int i = 0;
            for (JsonElement pa : ja) {

                JsonObject name = pa.getAsJsonObject();
                JsonArray artistArray = pa.getAsJsonObject().getAsJsonArray("artists");
                List<String> artistArrayString = new ArrayList<>();
                for (JsonElement paa : artistArray) {
                    artistArrayString.add(paa.getAsJsonObject().get("name").getAsString());
                }
                JsonObject href = pa.getAsJsonObject().getAsJsonObject("external_urls");
                JsonArray imageArray = pa.getAsJsonObject().getAsJsonArray("images");
                List<String> imagesArrayString = new ArrayList<>();
                for (JsonElement paaa : imageArray) {
                    imagesArrayString.add(paaa.getAsJsonObject().get("url").getAsString());
                }
                URL urlofImage = new URL(imagesArrayString.get(0));
                String nameString = name.get("name").getAsString();
                String artistString = artistArrayString.toString();
                String hrefString = href.get("spotify").getAsString();
                Song currentSong = new Song();
                currentSong.setEverything(nameString,artistString,hrefString,urlofImage);
                songs[i] = currentSong;
                i++;
            }
        } catch (Exception e) {
            JsonObject errorMessage = JsonParser.parseString(json).getAsJsonObject();
            String errorMessageString = errorMessage.get("message").getAsString();
            System.out.println(errorMessageString);
        }

    }

    public void setSongOnView() {
        TitleOfSong.setText(songs[currentSong].getTitle());
        AuthorsOfSong.setText(songs[currentSong].getArtists());

        Image userPhoto = null;
        try {
            userPhoto = SwingFXUtils.toFXImage(ImageIO.read(songs[currentSong].getUrlImageOfSong()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageOfSong.setImage(userPhoto);
    }

    public void mouseLinkToSong() {
        try {
            Desktop.getDesktop().browse(new URL(songs[currentSong].getHref()).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextSong() {
        if (currentSong < 19 && currentSong >= 0) {
            currentSong++;
        }
        setSongOnView();
    }

    public void previousSong() {
        if (currentSong >= 1 && currentSong <= 20) {
            currentSong--;
        }
        setSongOnView();
    }
}

class Song {
    private String Title;
    private String Artists;
    private String Href;
    private URL UrlImageOfSong;

    public void setEverything(String title, String artists, String href, URL urlimageofsong) {
        this.Title = title;
        this.Artists = artists;
        this.Href = href;
        this.UrlImageOfSong = urlimageofsong;
    }

    public String getTitle() {
        return Title;
    }

    public String getArtists() {
        return Artists;
    }

    public String getHref() {
        return Href;
    }

    public URL getUrlImageOfSong() {
        return UrlImageOfSong;
    }
}
