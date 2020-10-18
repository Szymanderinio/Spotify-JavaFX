package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.IOException;

public class menuInfo {
    @FXML
    private ImageView userPhoto2;

    @FXML
    private Text nameText;

    public void initialize() {
        getUserInfo.getUserInfo();
        setNameText();
        setUserPhoto2();
    }

    public void New() {
        try {
            Main.New();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Featured() {
        try {
            Main.Featured();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNameText() {
        nameText.setText(getUserInfo.userName);
    }

    public void setUserPhoto2() {
        Image userPhoto = null;
        try {
            userPhoto = SwingFXUtils.toFXImage(ImageIO.read(getUserInfo.userPhotoUrl), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userPhoto2.setImage(userPhoto);
    }

}

