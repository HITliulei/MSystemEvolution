package com.septemberhx.server.dao;

import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MServiceVersion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/20
 */
@Getter
@Setter
@ToString
public class MServiceDao {
    private String serviceId;
    private String serviceName;
    private String serviceVersion;
    private String serviceImage;
    private Integer port;
    private String git;

    public MServiceDao() { }

    public MServiceDao(String serviceId, String serviceName, String serviceVersion, String serviceImage, Integer port, String git) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.serviceImage = serviceImage;
        this.port = port;
        this.git = git;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MServiceDao that = (MServiceDao) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(serviceVersion, that.serviceVersion) &&
                Objects.equals(serviceImage, that.serviceImage) &&
                Objects.equals(port, that.port) &&
                Objects.equals(git, that.git);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, serviceName, serviceVersion, serviceImage, port, git);
    }

    public static MServiceDao fromDto(MService service) {
        return new MServiceDao(
                service.getId(),
                service.getServiceName(),
                service.getServiceVersion().toString(),
                service.getImageUrl(),
                service.getPort(),
                service.getGitUrl()
        );
    }

    public MService toDto() {
        MService service = new MService();
        service.setId(this.serviceId);
        service.setServiceName(this.serviceName);
        service.setServiceVersion(MServiceVersion.fromStr(this.serviceVersion));
        service.setImageUrl(this.serviceImage);
        service.setPort(this.port);
        service.setGitUrl(this.git);
        return service;
    }
}
