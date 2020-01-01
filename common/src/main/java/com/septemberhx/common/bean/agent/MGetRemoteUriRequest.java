package com.septemberhx.common.bean.agent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MGetRemoteUriRequest {
    private String objectId;
    private String functionName;
    private String rawPatterns;
}
