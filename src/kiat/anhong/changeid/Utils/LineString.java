package kiat.anhong.changeId.Utils;

public class LineString {

    public static String getId(String line) {
        int head = line.indexOf("id=") + 4;
        int tail = line.lastIndexOf(" ") - 1;
        return line.substring(head, tail);
    }

    public static String getNameType(String line) {
        int head = line.indexOf("type=");
        int tail = line.indexOf("id=") - 1;
        return line.substring(head, tail);
    }
}
