package com.septemberhx.server.mapper;

import com.septemberhx.server.dao.MDeployDao;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author Lei
 * @Date 2020/3/16 19:49
 * @Version 1.0
 */
public interface MDeployMapper {

    @Select("SELECT * FROM deploy")
    @Results({
            @Result(property = "podId", column = "podId"),
            @Result(property = "registerId", column = "registerId"),
            @Result(property = "nodeId", column = "nodeId"),
            @Result(property = "serviceName", column = "serviceName"),
            @Result(property = "serviceVersion", column = "serviceVersion"),
            @Result(property = "ipAddress", column = "ipAddress")
    })
    public List<MDeployDao> getAlldeployInfo();

    @Select("SELECT * FROM deploy WHERE podId=#{podId}")
    @Results({
            @Result(property = "podId", column = "podId"),
            @Result(property = "registerId", column = "registerId"),
            @Result(property = "nodeId", column = "nodeId"),
            @Result(property = "serviceName", column = "serviceName"),
            @Result(property = "serviceVersion", column = "serviceVersion"),
            @Result(property = "ipAddress", column = "ipAddress")
    })
    public MDeployDao getDeployInfo(String podId);


    @Select("SELECT * FROM deploy WHERE serviceName = #{serviceName}")
    @Results({
            @Result(property = "podId", column = "podId"),
            @Result(property = "registerId", column = "registerId"),
            @Result(property = "nodeId", column = "nodeId"),
            @Result(property = "serviceName", column = "serviceName"),
            @Result(property = "serviceVersion", column = "serviceVersion"),
            @Result(property = "ipAddress", column = "ipAddress")
    })
    public List<MDeployDao> getDeployByserviceName(@Param("serviceName")String serviceName);


    @Select("SELECT * FROM deploy WHERE serviceVersion = #{serviceVersion} AND serviceName = #{serviceName}")
    @Results({
            @Result(property = "unitId", column = "unitId"),
            @Result(property = "registerId", column = "registerId"),
            @Result(property = "nodeId", column = "nodeId"),
            @Result(property = "serviceName", column = "serviceName"),
            @Result(property = "serviceVersion", column = "serviceVersion"),
            @Result(property = "ipAddress", column = "ipAddress")
    })
    public List<MDeployDao> getDeployByserviceId(@Param("serviceName")String serviceName, @Param("serviceVersion")String serviceVersion);

    @Insert("INSERT INTO deploy (podId, registerId, nodeId, serviceName, serviceVersion, ipAddress) VALUES (#{podId}, #{registerId}, #{nodeId}, #{serviceName}, #{serviceVersion}, #{ipAddress})")
    public void insertDeploy(MDeployDao mDeployDao);

    @Update("UPDATE deploy SET registerId=#{registerId}, ipAddress=#{ipAddress} WHERE podId=#{podId}")
    public void updateRegister(MDeployDao mDeployDao);

    @Delete("DELETE FROM deploy WHERE podId=#{podId}")
    public void deleteById(String podId);

    @Delete("DELETE FROM deploy WHERE serviceName=#{serviceName}")
    public void deleteByName(@Param("serviceName")String serviceName);
    @Delete("DELETE FROM deploy WHERE serviceName=#{serviceName} and serviceVersion=#{serviceVersion}")
    public void delteByServiceId(@Param("serviceName")String serviceName,@Param("serviceVersion")String serviceVersion);
}
