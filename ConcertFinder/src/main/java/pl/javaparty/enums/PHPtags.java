package pl.javaparty.enums;

/**
 * Created by Jakub on 2015-03-13.
 */
public enum PHPtags {
    success("success"),
    insertComment("insertComment");

    int intValue;
    String stringValue;

    PHPtags(String stringValue) {
        this.stringValue = stringValue;
    }

    PHPtags(int intValue) {
        this.intValue = intValue;
    }

    public int getState() {
        return intValue;
    }
}
