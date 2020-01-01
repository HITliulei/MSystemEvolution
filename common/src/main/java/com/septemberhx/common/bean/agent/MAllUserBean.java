package com.septemberhx.common.bean.agent;

import com.septemberhx.common.base.user.MUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/11/23
 */
@Getter
@Setter
public class MAllUserBean {
    private List<MUser> allUserList;

    public MAllUserBean(List<MUser> allUserList) {
        this.allUserList = allUserList;
    }

    public MAllUserBean() {

    }
}
