package com.septemberhx.server.controller;

import com.septemberhx.common.bean.server.MRedirectInfo;
import com.septemberhx.common.bean.server.dep.MDepUserRequestBean;
import com.septemberhx.common.config.MConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
