package com.foamtec.mps.service;

import com.foamtec.mps.model.FileData;
import com.foamtec.mps.model.InformationFileData;
import com.foamtec.mps.repository.InformationFileDataRepository;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class MainService {

    @Autowired
    private InformationFileDataRepository informationFileDataRepository;

    public Date stringToDate(String strDate) throws ParseException {
        String[] df = strDate.split("/");
        String s = String.format("%02d", Long.parseLong(df[1])) + "/" + String.format("%02d", Long.parseLong(df[0])) + "/" + df[2];
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(s);
    }

    public String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    public Long saveFile(byte[] stream, String fileName, String contentType) throws IOException {
        InformationFileData informationFileData = createFile(fileName, contentType);
        String workingDir = System.getProperty("user.dir") + "/fileData";
        File convertFile = new File(workingDir + informationFileData.getUrl());
        convertFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(stream);
        fos.close();
        return informationFileData.getId();
    }

    public InformationFileData createFile(String fileName, String contentType) {
        InformationFileData informationFileData = new InformationFileData();
        informationFileData.setFileName(fileName);
        informationFileData.setContentType(contentType);
        informationFileDataRepository.save(informationFileData);
        informationFileData.setUrl("/" + informationFileData.getId() + "/" + fileName);
        informationFileDataRepository.update(informationFileData);
        return informationFileData;
    }


    public FileData getFileName(Long id) throws IOException {
        InformationFileData informationFileData = informationFileDataRepository.findById(id);
        String name = informationFileData.getFileName();
        String contentType = informationFileData.getContentType();
        String workingDir = System.getProperty("user.dir") + "/fileData/" + informationFileData.getId() + "/";
        File file = new File(workingDir + informationFileData.getFileName());
        FileInputStream fis = new FileInputStream(file);
        byte[] data = IOUtils.toByteArray(fis);
        FileData fileData = new FileData();
        fileData.setFileName(name);
        fileData.setContentType(contentType);
        fileData.setDataFile(data);
        return fileData;
    }
}
