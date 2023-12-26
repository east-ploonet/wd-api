package com.saltlux.aice_fe.pc.notice.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.utils.FileUtils;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.notice.dao.NoticeDao;
import com.saltlux.aice_fe.pc.notice.service.NoticeService;
import com.saltlux.aice_fe.pc.notice.vo.NoticeDetailVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeFileVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoticeServiceImpl extends BaseServiceImpl implements NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private FileUploadService fileService;

    @Override
    public Map<String, Object> findNoticeCategory() {
        Map<String, Object> result          = new HashMap<>();
        List<Map<String, Object>> listMap   = new ArrayList<>();

        List<NoticeVo> noticeCategory = noticeDao.findNoticeCategory();

        if(noticeCategory == null){
            throwException.statusCode(204);

        } else {
            for (NoticeVo noticeVo:noticeCategory){
                Map<String, Object> mapAdd = new HashMap<>();
                mapAdd.put("pk_notice_category" , noticeVo.getPk_notice_category());
                mapAdd.put("fd_category_name" , noticeVo.getFd_category_name());
                listMap.add(mapAdd);
            }
        }
        result.put("noticeCategory",listMap);
        return result;
    }

    @Override
    public void save(NoticeVo noticeVo) {
        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

        noticeVo.setLoginCompanyStaffPk(loginInfoVo.getLoginCompanyStaffPk());
        noticeDao.save(noticeVo);
    }

    @Override
    public Page<NoticeVo> getNotice(Pageable page, String query) {
        Map<String, Object> paramMap= new HashMap<>();
        paramMap.put("offset", page.getOffset());
        paramMap.put("pageSize", page.getPageSize());
//        paramMap.put("type",type);
        paramMap.put("query",query);
        LocalDateTime localDateTime = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        paramMap.put("start",localDateTime);


        List<NoticeVo> noticeVoList= noticeDao.getNotice(paramMap);

        List<Long> noticeIdList = new ArrayList<>();

        for (NoticeVo notice: noticeVoList) {
            noticeIdList.add(notice.getPk_notice());
        }

        if (noticeIdList.size()>0){
            List<NoticeFileVo> noticeFiles = noticeDao.getNoticeFiles(noticeIdList);
            Map<Long, List<NoticeFileVo>> collect = noticeFiles.stream().collect(Collectors.groupingBy(item -> item.getFk_notice()));
            for (NoticeVo notice: noticeVoList) {
                if (collect.get(notice.getPk_notice())!=null){
                    notice.setFileVos(collect.get(notice.getPk_notice()));;
                };
            }

        }


        Integer cnt =noticeDao.getNoticeCnt(paramMap);
        if (cnt==null){
            cnt =0;
        }

        return new PageImpl<>(noticeVoList,page,cnt);
    }

//    @Override
//    public void registerFiles(Map<String, Object> paramMap, Long fk_notice) throws Exception {
//
//        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
//        paramMap.put("staffId",loginInfoVo.getLoginCompanyStaffPk());
//
//        MultipartFile[] uploadFile = (MultipartFile[]) paramMap.get("uploadFile");
//        List<NoticeFileVo> noticeFileVoList=new ArrayList<>();
//
//
//
//            if( uploadFile != null && uploadFile.length > 0) {
//
//                for (MultipartFile file:uploadFile) {
//                    String uploadFilePath = FileUtils.getUploadFilePath(FileUtils.FileUploadType.NOTICE, file.getOriginalFilename());
//                    String url = fileService.uploadFileToS3(file, uploadFilePath);
//                    NoticeFileVo noticeVo=new NoticeFileVo();
//                    noticeVo.setFk_notice(fk_notice);
//                    noticeVo.setFd_file_name(file.getOriginalFilename());
//                    noticeVo.setFd_file_path(url);
//                    noticeVo.setFd_file_size(String.valueOf(file.getSize()));
//                    noticeVo.setFd_mime_code(file.getContentType());
//                    noticeVo.setFd_file_type("file");
//                    noticeFileVoList.add(noticeVo);
//                }
//
//            }
//
//        MultipartFile[] uploadImageFile = (MultipartFile[]) paramMap.get("uploadImageFile");
//
//        if( uploadImageFile != null && uploadImageFile.length > 0) {
//
//            for (MultipartFile file:uploadFile) {
//                String uploadFilePath = FileUtils.getUploadFilePath(FileUtils.FileUploadType.NOTICE, file.getOriginalFilename());
//                String url = fileService.uploadFileToS3(file, uploadFilePath);
//                NoticeFileVo noticeVo=new NoticeFileVo();
//                noticeVo.setFk_notice(fk_notice);
//                noticeVo.setFd_file_name(file.getOriginalFilename());
//                noticeVo.setFd_file_path(url);
//                noticeVo.setFd_file_size(String.valueOf(file.getSize()));
//                noticeVo.setFd_mime_code(file.getContentType());
//                noticeVo.setFd_file_type("Image");
//                noticeFileVoList.add(noticeVo);
//            }
//
//        }
//
//        paramMap.put("noticeFileVoList",noticeFileVoList);
//        noticeDao.registerNoticeFile(paramMap);
//    }

    @Override
    public Map<String, Object> getDetailNotice(Long pk_notice) {
        Map<String, Object> paramMap= new HashMap<>();
        NoticeDetailVo noticeDetailVo = noticeDao.getDetailNotice(pk_notice);
        List<Long> idList =noticeDao.getBeforeFrontId(noticeDetailVo.getPk_notice());

        List<NoticeFileVo> detailNoticeFiles = noticeDao.getDetailNoticeFiles(noticeDetailVo.getPk_notice());
        noticeDetailVo.setFileVos(detailNoticeFiles);
        for (Long id: idList) {
            if (id>noticeDetailVo.getPk_notice()){
                noticeDetailVo.setFrontId(id);
            }
            if (id<noticeDetailVo.getPk_notice()){
                noticeDetailVo.setBeforeId(id);
            }
        }
        paramMap.put("notice",noticeDetailVo);
        return paramMap;
    }

    @Override
    public NoticeFileVo getNoticeFile(Long pk_notice_file_id) {
        NoticeFileVo noticeFileVo = noticeDao.getNoticeFileById(pk_notice_file_id);
        return noticeFileVo;
    }





}
