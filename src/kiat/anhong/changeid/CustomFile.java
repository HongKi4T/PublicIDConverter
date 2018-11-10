package kiat.anhong.changeId;

import java.io.File;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kiat.anhong.changeId.Utils.FileEnum;

public class CustomFile {

    //Property
    private final String title;
    private final String fileName;
    private File file;
    private final TextField textField;

    //Constructor
    public CustomFile(File file) {
        this.title = "";
        this.fileName = file.getName();
        this.file = file;
        this.textField = null;
    }

    public CustomFile(FileEnum fileEnum, TextField textField) {
        String filePath = getFilePath(fileEnum.getFileName());
        System.out.print("fileName = " + fileEnum.getFileName());
        System.out.println("\t\tfilePath = " + filePath);
        if (filePath == null) {
            this.file = null;
        } else {
            this.file = new File(filePath);
            textField.setText(this.file.getAbsolutePath());
        }
        this.title = fileEnum.getLabel();
        this.fileName = fileEnum.getFileName();

        textField.setEditable(false);
        this.textField = textField;

    }

    //Getter
    public String getTitle() {
        return this.title;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getName() {
        String type;
        if (this.file.isDirectory()) {
            type = "folder ";
        } else {
            type = "file ";
        }
        return type + this.file.getName();
    }

    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }

    public File getFile() {
        return this.file;
    }

    public File getDirectory() {
        if (this.file.isDirectory()) {
            return this.file;
        }
        return this.file.getParentFile();
    }

    public TextField getTextField() {
        return this.textField;
    }

    public void setFilePreference(File file) {
        this.setFile(file);
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        if (file != null) {
            prefs.put(this.getFileName(), file.getPath());
        } else {
            prefs.remove(this.getFileName());

        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setButton(Button button, Stage stage, FileChooser.ExtensionFilter extension) {
        button.setOnAction((final ActionEvent e) -> {
            this.openFile(stage, extension);
        });
    }

    private String getFilePath(String fileName) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        return prefs.get(fileName, null);
    }

    public boolean canOpen() {
        return (!this.isNull() && this.file.exists());
    }

    public boolean isDirectory() {
        return this.file.isDirectory();
    }

    public boolean isNull() {
        return this.file == null;
    }

    public void openFile(Stage stage, FileChooser.ExtensionFilter extension) {
        Chooser chooser = new Chooser(extension);

        chooser.setTitle(this.getTitle());

        if (this.canOpen()) {
            chooser.setInitialDirectory(this.getDirectory());
        }
        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile != null) {
            this.setFilePreference(selectedFile);
            this.getTextField().setText(selectedFile.getAbsolutePath());
        }
    }
}
