
package com.saltlux.aice_fe._baseline.baseService.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.util.FileManager;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.vo.UploadFileVo;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("fileService")
public class FileServiceImpl implements FileService {

	FileUploadService fileUploadService;
	
    //-- Global Fields --//
    //
    @Value("${path.file.upload}")
    private String pathFileUpload;
    

	@Value("${globals.ph.upload.file}")
	private String phNotUpload;
	 
	 
	@Value("${s3.url}")
    private String s3Url;

    @Value("${s3.bucket}")
    private String bucketName;

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secretKey;
	    
	//
	//-- Global Fields --//

    @Override
    public List<FileVo> uploadFileToVoList(MultipartFile[] uploadFiles, final String tableName) throws Exception {

        List<FileVo> fileVoList     = new ArrayList<>();
	    //
	    Map<String, Object> fileMap = null;
	    //
	    try {
		    for(MultipartFile file : uploadFiles) {
				//-- 신규 파일 또는 기존 파일의 수정일 경우 파일경로 반영
				if( file           != null && ! file          .isEmpty() &&
					pathFileUpload != null && ! pathFileUpload.isEmpty() &&
				    tableName      != null && ! tableName     .isEmpty() ) {

//					fileMap = FileManager.uploadFile( fileUploadPath , pathServerMiddle + File.separator + tableName.toUpperCase(), "", file, codeVoList );
					fileMap = FileManager.uploadFile( pathFileUpload , "/" + tableName.toUpperCase(), file.getContentType(), file, phNotUpload );
					//
					FileVo fileVo = new FileVo();
					//
					fileVo.setFd_file_name( fileMap.get("orgName".trim()).toString() );
					fileVo.setFd_file_path( fileMap.get("webPath".trim()).toString() );
					fileVo.setFd_file_size( fileMap.get("size".trim()).toString() );
					//if(fileMap.get("mimeCode") != null ) fileVo.setFd_mime_code( fileMap.get("mimeCode".trim()).toString() );
					//
					fileVoList.add(fileVo);

				}
			}
		} catch (IOException ex) {

		    log.error    ("********** File Upload Failed : {}", ex.getMessage());
		}

        return fileVoList;
    }
    
    @Override
    public List<FileVo> uploadFileToVoList(MultipartFile uploadFiles, final String tableName) throws Exception {

        List<FileVo> fileVoList     = new ArrayList<>();
	    //
	    Map<String, Object> fileMap = null;
	    //
	    try {
				//-- 신규 파일 또는 기존 파일의 수정일 경우 파일경로 반영
				if( uploadFiles           != null && ! uploadFiles          .isEmpty() &&
					pathFileUpload != null && ! pathFileUpload.isEmpty() &&
				    tableName      != null && ! tableName     .isEmpty() ) {

//					fileMap = FileManager.uploadFile( fileUploadPath , pathServerMiddle + File.separator + tableName.toUpperCase(), "", file, codeVoList );
					fileMap = FileManager.uploadFile( pathFileUpload , "/" + tableName.toUpperCase(), uploadFiles.getContentType(), uploadFiles, phNotUpload );
					//
					FileVo fileVo = new FileVo();
					//
					fileVo.setFd_file_name( fileMap.get("orgName".trim()).toString() );
					fileVo.setFd_file_path( fileMap.get("webPath".trim()).toString() );
					fileVo.setFd_file_size( fileMap.get("size".trim()).toString() );
					//if(fileMap.get("mimeCode") != null ) fileVo.setFd_mime_code( fileMap.get("mimeCode".trim()).toString() );
					//
					fileVoList.add(fileVo);

				}
		} catch (IOException ex) {

		    log.error    ("********** File Upload Failed : {}", ex.getMessage());
		}

        return fileVoList;
    }
    
    @Override
    public List<FileVo> uploadFileToVoListS3(MultipartFile file, final String tableName) throws Exception {
        List<FileVo> fileVoList     = new ArrayList<>();
	    //
	    Map<String, Object> fileMap = null;
	    
	    //
	    try {
	    	uploadFileS3(file, tableName, file.getOriginalFilename());
	    }catch (Exception e) {
	    	System.out.println("s3 upload error:" + e);
		}
	    
	    try {
	    		
				//-- 신규 파일 또는 기존 파일의 수정일 경우 파일경로 반영
				if( file           != null && ! file          .isEmpty() &&
					pathFileUpload != null && ! pathFileUpload.isEmpty() &&
				    tableName      != null && ! tableName     .isEmpty() ) {

//					fileMap = FileManager.uploadFile( fileUploadPath , pathServerMiddle + File.separator + tableName.toUpperCase(), "", file, codeVoList );
					fileMap = FileManager.uploadFileToS3( pathFileUpload , "/" + tableName, file.getContentType(), file, phNotUpload );
					//
					FileVo fileVo = new FileVo();
					//
					fileVo.setFd_file_name( fileMap.get("orgName".trim()).toString() );
					fileVo.setFd_file_path( fileMap.get("webPath".trim()).toString() );
					fileVo.setFd_file_size( fileMap.get("size".trim()).toString() );
					//if(fileMap.get("mimeCode") != null ) fileVo.setFd_mime_code( fileMap.get("mimeCode".trim()).toString() );
					//
					fileVoList.add(fileVo);
				}
				
		} catch (IOException ex) {
		    log.error    ("********** File Upload Failed : {}", ex.getMessage());
		}

        return fileVoList;
    }
    
    @Override
    public UploadFileVo uploadFileS3(MultipartFile file, String tableName, String fileName) {
    	try {
			String dateStr = new SimpleDateFormat("yyyyMM").format(new Date());
			//
			String path = tableName.toUpperCase() + "/" + fileName;
			
	        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
	        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_NORTHEAST_2).build();
	        ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType(file.getContentType());
	        metadata.setContentLength(file.getSize());
	        UploadFileVo uploadFileVo = new UploadFileVo();
	        log.debug(String.format("bucketName : %s", bucketName));
	        log.debug(String.format("keyname : %s", path));
	        log.debug(String.format("content type : %s", metadata.getContentType()));
	        log.debug(String.format("content length : %s", metadata.getContentLength()));
	        uploadFileVo.setFd_file_size(file.getSize());
	        uploadFileVo.setFd_file_name(file.getOriginalFilename());
	        uploadFileVo.setFd_mime_code(file.getContentType());
	        try {
	            uploadFileVo.setBase64(StringUtils.newStringUtf8(org.apache.commons.codec.binary.Base64.encodeBase64(file.getBytes(), false)));
	        } catch (IOException e) {
	            System.out.println("e:" + e);
	        }


	        try (InputStream inputStream = file.getInputStream()) {

	            TransferManager tm = TransferManagerBuilder.standard()
	                    .withS3Client(amazonS3)
	                    .build();
	            PutObjectRequest request =
	                    new PutObjectRequest(bucketName, path, inputStream, metadata);
	            request.withCannedAcl(CannedAccessControlList.Private);
	            
	            Upload upload = tm.upload(request);
	            upload.waitForCompletion();
	            uploadFileVo.setFd_file_path(path);
	            //return uploadFileVo;
	        } catch (AmazonServiceException ase) {

	            log.error("Caught an AmazonServiceException, which " +
	                    "means your request made it " +
	                    "to Amazon S3, but was rejected with an error response" +
	                    " for some reason.");
	            log.error("Error Message:    " + ase.getMessage());
	            log.error("HTTP SNSStatus Code: " + ase.getStatusCode());
	            log.error("AWS Error Code:   " + ase.getErrorCode());
	            log.error("Error Type:       " + ase.getErrorType());
	            log.error("Request ID:       " + ase.getRequestId());
	            System.out.println("ase1:" + ase);
	            throw ase;
	        } catch (AmazonClientException ace) {
	            log.error("Caught an AmazonClientException, which " +
	                    "means the client encountered " +
	                    "an internal error while trying to " +
	                    "communicate with S3, " +
	                    "such as not being able to access the network.");
	            log.error("Error Message: " + ace.getMessage());
	            System.out.println("ase2:" + ace);
	            throw ace;
	        } catch (IOException ie) {
	            log.error("Error Message: " + ie.getMessage());
	            System.out.println("ie:" + ie);
	        } catch (InterruptedException e) {
	        	System.out.println("ex:" + e);
	        	log.error("InterruptedException Message: " + e.getMessage());
	            e.printStackTrace();
	        }
	        
		} catch (Exception e) {
			System.out.println("e:" + e);
		}
    	return null;
    }
    
    
}
