package com.septemberhx.common.base.user;

import com.septemberhx.common.base.MUniqueObject;
import com.septemberhx.common.service.MFunc;
import com.septemberhx.common.service.MSla;
import com.septemberhx.common.service.MSvcInterface;
import lombok.Getter;
import lombok.Setter;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/9/11
 */
@Getter
@Setter
public class MUserDemand extends MUniqueObject {
    private String userId;      // user id
    private String functionId;  // unique ID for functions. It will be used when try to map demands to services
    private MSla sla;   // the SLA level that users expect for
    private String serviceId;   // service name actually

    public MUserDemand(String userId, String functionId, Integer sla, String serviceId) {
        this.userId = userId;
        this.functionId = functionId;
        this.sla = new MSla(sla);
        this.id = userId + "_" + functionId + "_" + sla;
        this.serviceId = serviceId;
    }

    public MUserDemand() {}

    public boolean isDemandMet(MFunc functionIdProvided, MSla slaLevelProvided) {
        return functionIdProvided.getFunctionName().equals(functionId) && slaLevelProvided == sla;
    }

    public boolean isServiceInterfaceMet(MSvcInterface serviceInterface) {
        if (this.serviceId != null && !serviceInterface.getServiceId().startsWith(this.serviceId)) return false;
        return this.isDemandMet(serviceInterface.getFuncDescription().getFunc(), serviceInterface.getFuncDescription().getSla());
    }

    @Override
    public String toString() {
        return "MUserDemand{" +
                "userId='" + userId + '\'' +
                ", functionId='" + functionId + '\'' +
                ", slaLevel=" + sla +
                ", serviceId='" + serviceId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
