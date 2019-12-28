package com.septemberhx.server.model;

import com.septemberhx.common.base.MUniqueObjectManager;
import com.septemberhx.common.service.MService;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
public class MServiceManager extends MUniqueObjectManager<MService> {

    public boolean registerService(MService newService) {
        boolean resultFlag = false;
        if (this.containsById(newService.getId())) {
            resultFlag = this.updateService(newService);
        } else {

        }
        return resultFlag;
    }

    public boolean updateService(MService newService) {
        return false;
    }

}
