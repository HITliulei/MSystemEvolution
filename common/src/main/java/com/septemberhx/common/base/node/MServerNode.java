package com.septemberhx.common.base.node;

import com.septemberhx.common.base.MPosition;
import com.septemberhx.common.base.MResource;
import com.septemberhx.common.base.MUniqueObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MServerNode extends MUniqueObject {
    private ServerNodeType nodeType;
    private MResource resource;
    private MPosition position;
    private Long delay;
    private Long bandwidth;
    private String ip;
    private String clusterId;

    @Override
    public String toString() {
        return "MServerNode{" +
                "nodeType=" + nodeType +
                ", resource=" + resource +
                ", position=" + position +
                ", delay=" + delay +
                ", bandwidth=" + bandwidth +
                ", ip='" + ip + '\'' +
                ", clusterId='" + clusterId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
