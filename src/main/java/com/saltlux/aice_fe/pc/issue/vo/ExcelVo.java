package com.saltlux.aice_fe.pc.issue.vo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import lombok.Data;

@Data
public class ExcelVo {
    private String columnName;
    private String targetName;
    
    public ExcelVo(String columnName, String targetName) {
    	this.columnName = columnName;
    	this.targetName = targetName;
    }
}
