package com.septemberhx.common.bean.server;

import com.septemberhx.common.service.MServiceVersion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/28
 *
 * From Server to Service Analyser
 */
@Getter
@Setter
@ToString
public class MFetchServiceInfoBean {
    /**
     * the git repository address
     **/
    private String gitUrl;

    /**
     * target version
     **/
    private MServiceVersion version;

    /**
     * Since the analyser takes some time to analyse the source code,
     * the analyser should return the result to the callBackUrl latter.
     */
    private String callBackUrl;
}
