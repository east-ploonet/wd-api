package com.saltlux.aice_fe.file.utils;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FileUtils {

    @Getter
    public enum FileUploadType {
        NOTICE("NOTICE"), FAQ("FAQ"), USER("USER");
        String pathName;

        FileUploadType(String pathName) {
            this.pathName = pathName;
        }
    }

    static public String getUploadFilePath(FileUploadType fileUploadType, String fileName) {
        LocalDate nowDate = LocalDate.now();
        return fileUploadType.getPathName() + "/"
                + String.format("%04d", nowDate.getYear()) + "/"
                + String.format("%02d", nowDate.getMonth().getValue()) + "/"
                + String.format("%02d", nowDate.getDayOfMonth()) + "/"
                + System.currentTimeMillis() + "_" + fileName;
    }
}
