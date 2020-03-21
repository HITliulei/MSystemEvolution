package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.bean.MRoutingBean;
import com.septemberhx.common.bean.gateway.MDepReplaceRequestBean;
import com.septemberhx.common.bean.server.MRedirectInfo;
import com.septemberhx.common.bean.server.dep.MDepUserRequestBean;
import com.septemberhx.common.config.MConfig;
import com.septemberhx.common.service.MSvcInstance;
import com.septemberhx.common.utils.MRequestUtils;
import com.septemberhx.common.utils.MUrlUtils;
import com.septemberhx.server.model.MServerSkeleton;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/7
 */
public class MSvcDepController {

    @PostMapping(path = MConfig.MSERVER_NEW_DEP_REQUEST)
    @ResponseBody
    public MRedirectInfo processNewRequest(@RequestBody MDepUserRequestBean userRequestBean) {

        return null;
    }

    @ResponseBody
    @PostMapping(path = MConfig.MSERVER_REPLACE_CALL)
    public MResponse processReplacementRequest(@RequestBody MDepReplaceRequestBean requestBean, HttpServletRequest request) {
        MRoutingBean routingBean = requestBean.getRawRoutingBean();
        Optional<MSvcInstance> svcInstOpt = MServerSkeleton.getCurrInstManager().getById(requestBean.getReplacementId());
        if (svcInstOpt.isPresent()) {
            routingBean.setIpAddr(svcInstOpt.get().getIp());
            return MRequestUtils.sendRequest(
                    MUrlUtils.getRemoteUri(routingBean),
                    requestBean.getParam(),
                    MResponse.class,
                    RequestMethod.POST,
                    createHeader(request.getHeader(MConfig.PARAM_CALLER_URL), request.getHeader(MConfig.PARAM_CALLED_URL))
            );
        } else {
            return MResponse.failResponse();
        }
    }

    private Map<String, List<String>> createHeader(String callerUrl, String calledUrl) {
        Map<String, List<String>> customHeaders = new HashMap<>();
        List<String> p1 = new ArrayList<>();
        p1.add(callerUrl);
        List<String> p2 = new ArrayList<>();
        p2.add(calledUrl);
        customHeaders.put(MConfig.PARAM_CALLER_URL, p1);
        customHeaders.put(MConfig.PARAM_CALLED_URL, p2);
        return customHeaders;
    }
}
