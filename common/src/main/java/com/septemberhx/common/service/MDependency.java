package com.septemberhx.common.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Lei on 2019/12/30 15:51
 */
@Getter
@Setter
@ToString
public class MDependency {
    private String serviceName;
    private String patternUrl;
    private List<MServiceVersion> versions;
}
