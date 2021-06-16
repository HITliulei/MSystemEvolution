package com.septemberhx.server.dao;

import com.septemberhx.common.service.MParamer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/28
 */
@Getter
@Setter
@ToString
public class MParamDao {
    private String interfaceId;
    private String name;
    private String request;
    private String defaultValue;
    private String type;
    private String method;
    private Integer order;

    public MParamDao(String interfaceId, String name, String request, String defaultValue, String type, String method, Integer order) {
        this.interfaceId = interfaceId;
        this.name = name;
        this.request = request;
        this.defaultValue = defaultValue;
        this.type = type;
        this.method = method;
        this.order = order;
    }

    public MParamDao() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MParamDao mParamDao = (MParamDao) o;
        return Objects.equals(interfaceId, mParamDao.interfaceId) &&
                Objects.equals(name, mParamDao.name) &&
                Objects.equals(request, mParamDao.request) &&
                Objects.equals(defaultValue, mParamDao.defaultValue) &&
                Objects.equals(type, mParamDao.type) &&
                Objects.equals(method, mParamDao.method) &&
                Objects.equals(order, mParamDao.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interfaceId, name, request, defaultValue, type, method, order);
    }

    public static MParamDao fromDto(String interfaceId, MParamer paramer, Integer order) {
        return new MParamDao(
                interfaceId,
                paramer.getName(),
                paramer.getRequestname(),
                paramer.getDefaultObject(),
                paramer.getType(),
                paramer.getMethod(),
                order
        );
    }

    public MParamer toDto() {
        MParamer paramer = new MParamer();
        paramer.setName(this.name);
        paramer.setRequestname(this.request);
        paramer.setDefaultObject(this.defaultValue);
        paramer.setType(this.type);
        paramer.setMethod(this.method);
        return paramer;
    }
}
