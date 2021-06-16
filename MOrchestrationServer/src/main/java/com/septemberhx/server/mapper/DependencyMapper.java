package com.septemberhx.server.mapper;

import com.septemberhx.common.service.MDependency;
import com.septemberhx.server.dao.MDependencyDao;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author Lei
 * @Date 2020/3/15 14:34
 * @Version 1.0
 */
public interface DependencyMapper {
    @Select("SELECT * FROM dependency")
    @Results({
            @Result(property = "dependencyName", column = "dependencyName"),
            @Result(property = "dependencyId", column = "denpendencyId"),
            @Result(property = "serviceId", column = "serviceId"),
            @Result(property = "serviceDenpendencyName", column = "serviceDependencyName"),
            @Result(property = "serviceDependencyInterfaceName", column = "serviceDenpendencyInterfaceName"),
            @Result(property = "serviceDenpendencyVersion", column = "serviceDependencyVersion"),
            @Result(property = "functionDescribe", column = "functionDescribe"),
            @Result(property = "functionLevel", column = "sla"),
    })
    public List<MDependencyDao> selectAlldependency();


    @Select("SELECT * FROM dependency WHERE serviceId=#{serviceId}")
    @Results({
            @Result(property = "dependencyName", column = "dependencyName"),
            @Result(property = "dependencyId", column = "denpendencyId"),
            @Result(property = "serviceId", column = "serviceId"),
            @Result(property = "serviceDenpendencyName", column = "serviceDependencyName"),
            @Result(property = "serviceDependencyInterfaceName", column = "serviceDenpendencyInterfaceName"),
            @Result(property = "serviceDenpendencyVersion", column = "serviceDependencyVersion"),
            @Result(property = "functionDescribe", column = "functionDescribe"),
            @Result(property = "functionLevel", column = "sla"),
    })
    public List<MDependencyDao> getServiceDenpendency(@Param("serviceId") String serviceId);

    @Select("SELECT * FROM dependency WHERE dependencyName=#{dependencyName} and denpendencyId=#{dependencyId}")
    @Results({
            @Result(property = "dependencyName", column = "dependencyName"),
            @Result(property = "dependencyId", column = "denpendencyId"),
            @Result(property = "serviceId", column = "serviceId"),
            @Result(property = "serviceDenpendencyName", column = "serviceDependencyName"),
            @Result(property = "serviceDependencyInterfaceName", column = "serviceDenpendencyInterfaceName"),
            @Result(property = "serviceDenpendencyVersion", column = "serviceDependencyVersion"),
            @Result(property = "functionDescribe", column = "functionDescribe"),
            @Result(property = "functionLevel", column = "sla"),
    })
    public MDependencyDao getServiceDependencyDaoByNameAndId(@Param("dependencyName") String name, @Param("dependencyId") String id);

    @Insert("INSERT INTO dependency " +
            "(dependencyName, denpendencyId, serviceId, serviceDependencyName, serviceDenpendencyInterfaceName, serviceDependencyVersion, functionDescribe, sla)"
            +
            " VALUES " +
            "(#{dependencyName}, #{dependencyId}, #{serviceId}, #{serviceDenpendencyName}, #{serviceDependencyInterfaceName}, #{serviceDenpendencyVersion}, #{functionDescribe}, #{functionLevel})")
    void insert(MDependencyDao mDependencyDao);


    @Update("UPDATE dependency SET serviceDenpendencyName = #{serviceDenpendencyName}, serviceDependencyInterfaceName = #{serviceDependencyInterfaceName}, serviceDenpendencyVersion = #{serviceDenpendencyVersion} WHERE dependencyName = #{dependencyName} and dependencyId=#{dependencyId}")
    public void updateDependency(MDependencyDao mDependencyDao);

    @Delete("DELETE FROM dependency WHERE serviceId = #{serviceId}")
    public void deleteServiceDenpendency(@Param("serviceId") String serviceId);

    @Delete("DELETE FROM dependency WHERE dependencyName = #{name} AND denpendencyId={id}")
    public void deleteByNameAndId(@Param("name") String name, @Param("id")String id);
}
