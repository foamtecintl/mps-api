package com.foamtec.mps.model;

import java.io.Serializable;

public class FileData implements Serializable {
    private String fileName;
    private String contentType;
    private byte[] dataFile;
}
