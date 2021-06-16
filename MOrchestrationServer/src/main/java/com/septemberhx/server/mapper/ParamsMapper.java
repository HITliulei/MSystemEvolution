package com.septemberhx.server.mapper;

import com.septemberhx.server.dao.MParamDao;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/28
 */
public interface ParamsMapper {
    @Select("SELECT * FROM params")
    @Results({
            @Result(property = "interfaceId", column = "interfaceId"),
            @Result(property = "name", column = "name"),
            @Result(property = "request", column = "request"),
            @Result(property = "defaultValue", column = "defaultValue"),
            @Result(property = "type", column = "type"),
            @Result(property = "method", column = "method"),
            @Result(property = "order", column = "order")
    })
    List<MParamDao> getAll();

    @Select("SELECT * FROM params WHERE interfaceId = #{interfaceId}")
    @Results({
            @Result(property = "interfaceId", column = "interfaceId"),
            @Result(property = "name", column = "name"),
            @Result(property = "request", column = "request"),
            @Result(property = "defaultValue", column = "defaultValue"),
            @Result(property = "type", column = "type"),
            @Result(property = "method", column = "method"),
            @Result(property = "order", column = "order")
    })
    List<MParamDao> getByInterfaceId(String interfaceId);

    @Insert("INSERT INTO params (interfaceId, name, request, defaultValue, `type`, `method`, `order`)" +
            " VALUES (#{interfaceId}, #{name}, #{request}, #{defaultValue}, #{type}, #{method}, #{order})")
    void insert(MParamDao paramDao);

    @Delete("DELETE FROM params WHERE interfaceId = #{interfaceId}")
    void deleteByInterfaceId(String interfaceId);
}
