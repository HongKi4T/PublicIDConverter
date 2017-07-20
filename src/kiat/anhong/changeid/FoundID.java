package kiat.anhong.changeid;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class FoundID {

    private static final String TAB = "    ";
    private static final String TAB2 = TAB + TAB;
    private static final String DIV_END = " └── ";
    private static final String DIV = " ├── ";
    private static final String BREAK_LINE = "\r\n";
    private static final File file = new File("found_public_ids.txt");
    private static final String header = "This file is located at: " + BREAK_LINE + file.getAbsolutePath();

    public static void writeHeader() {
        try {
            Files.write(file.toPath(), "".getBytes(), StandardOpenOption.CREATE);
            Files.write(file.toPath(), header.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {

        }
    }

    public static void write(String string) {
        string = BREAK_LINE + string;
        try {
            Files.write(file.toPath(), string.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {

        }
    }

    public static void write(CustomFile file) {
        write(BREAK_LINE + TAB + file.getAbsolutePath() + " :");
    }

    public static void write(ArrayList<String> nameType, ArrayList<String> originalIds) {
        write(TAB2 + "Found the following " + nameType.size() + " IDs :");
        int i;
        for (i = 0; i < nameType.size() - 1; i++) {
            FoundID.write(TAB2 + DIV + nameType.get(i) + " id=\"" + originalIds.get(i) + "\"");
        }
        FoundID.write(TAB2 + DIV_END + nameType.get(i) + " id=\"" + originalIds.get(i) + "\"");
    }

    public static void show() {
        try {
            Desktop.getDesktop().edit(file);
        } catch (IOException ex) {
        }
    }

}
