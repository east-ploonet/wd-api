package com.saltlux.aice_fe._baseline.baseService;

import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe.file.vo.UploadFileVo;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface FileService {

	List<FileVo> uploadFileToVoList(MultipartFile[] uploadFiles, final String tableName) throws Exception;
	
	List<FileVo> uploadFileToVoList(MultipartFile uploadFiles, final String tableName) throws Exception;
	
	List<FileVo> uploadFileToVoListS3(MultipartFile uploadFiles, final String tableName) throws Exception;
	
	UploadFileVo uploadFileS3(MultipartFile file, String tableName, String fileName) throws Exception;
	
}
