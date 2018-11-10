package kiat.anhong.changeId.Utils;

public enum FileEnum {

    SOURCE_PUBLIC_XML("Source public.xml:", "sourcepublic"),
    SOURCE_SMAI_FILE("Source smali file:", "sourcesmali"),
    PORT_PUBLIC_XML("Port public.xml:", "portpublic");

    private final String label;
    private final String fileName;

    private FileEnum(String label, String fileName) {
        this.label = label;
        this.fileName = fileName;
    }

    public String getLabel() {
        return this.label;
    }

    public String getFileName() {
        return this.fileName;
    }

}
