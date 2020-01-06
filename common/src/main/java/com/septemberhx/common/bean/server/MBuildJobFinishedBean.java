package com.septemberhx.common.bean.server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/1/6
 *
 * This class is used when the build center finishes a build job, and it will be sent to the server
 */
@Getter
@Setter
@ToString
public class MBuildJobFinishedBean {
    private String id;
    private Boolean success;
    private String imageUrl;

    public boolean isSuccess() {
        return success != null && success;
    }
}
