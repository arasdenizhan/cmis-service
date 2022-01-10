package com.solviads.cmis.constants;


public enum MIMETypes {
    TEXT("text/plain"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    PDF("application/pdf");

    private final String value;

    MIMETypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
