package com.septemberhx.server.mapper;

import com.septemberhx.common.dao.MServiceDao;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/20
 */
public interface ServicesMapper {
    @Select("SELECT * FROM services")
    @Results({
            @Result(property = "serviceId", column = "id"),
            @Result(property = "serviceName", column = "name"),
            @Result(property = "serviceVersion", column = "version"),
            @Result(property = "serviceImage", column = "image")
    })
    List<MServiceDao> getAll();

    @Select("SELECT * FROM services WHERE id = #{serviceId}")
    @Results({
            @Result(property = "serviceId", column = "id"),
            @Result(property = "serviceName", column = "name"),
            @Result(property = "serviceVersion", column = "version"),
            @Result(property = "serviceImage", column = "image")
    })
    MServiceDao getById(String serviceId);

    @Insert("INSERT INTO services(id, name, version, image) VALUES(#{serviceId}, #{serviceName}, #{serviceVersion}, #{serviceImage})")
    void insert(MServiceDao serviceDao);

    @Select("SELECT * FROM services WHERE name = #{serviceName}")
    @Results({
            @Result(property = "serviceId", column = "id"),
            @Result(property = "serviceName", column = "name"),
            @Result(property = "serviceVersion", column = "version"),
            @Result(property = "serviceImage", column = "image")
    })
    List<MServiceDao> getByName(String serviceName);

    @Delete("DELETE FROM services WHERE id = #{serviceId}")
    void deleteById(String serviceId);

    @Update("UPDATE services SET name = #{serviceName}, version = #{serviceVersion}, image = #{serviceImage} WHERE id = #{serviceId}")
    void update(MServiceDao serviceDao);
}
