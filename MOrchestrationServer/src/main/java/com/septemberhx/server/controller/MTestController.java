package com.septemberhx.server.controller;

import com.septemberhx.common.bean.MTimeIntervalBean;
import com.septemberhx.server.adaptation.MDepAdaptation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/4/12
 */
@RestController
@RequestMapping(value = "/test")
public class MTestController {

    @PostMapping(value = "/depEvolve")
    public void depEvolveTest(MTimeIntervalBean intervalBean) {
        MDepAdaptation adaptation = new MDepAdaptation();
        adaptation.evolve(intervalBean);
    }
}
