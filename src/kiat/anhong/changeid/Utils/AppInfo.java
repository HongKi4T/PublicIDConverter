package kiat.anhong.changeid.Utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppInfo {
    private static final String APP_VERSION = "0.2-Beta";
    public static final String APP_NAME = "Public ID Converter";
    
    private static Label getVersion(){
        Label version = new Label(APP_VERSION);
        version.setFont(Font.font(null, FontWeight.NORMAL, 12));
        return version;
    }
    
    public static HBox getVersionBox(){
        HBox versionBox = new HBox();
        versionBox.setAlignment(Pos.BOTTOM_RIGHT);
        versionBox.getChildren().add(getVersion());
        versionBox.setPadding(new Insets(5));
        return versionBox;
    }
}
