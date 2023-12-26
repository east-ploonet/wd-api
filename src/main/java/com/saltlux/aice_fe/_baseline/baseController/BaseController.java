package com.saltlux.aice_fe._baseline.baseController;

import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.commonCode.service.CodeService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginAdminInfoVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@CrossOrigin("*")
@ResponseStatus(HttpStatus.OK)
@Log4j2
public class BaseController {

    protected final String PRODUCES_JSON = "application/json; charset=UTF-8";

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected ThrowException throwException;

    @Autowired
    protected CodeService codeService;

    @Value("${path.file.upload}")
    protected String pathFileUpload;

    @Value("${push.inbound.url}")
    protected String pushInboundUrl;

    @Value("${path.browser.storage}")
    private String pathBrowserStorage;


    public PcLoginInfoVo getPcLoginInfoVo() {

        PcLoginInfoVo pcLoginInfoVo;
        if (request.getSession().getAttribute("pcLoginInfo") != null) {
            return ((PcLoginInfoVo) request.getSession().getAttribute("pcLoginInfo"));
        } else {
            return null;
        }
//		컨트롤러에서 다음과 같이 사용 : PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
    }

    public AppLoginInfoVo getAppLoginInfoVo() {

        AppLoginInfoVo appLoginInfoVo;

        if (request.getSession().getAttribute("appLoginInfo") != null) {
            return ((AppLoginInfoVo) request.getSession().getAttribute("appLoginInfo"));
        } else {
            return null;
        }
//		컨트롤러에서 다음과 같이 사용 : AppLoginInfoVo appLoginInfoVo = getAppLoginInfoVo();
    }

    public PcLoginAdminInfoVo getPcLoginAdminInfoVo() {

        PcLoginAdminInfoVo pcLoginInfoVo;
        HttpSession session = request.getSession();

        if (request.getSession().getAttribute("pcAdminInfo") != null) {
            pcLoginInfoVo = ((PcLoginAdminInfoVo) request.getSession().getAttribute("pcAdminInfo"));
        } else {
            pcLoginInfoVo = null;
        }
        return pcLoginInfoVo;
//		컨트롤러에서 다음과 같이 사용 : PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
    }
}
