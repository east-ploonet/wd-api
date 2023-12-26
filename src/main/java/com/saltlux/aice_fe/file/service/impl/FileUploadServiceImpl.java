package com.saltlux.aice_fe.file.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {


    @Value("${s3.url}")
    public String s3Url;

    @Value("${s3.bucket}")
    public String bucketName;

    @Value("${s3.accessKey}")
    public String accessKey;

    @Value("${s3.secretKey}")
    public String secretKey;
//
//    @Value("${s3.cloudFront.host}")
//    private String host;


    @Override
    public UploadFileVo uploadFileToS3(MultipartFile file, String path) {
    	System.out.println("accessKey:AKIAR4LFHIID6QEUDYHQ");
    	System.out.println("secretKey:" + secretKey);
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
            e.printStackTrace();
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
            return uploadFileVo;
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
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public UploadFileVo uploadFileS3(Path file, String path) {
    	AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_NORTHEAST_2).build();
        try {
        String mimeType = Files.probeContentType(file);
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(mimeType);
        metadata.setContentLength(Files.size(file));
        byte[] array = Files.readAllBytes(file);
        
        UploadFileVo uploadFileVo = new UploadFileVo();
        log.debug(String.format("bucketName : %s", bucketName));
        log.debug(String.format("keyname : %s", path));
        log.debug(String.format("content type : %s", metadata.getContentType()));
        log.debug(String.format("content length : %s", metadata.getContentLength()));
        uploadFileVo.setFd_file_size(Files.size(file));
        uploadFileVo.setFd_file_name(file.getFileName().toString());
        uploadFileVo.setFd_mime_code(mimeType);
        
        uploadFileVo.setBase64(StringUtils.newStringUtf8(org.apache.commons.codec.binary.Base64.encodeBase64(array, false)));
	        try (InputStream inputStream = Files.newInputStream(file)) {
	
	            TransferManager tm = TransferManagerBuilder.standard()
	                    .withS3Client(amazonS3)
	                    .build();
	            PutObjectRequest request =
	                    new PutObjectRequest(bucketName, path, inputStream, metadata);
	            request.withCannedAcl(CannedAccessControlList.Private);
	            Upload upload = tm.upload(request);
	            upload.waitForCompletion();
	            uploadFileVo.setFd_file_path(path);
	            return uploadFileVo;
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
	
	            throw ase;
	        } catch (AmazonClientException ace) {
	            log.error("Caught an AmazonClientException, which " +
	                    "means the client encountered " +
	                    "an internal error while trying to " +
	                    "communicate with S3, " +
	                    "such as not being able to access the network.");
	            log.error("Error Message: " + ace.getMessage());
	            throw ace;
	        } catch (IOException ie) {
	            log.error("Error Message: " + ie.getMessage());
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    @Transactional(readOnly = true)
    public String getFileInfo() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_NORTHEAST_2).build();
        ObjectMetadata objectMetadata = amazonS3.getObjectMetadata(bucketName, "NOTICE/2022/09/28/1664347112746_3.jpeg");
        return objectMetadata.getContentType();
    }


    @Override
    @Transactional(readOnly = true)
    public UploadFileVo getFileStream(String path) {
        UploadFileVo uploadFileVo = new UploadFileVo();
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_NORTHEAST_2).build();
        S3Object object = amazonS3.getObject(bucketName, path);
        uploadFileVo.setFd_file_type(object.getObjectMetadata().getContentType());
        try {
            byte[] byteArray = IOUtils.toByteArray(object.getObjectContent());
            String encodedString = Base64.getEncoder().encodeToString(byteArray);
            uploadFileVo.setBase64(encodedString);
            return uploadFileVo;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    @Transactional(readOnly = true)
    public byte[] download(String path) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_NORTHEAST_2).build();
        S3Object object = amazonS3.getObject(bucketName, path);
        try {
            byte[] byteArray = IOUtils.toByteArray(object.getObjectContent());
            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
