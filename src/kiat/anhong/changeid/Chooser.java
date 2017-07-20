package kiat.anhong.changeid;

import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Chooser {
    private FileChooser file;
    private DirectoryChooser folder;

    public Chooser(FileChooser.ExtensionFilter extension) {
        if (extension != null) {
            file = new FileChooser();
            file.getExtensionFilters().add(extension);
        } else{
            folder =new DirectoryChooser();
        }
    }
    
    public void setTitle(String title){
        if (file != null) {
            this.file.setTitle(title);
        } else{
            this.folder.setTitle(title);
        }
    }
    public void setInitialDirectory(File folder){
        if (file != null) {
            this.file.setInitialDirectory(folder);
        } else{
            this.folder.setInitialDirectory(folder);
        }
    }
    public File showOpenDialog(Stage stage){
        if(file != null){
            return file.showOpenDialog(stage);
        } else{
            return folder.showDialog(stage);
        }
    }
}
