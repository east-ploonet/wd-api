package com.saltlux.aice_fe._baseline.util;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class FileManager {
	
	 
	/**
	 * File Upload
	 *
	 * @param serverPath : 서버업로드 경로
	 * @param subPath : 메뉴명 ex)신고하기 "cs"
	 * @param type
	 * @param file
	 * @return
	 * @throws IOException
	 */
//	public static Map uploadFile(String serverPath, String subPath, String type, MultipartFile file, List<CodeVo> codeList) throws IOException {
	public static Map<String, Object> uploadFile(String serverPath, String subPath, String type, MultipartFile file, String phNotUpload) throws IOException {

		//String uploadPath = request.getServletContext().getInitParameter("trackingPath");
		//String realPath = request.getServletContext().getInitParameter("uploadPath") +uploadPath;

		String dateStr = new SimpleDateFormat("yyyyMM").format(new Date());
		//
		String webPath, orgName, saveName, size, ext, uuid;
		//
//		String realPath = serverPath + File.separator + subPath + File.separator + dateStr;
		String realPath = serverPath + "/" + subPath + "/" + dateStr;
		//
		File targetDirectory = new File( realPath.replace("..", " ").replace("./", " ").replace("../", " ") );
		//
		if( ! targetDirectory.exists() ) {

			targetDirectory.mkdirs();

		}
		
		//
		Map<String, Object> returnMap = new HashMap<>();

		try {

			size    = String.valueOf(file.getSize());
			orgName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("\\")+1);
			ext     = orgName.substring(orgName.lastIndexOf(".") + 1);
			uuid    = UUID.randomUUID().toString();
			//
			if( type.equals("thumb") ) {

				saveName = uuid + "_thumb." + ext;

			} else {

				saveName = uuid + "." + ext;

			}
			//
//			realPath    = targetDirectory.getAbsolutePath() + File.separator + saveName;
			realPath    = targetDirectory.getAbsolutePath() + "/" + saveName;
//			webPath     = subPath + File.separator + dateStr + File.separator + saveName;
			webPath     = subPath + "/" + dateStr + "/" + saveName;


			
			String[] extStr = phNotUpload.split(",");
			
			if(Arrays.asList(extStr).contains(ext)) return null;
			
			File outputFile = new File( realPath.replace("..", " ").replace("./", " ").replace("../", " ") );
			file.transferTo(outputFile);

			returnMap.put("size"        , size);
			returnMap.put("ext"         , ext);
			returnMap.put("orgName"     , orgName);
			returnMap.put("saveName"    , saveName);
			returnMap.put("realPath"    , realPath);
			returnMap.put("webPath"     , webPath);

/*
			for(CodeVo codeVo : codeList){
				if( codeVo.getFd_memo().toUpperCase().contains( ext.toUpperCase() ) ){
					returnMap.put("mimeCode"    , codeVo.getPk_code());
					break;
				}
			}
*/

			log.debug("========================================File Upload========================================");
			log.debug("size      = {}", returnMap.get("size")     );
			log.debug("ext       = {}", returnMap.get("ext")      );
			log.debug("orgName   = {}", returnMap.get("orgName")  );
			log.debug("saveName  = {}", returnMap.get("saveName") );
			log.debug("realPath  = {}", returnMap.get("realPath") );
			log.debug("webPath   = {}", returnMap.get("webPath")  );
//			log.debug("mimeCode  = {}", returnMap.get("mimeCode") );
			log.debug("========================================================================================");

		} catch (IOException ex) {
			log.error    ("********** File Upload Failed : {}", ex.getMessage());
			throw ex;
		}
		//
		return returnMap;

	}
	
	/**
	 * File Upload
	 *
	 * @param serverPath : 서버업로드 경로
	 * @param subPath : 메뉴명 ex)신고하기 "cs"
	 * @param type
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> uploadFileToS3(String serverPath, String subPath, String type, MultipartFile file, String phNotUpload) throws IOException {

		String dateStr = new SimpleDateFormat("yyyyMM").format(new Date());
		//
		String webPath, orgName, saveName, size, ext, uuid;
		//
		String realPath = serverPath + "/" + subPath + "/" + dateStr;
		//
		File targetDirectory = new File( realPath.replace("..", " ").replace("./", " ").replace("../", " ") );
		//
		if( ! targetDirectory.exists() ) {

			targetDirectory.mkdirs();

		}
		
		//
		Map<String, Object> returnMap = new HashMap<>();

		try {

			size    = String.valueOf(file.getSize());
			orgName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("\\")+1);
			ext     = orgName.substring(orgName.lastIndexOf(".") + 1);
			uuid    = UUID.randomUUID().toString();
			//
			if( type.equals("thumb") ) {

				saveName = uuid + "_thumb." + ext;

			} else {

				saveName = uuid + "." + ext;

			}
			//
//			realPath    = targetDirectory.getAbsolutePath() + File.separator + saveName;
			realPath    = targetDirectory.getAbsolutePath() + "/" + saveName;
//			webPath     = subPath + File.separator + dateStr + File.separator + saveName;
			webPath     = subPath + "/" + dateStr + "/" + saveName;


			
			String[] extStr = phNotUpload.split(",");
			
			if(Arrays.asList(extStr).contains(ext)) return null;
			
			File outputFile = new File( realPath.replace("..", " ").replace("./", " ").replace("../", " ") );
			file.transferTo(outputFile);

			returnMap.put("size"        , size);
			returnMap.put("ext"         , ext);
			returnMap.put("orgName"     , orgName);
			returnMap.put("saveName"    , saveName);
			returnMap.put("realPath"    , realPath);
			returnMap.put("webPath"     , webPath);
			
			log.debug("========================================File Upload========================================");
			log.debug("size      = {}", returnMap.get("size")     );
			log.debug("ext       = {}", returnMap.get("ext")      );
			log.debug("orgName   = {}", returnMap.get("orgName")  );
			log.debug("saveName  = {}", returnMap.get("saveName") );
			log.debug("realPath  = {}", returnMap.get("realPath") );
			log.debug("webPath   = {}", returnMap.get("webPath")  );
			log.debug("========================================================================================");

		} catch (IOException ex) {
			log.error    ("********** File Upload Failed : {}", ex.getMessage());
			throw ex;
		}
		//
		return returnMap;

	}
	
    /**
     * File 확장자 체크
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static boolean extensionCheck(MultipartFile file)throws Exception {
    	
    	String imgExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());

    	//-- (jpg, jpeg, png)가 아닐경우 false 반환
    	//
    	if(!imgExt.equalsIgnoreCase("jpg") && 
		   !imgExt.equalsIgnoreCase("png") && 
		   !imgExt.equalsIgnoreCase("jpeg")) 			 
	    {

		    log.debug("========================================File Extension Error========================================");
		    log.debug("ext = {}", imgExt);
		    log.debug("====================================================================================================");
    		//
    		return false;

	    } else {

	    	return true;

	    }    	
    	
    }


    /**
     * 파일 존재여부 체크
     *
     * @param strFileName
     * @return
     */
    private static boolean isFileExists(String strFileName) {

        boolean rCode = false;

        File fileName = new File( strFileName.replace("..", " ").replace("./", " ").replace("../", " ") );

        try {

            if (fileName.exists()) {

                if (fileName.isFile()) {

                    rCode = true;

                } else {

                    rCode = false;

                }

            } else {

                rCode = false;

            }

        } catch(Exception ex) {

            log.error( ex.getMessage() );
            //
            rCode = false;

        }

        return rCode;

    }


    /**
     * 파일 다운로드
     *
     * @param request
     * @param response
     * @param out
     * @param strSourceFilePath
     * @param strSourceFileName
     * @param strDownFileName
     * @return
     */
    public static boolean downloadFile(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String strSourceFilePath, String strSourceFileName, String strDownFileName) {

        return downloadFile( request, response, out, strSourceFilePath, strSourceFileName, strDownFileName, false );
    }


    /**
     * 파일 다운로드
     *
     * @param request
     * @param response
     * @param out
     * @param strSourceFilePath
     * @param strSourceFileName
     * @param strDownFileName
     * @param sourceFileDelete
     * @return
     */
    private static boolean downloadFile(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String strSourceFilePath, String strSourceFileName, String strDownFileName, boolean sourceFileDelete) {

        if( request           == null ||
            response          == null ||
            out               == null ||
            strSourceFilePath == null || strSourceFilePath.isEmpty() ||
          /*strSourceFileName == null || strSourceFileName.isEmpty()*/
            strDownFileName   == null || strDownFileName  .isEmpty() ) {

            return false;

        }

        final String downFileName = strDownFileName;

        /*
        String downFileName = strSourceFileName;
        //
        if(strDownFileName != null && strDownFileName.length() > 0) {

            downFileName = strDownFileName;

        }
        */
        boolean result = false;

        //-- --//

        String downFilePath = strSourceFilePath;
        //
        if( strSourceFileName != null && ! strSourceFileName.isEmpty() ) {

            downFilePath = new StringBuffer(downFilePath).append("/").append(strSourceFileName).toString();

        }

        if( isFileExists( downFilePath ) ) {

            File f = new File( downFilePath.replace("..", " ").replace("./", " ").replace("../", " ") );
            //
            byte buff[] = new byte[2048];
            //
            int bytesRead = 0;
            //
            FileInputStream fin      = null;
            BufferedInputStream bis  = null;
            ServletOutputStream fout = null;
            BufferedOutputStream bos = null;
            //
            try {
	            // Remove newline characters (CR, LF)
	            String safeDownFileName     = java.net.URLEncoder.encode( downFileName, "utf-8" );
	            safeDownFileName            = safeDownFileName.replaceAll("\r", "").replaceAll("\n", "");

                response.reset();
                //
                response.setCharacterEncoding("utf-8");
                response.setContentType      ("application/octet-stream");
                response.setHeader           ("Cache-Control","no-store");
                response.setDateHeader       ("Expires",0);
                response.setHeader           ("Pragma","no-cache");
                response.setHeader           ("Content-Disposition", "attachment; filename=" + safeDownFileName );
                response.setHeader           ("Content-Description", "");

                //-- --//

                fin  = new FileInputStream(f);
                bis  = new BufferedInputStream(fin);
                //
                fout = response.getOutputStream();
                bos  = new BufferedOutputStream(fout);

                //-- --//

                while((bytesRead = bis.read(buff)) != -1) {

                    bis.toString();
                    //
                    bos.write(buff, 0, bytesRead);
                    //
                    bos.flush();

                }

                //-- --//

                if(bos  != null) bos .close();
                if(fout != null) fout.close();
                if(bis  != null) bis .close();
                if(fin  != null) fin .close();
                //
                if( sourceFileDelete ) {

                    f.delete();
                }
                //
                result = true;

            } catch(Exception ex) {

                log.error( ex.getMessage() );
                //
                try {

                    if(bos  != null) bos .close();
                    if(fout != null) fout.close();
                    if(bis  != null) bis .close();
                    if(fin  != null) fin .close();

                } catch (Exception e) {

                    e = null;

                }

            }

        }

        return result;

    }


}
