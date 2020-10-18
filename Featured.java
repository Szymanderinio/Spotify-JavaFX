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

public class Featured {
    Request request = new Request();
    String apiLink = Auth.RESOURCE_URL + "/v1/browse/featured-playlists?limit=10";
    String json = request.request(apiLink);
    featuredPlaylist[] featured = new featuredPlaylist[10];
    int currentFeatured = 0;

    @FXML
    private Text TitleOfFeatured;

    @FXML
    private Text DescriptionOfFeatured;

    @FXML
    private ImageView ImageOfFeatured;

    @FXML
    private Hyperlink LinkToFeatured;


    public void initialize() {
        fetchFeatured();
        setFeaturedOnView();
    }

    public void backToMenu() {
        try {
            Main.menu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchFeatured() {
        try {
            JsonObject playlists = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("playlists");
            JsonArray ja = playlists.getAsJsonArray("items");
            int i = 0;
            for (JsonElement pa : ja) {
                JsonObject name = pa.getAsJsonObject();
                JsonObject href = pa.getAsJsonObject().getAsJsonObject("external_urls");

                JsonArray imageArray = pa.getAsJsonObject().getAsJsonArray("images");
                List<String> imagesArrayString = new ArrayList<>();
                for (JsonElement paaa : imageArray) {
                    imagesArrayString.add(paaa.getAsJsonObject().get("url").getAsString());
                }
                URL urlofImage = new URL(imagesArrayString.get(0));
                String nameString = name.get("name").getAsString();
                String description = name.get("description").getAsString();
                String hrefString = href.get("spotify").getAsString();
                featuredPlaylist currentFeatured = new featuredPlaylist();
                currentFeatured.setEverything(nameString, description, hrefString, urlofImage);
                featured[i] = currentFeatured;
                i++;
            }
        } catch (Exception e) {
            JsonObject errorMessage = JsonParser.parseString(json).getAsJsonObject();
            String errorMessageString = errorMessage.get("message").getAsString();
            System.out.println(errorMessageString);
        }

    }

    public void setFeaturedOnView() {
        TitleOfFeatured.setText(featured[currentFeatured].getTitle());
        DescriptionOfFeatured.setText(featured[currentFeatured].getDescription());

        Image userPhoto = null;
        try {
            userPhoto = SwingFXUtils.toFXImage(ImageIO.read(featured[currentFeatured].getUrlImageOfFeatured()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageOfFeatured.setImage(userPhoto);
    }

    public void mouseLinkToFeatured() {
        try {
            Desktop.getDesktop().browse(new URL(featured[currentFeatured].getHref()).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextFeatured() {
        if (currentFeatured < 9 && currentFeatured >= 0) {
            currentFeatured++;
        }
        setFeaturedOnView();
    }

    public void previousFeatured() {
        if (currentFeatured >= 1 && currentFeatured <= 10) {
            currentFeatured--;
        }
        setFeaturedOnView();
    }
}

class featuredPlaylist {
    private String Title;
    private String Description;
    private String Href;
    private URL UrlImageOfFeatured;

    public void setEverything(String title, String description, String href, URL urlimageoffeatured) {
        this.Title = title;
        this.Description = description;
        this.Href = href;
        this.UrlImageOfFeatured = urlimageoffeatured;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getHref() {
        return Href;
    }

    public URL getUrlImageOfFeatured() {
        return UrlImageOfFeatured;
    }
}