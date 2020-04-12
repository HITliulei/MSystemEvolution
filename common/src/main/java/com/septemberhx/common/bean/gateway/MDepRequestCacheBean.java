package com.septemberhx.common.bean.gateway;

import com.septemberhx.common.bean.MResponse;
import com.septemberhx.common.service.dependency.BaseSvcDependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/9
 */
@Getter
@Setter
@ToString
public class MDepRequestCacheBean implements Comparable<MDepRequestCacheBean> {
    /*
     * The dependency description
     */
    private BaseSvcDependency baseSvcDependency;

    /*
     * Who this request belongs to
     */
    private String clientId;

    /*
     * When the system receive the request in mill seconds
     */
    private long timestamp;

    private String nodeId;

    private MResponse parameters;

    public MDepRequestCacheBean() { }

    public MDepRequestCacheBean(BaseSvcDependency baseSvcDependency, String clientId, long timestamp, MResponse parameters, String nodeId) {
        this.baseSvcDependency = baseSvcDependency;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.parameters = parameters;
        this.nodeId = nodeId;
    }

    @Override
    public int compareTo(MDepRequestCacheBean o) {
        return Long.compare(this.timestamp, o.timestamp);
    }
}
