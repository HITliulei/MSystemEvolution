package com.septemberhx.server.mapper;

import com.septemberhx.server.dao.MServiceDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/20
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ServicesMapperTest {

    @Autowired
    private ServicesMapper servicesMapper;

//    @Test
    public void getAll() {
        List<MServiceDao> serviceDaoList = servicesMapper.getAll();
        for (MServiceDao serviceDao : serviceDaoList) {
            System.out.println(serviceDao.toString());
        }
    }

//    @Test
    public void test1() {
        MServiceDao serviceDao = new MServiceDao(
                "service-test-123321",
                "service-test",
                "1.1.2",
                "septemberhx/service-test",
                8080,
                "git"
        );
        int rawSize = servicesMapper.getByName(serviceDao.getServiceName()).size();

        servicesMapper.insert(serviceDao);

        MServiceDao result = servicesMapper.getById(serviceDao.getServiceId());
        assert result.equals(serviceDao);

        List<MServiceDao> resultList = servicesMapper.getByName(serviceDao.getServiceName());
        assert resultList.size() == 1 + rawSize;
        assert servicesMapper.getAll().size() == 1;

        result.setServiceImage("hello-world");
        servicesMapper.update(result);
        assert servicesMapper.getById(result.getServiceId()).getServiceImage().equals(result.getServiceImage());

        servicesMapper.deleteById(serviceDao.getServiceId());
        result = servicesMapper.getById(serviceDao.getServiceId());
        assert result == null;
    }
}