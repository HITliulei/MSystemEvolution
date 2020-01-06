package com.septemberhx.server.mapper;

import com.septemberhx.server.dao.MServiceDao;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

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
            @Result(property = "serviceImage", column = "image"),
            @Result(property = "port", column = "port"),
            @Result(property = "git", column = "git")
    })
    List<MServiceDao> getAll();

    @Select("SELECT * FROM services WHERE id = #{serviceId}")
    @Results({
            @Result(property = "serviceId", column = "id"),
            @Result(property = "serviceName", column = "name"),
            @Result(property = "serviceVersion", column = "version"),
            @Result(property = "serviceImage", column = "image"),
            @Result(property = "port", column = "port"),
            @Result(property = "git", column = "git")
    })
    MServiceDao getById(String serviceId);

    @Insert("INSERT INTO services(id, name, version, image, port, git)" +
            " VALUES(#{serviceId}, #{serviceName}, #{serviceVersion}, #{serviceImage}, #{port}, #{git})")
    void insert(MServiceDao serviceDao);

    @Select("SELECT * FROM services WHERE name = #{serviceName}")
    @Results({
            @Result(property = "serviceId", column = "id"),
            @Result(property = "serviceName", column = "name"),
            @Result(property = "serviceVersion", column = "version"),
            @Result(property = "serviceImage", column = "image"),
            @Result(property = "port", column = "port"),
            @Result(property = "git", column = "git")
    })
    List<MServiceDao> getByName(String serviceName);

    @Delete("DELETE FROM services WHERE id = #{serviceId}")
    void deleteById(String serviceId);

    @Update("UPDATE services SET name = #{serviceName}, version = #{serviceVersion}, image = #{serviceImage}, port = #{port}, git = #{git} WHERE id = #{serviceId}")
    void update(MServiceDao serviceDao);

    @Update("UPDATE services SET image = #{imageUrl} WHERE id = #{serviceId}")
    void updateImageUrl(String serviceId, String imageUrl);
}
