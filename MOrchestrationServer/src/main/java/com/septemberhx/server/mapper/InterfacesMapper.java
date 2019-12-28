package com.septemberhx.server.mapper;

import com.septemberhx.server.dao.MInterfaceDao;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/28
 */
public interface InterfacesMapper {

    @Select("SELECT * FROM interfaces")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "patternUrl", column = "patternUrl"),
            @Result(property = "functionName", column = "functionName"),
            @Result(property = "requestMethod", column = "requestMethod"),
            @Result(property = "returnType", column = "returnType"),
            @Result(property = "serviceId", column = "serviceId"),
            @Result(property = "featureName", column = "featureName"),
            @Result(property = "slaLevel", column = "slaLevel")
    })
    List<MInterfaceDao> getAll();

    @Select("SELECT * FROM interfaces WHERE serviceId = #{serviceId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "patternUrl", column = "patternUrl"),
            @Result(property = "functionName", column = "functionName"),
            @Result(property = "requestMethod", column = "requestMethod"),
            @Result(property = "returnType", column = "returnType"),
            @Result(property = "serviceId", column = "serviceId"),
            @Result(property = "featureName", column = "featureName"),
            @Result(property = "slaLevel", column = "slaLevel")
    })
    List<MInterfaceDao> getByServiceId(String serviceId);

    @Insert("INSERT INTO interfaces (id, patternUrl, functionName, requestMethod, returnType, serviceId, featureName, slaLevel)" +
            " VALUES(#{id}, #{patternUrl}, #{functionName}, #{requestMethod}, #{returnType}, #{serviceId}, #{featureName}, #{slaLevel});\n")
    void insert(MInterfaceDao interfaceDao);

    @Delete("DELETE FROM interfaces WHERE serviceId = #{serviceId}")
    void deleteByServiceId(String serviceId);
}
