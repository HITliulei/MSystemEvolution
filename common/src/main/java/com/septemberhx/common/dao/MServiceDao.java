package com.septemberhx.common.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/20
 */
@Getter
@Setter
public class MServiceDao {
    private String serviceId;
    private String serviceName;
    private String serviceVersion;
    private String serviceImage;

    @Override
    public String toString() {
        return "MServiceDao{" +
                "serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", serviceImage='" + serviceImage + '\'' +
                '}';
    }

    public MServiceDao() { }

    public MServiceDao(String serviceId, String serviceName, String serviceVersion, String serviceImage) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.serviceImage = serviceImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MServiceDao that = (MServiceDao) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(serviceVersion, that.serviceVersion) &&
                Objects.equals(serviceImage, that.serviceImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, serviceName, serviceVersion, serviceImage);
    }
}
