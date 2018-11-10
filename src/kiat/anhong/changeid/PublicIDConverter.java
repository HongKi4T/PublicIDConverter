package kiat.anhong.changeId;

import kiat.anhong.changeId.Utils.ButtonText;
import kiat.anhong.changeId.Utils.FileEnum;
import kiat.anhong.changeId.Utils.AppInfo;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.scene.layout.*;
import kiat.anhong.changeId.Utils.CheckBoxText;

import static kiat.anhong.changeId.Utils.LineString.*;

public class PublicIDConverter extends Application {

    private static final String TMP_FILE = "tmp";
    private static final String DEFAULT_SEARCH_STRING = "0x7[0-9a-f]{7}";
    private static final String FRAMEWORK_SEARCH_STRING = "(0x01[0-9a-f]{6})|(0x1[0-9a-f]{7})";

    private static final int MIN_WIDTH_TEXTFIELD = 400;

    private final FileChooser.ExtensionFilter xmlExtension = getExtensionFilter("xml");
    private final FileChooser.ExtensionFilter smaliExtension = getExtensionFilter("smali");

    private final ArrayList<String> originalIds = new ArrayList<>();
    private final ArrayList<String> nameType = new ArrayList<>();

    private CustomFile sourcePublicXml, portPublicXml, sourceSmaliFile;

    private boolean isFramework() {
        return frameworkCB.isSelected();
    }

    private boolean addComment() {
        return commentCB.isSelected();
    }

    private boolean useFolder() {
        return folderCB.isSelected();
    }

    TextField sourceTextField = new TextField();
    TextField portTextField = new TextField();
    TextField fileTextField = new TextField();

    final Text statusText = new Text();
    CheckBox frameworkCB;
    CheckBox folderCB;
    CheckBox commentCB;

    //commentCB
    public static void main(String[] args) {
        launch(args);
    }

    public PublicIDConverter() {
        System.out.println("main");

        frameworkCB = new CheckBox(CheckBoxText.FRAMEWORK);
        folderCB = new CheckBox(CheckBoxText.DIRECTORY);
        commentCB = new CheckBox(CheckBoxText.COMMENT);
        commentCB.setSelected(true);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle(AppInfo.APP_NAME);
        MyGridPane gridXML = new MyGridPane();
        ColumnConstraints colCon = new ColumnConstraints();
        colCon.setHgrow(Priority.ALWAYS);
        colCon.setMinWidth(MIN_WIDTH_TEXTFIELD);
        gridXML.setAllCol(colCon);

        getFilesFromPreference();

        Label sourceLabel = new Label(sourcePublicXml.getTitle());
        Label fileLabel = new Label(sourceSmaliFile.getTitle());
        Label portLabel = new Label(portPublicXml.getTitle());

        Button sourceXmlBtn = new Button(ButtonText.OPEN);
        Button portXmlBtn = new Button(ButtonText.OPEN);
        Button sourceSmaliBtn = new Button(ButtonText.OPEN);
        Button convertButton = new Button(ButtonText.CONVERT);
        Button findButton = new Button(ButtonText.FIND);

        sourcePublicXml.setButton(sourceXmlBtn, stage, xmlExtension);
        portPublicXml.setButton(portXmlBtn, stage, xmlExtension);

        sourceSmaliBtn.setOnAction((ActionEvent actionEvent) -> {
            if (useFolder()) {
                sourceSmaliFile.openFile(stage, null);
            } else {
                sourceSmaliFile.openFile(stage, smaliExtension);
            }
        });

        convertButton.setOnAction((ActionEvent actionEvent) -> {
            cmdConvert();
        });

        findButton.setOnAction((ActionEvent actionEvent) -> {
            updateStatus("Found " + cmdFind(sourceSmaliFile, true) + " IDs from " + sourceSmaliFile.getName());
        });

        RowLayout source = new RowLayout(sourceLabel, sourceTextField, sourceXmlBtn);
        RowLayout file = new RowLayout(fileLabel, fileTextField, sourceSmaliBtn);
        RowLayout port = new RowLayout(portLabel, portTextField, portXmlBtn);

        gridXML.add(source, 0);
        gridXML.add(file, 1);

        MyBox findBtn = new MyBox();
        findBtn.setButton(findButton);

        gridXML.add(findBtn, 1, 2);
        gridXML.add(folderCB, 1, 2);
        gridXML.add(new Label(""), 0, 3);
        gridXML.add(port, 4);

        MyBox convertBtn = new MyBox();
        convertBtn.setButton(convertButton);
        gridXML.add(convertBtn, 1, 5);

        MyGridPane optionsGrid = new MyGridPane();

        Label optionsTitle = new Label("OPTIONS");
        optionsTitle.setFont(Font.font(null, FontWeight.BOLD, 16));
        optionsGrid.add(optionsTitle, 0, 0);
        optionsGrid.add(frameworkCB, 0, 1);
        optionsGrid.add(commentCB, 1, 1);

        HBox versionBox = AppInfo.getVersionBox();

        final Pane rootGroup = new VBox();
        rootGroup.getChildren().addAll(gridXML, optionsGrid, versionBox);

        VBox statusBar = new VBox();
        statusBar.setStyle("-fx-background-color: #e9e9e9");
        statusBar.getChildren().add(statusText);

        final BorderPane borderPane = new BorderPane();
        borderPane.setCenter(rootGroup);
        borderPane.setBottom(statusBar);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void cmdConvert() {
        convertFile(sourceSmaliFile);
    }

    private void updateStatus(String string) {
        statusText.setText(string);
        System.out.print(string + "\n");
    }

    public static FileChooser.ExtensionFilter getExtensionFilter(String exten) {
        return new FileChooser.ExtensionFilter("File " + exten.toLowerCase(), "*." + exten.toLowerCase());
    }

    private int cmdFind(CustomFile sourceFile, boolean isRoot) {
        int num_id = 0;
        System.out.println("file : " + sourceFile.getAbsolutePath());
        if (isRoot) {
            FoundID.writeHeader();
        }
        if (sourceFile.isDirectory()) {
            for (final File file : sourceFile.getFile().listFiles()) {
                CustomFile mFile = new CustomFile(file);
                num_id += cmdFind(mFile, false);
            }
        } else {
            if (findIds(sourceFile) && nameType != null && !nameType.isEmpty()) {
                num_id += nameType.size();
                FoundID.write(sourceFile);
                FoundID.write(nameType, originalIds);
            }
        }
        if (isRoot) {
            FoundID.show();
        }
        return num_id;
    }

    private void getFilesFromPreference() {
        sourcePublicXml = new CustomFile(FileEnum.SOURCE_PUBLIC_XML, sourceTextField);
        sourceSmaliFile = new CustomFile(FileEnum.SOURCE_SMAI_FILE, fileTextField);
        portPublicXml = new CustomFile(FileEnum.PORT_PUBLIC_XML, portTextField);
    }

    private boolean findIds(CustomFile sourceFile) {
        if (!checkFiles()) {
            return false;
        }
        originalIds.clear();
        nameType.clear();

        Scanner scanner = null;
        Pattern pattern = Pattern.compile(getSearchString());

        //Read source smali
        try {
            updateStatus("Reading " + sourceFile.getFile().getName());
            scanner = new Scanner(sourceFile.getFile());
            while (scanner.hasNextLine()) {
                final String lineFromFile = scanner.nextLine();
                Matcher matcher = pattern.matcher(lineFromFile);
                if (matcher.find()) {
                    String publicId = matcher.group();
                    if (publicId.length() >= 6) {
                        originalIds.add(publicId);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Err(e);
        } finally {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception e) {
                    Err(e);
                }
            }
        }

        if (originalIds.isEmpty()) {
            updateStatus("No ids found in smali file");
            return false;
        }

        //Search source public.xml
        try {
            updateStatus("Searching source public.xml for ids...");
            for (String id : originalIds) {
                boolean contains = false;
                scanner = new Scanner(sourcePublicXml.getFile());
                while (scanner.hasNextLine()) {
                    final String lineFromFile = scanner.nextLine();
                    if (lineFromFile.contains(id)) {
                        String name = getNameType(lineFromFile);
                        nameType.add(name);
                        contains = true;
                    }
                }
                if (!contains) {
                    nameType.add("Not found in source public.xml");
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Err : " + e);
        } finally {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception e) {
                    System.out.println("Err : " + e);
                }
            }
        }

        if (nameType.isEmpty()) {
            updateStatus("Could not find any ids in the source public.xml");
            return false;
        } else {
            updateStatus(sourceFile.getFile().getName() + " :Found " + nameType.size() + " IDs");
        }

        return true;
    }

    private void convertFile(CustomFile sourceFile) {
        Scanner scanner = null;
        ArrayList<String> newIds = new ArrayList<>();
        String filename = sourceFile.getFileName();
        if (sourceFile.isDirectory()) {
            for (File file : sourceFile.getFile().listFiles()) {
                System.out.println("dir");
                convertFile(new CustomFile(file));
            }
            return;
        }
        if (!findIds(sourceFile)) {
            return;
        }

        //Search port public.xml
        boolean isEmpty = true;
        boolean missing = false;
        try {
            for (String aNameType : nameType) {
                boolean contains = false;
                scanner = new Scanner(portPublicXml.getFile());
                while (scanner.hasNextLine()) {
                    final String linePortPublic = scanner.nextLine();
                    if (linePortPublic.contains(aNameType)) {
                        String id = getId(linePortPublic);
                        if (addComment()) {
                            String comment = "    # " + aNameType;
                            id += comment;
                        }
                        newIds.add(id);
                        contains = true;
                        isEmpty = false;
                        break;
                    }
                }
                if (!contains) {
                    missing = true;
                    newIds.add("    # MISSING: " + aNameType);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Err : " + e);
        } finally {
            close(scanner);
        }

        if (isEmpty) {
            updateStatus("No matching ids found in port public.xml");
            return;
        }

        //Search source file and change ids
        BufferedWriter bw = null;
        File tmpFile = new File(TMP_FILE);
        try {
            updateStatus("Replacing ids in smali file...");

            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile)));
            scanner = new Scanner(sourceFile.getFile());
            while (scanner.hasNextLine()) {
                String lineFromFile = scanner.nextLine();
                for (int i = 0; i < originalIds.size(); i++) {
                    if (lineFromFile.contains(originalIds.get(i))) {
                        if (newIds.get(i).contains("MISSING")) {
                            if (!lineFromFile.contains("MISSING") && addComment()) {
                                lineFromFile += newIds.get(i);
                            }
                        } else if (!newIds.get(i).contains(originalIds.get(i))) {
                            System.out.println("\t-" + lineFromFile);
                            lineFromFile = lineFromFile.replace(originalIds.get(i), newIds.get(i));
                            System.out.println("\t+" + lineFromFile);
                        }
                        break;
                    }
                }
                bw.write(lineFromFile);
                bw.newLine();
            }
        } catch (IOException e) {
            Err(e);
        } finally {
            close(bw);
            close(scanner);
        }
        try {
            Files.move(Paths.get(TMP_FILE), Paths.get(sourceFile.getFile().getPath()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Edited file : " + filename);
        } catch (IOException e) {
            Err(e);
        }
        updateStatus((missing ? "There were some \"MISSING\" IDs. \n" : "") + filename + " created in source smali directory.");
    }

    private String getSearchString() {
        return isFramework() ? FRAMEWORK_SEARCH_STRING : DEFAULT_SEARCH_STRING;
    }

    private boolean checkFiles() {
        if (sourcePublicXml.isNull() || sourceSmaliFile.isNull() || portPublicXml.isNull()) {
            updateStatus("Please load all files!");
            return false;
        }
        if (!sourcePublicXml.canOpen()) {
            updateStatus("Could not open source public.xml");
            return false;
        }
        if (!portPublicXml.canOpen()) {
            updateStatus("Could not open port public.xml");
            return false;
        }
        if (!sourceSmaliFile.canOpen()) {
            updateStatus("Could not open source smali");
            return false;
        }

        if (sourcePublicXml.isDirectory() || portPublicXml.getFile().isDirectory()) {
            updateStatus("Make sure you selected a file and not a directory");
            return false;
        }
        return true;
    }

    private void Err(Exception e) {
        System.out.println("Err : " + e);
    }

    private void close(BufferedWriter bw) {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e) {
                Err(e);
            }
        }
    }

    private void close(Scanner scan) {
        if (scan != null) {
            try {
                scan.close();
            } catch (Exception e) {
                Err(e);
            }
        }
    }

}
