CREATE DATABASE IF NOT EXISTS `service_evolution`;
USE  `service_evolution`;

CREATE TABLE `services` (
    `id` varchar(100) NOT NULL COMMENT '服务ID',
    `name` varchar(100) NOT NULL COMMENT '服务名称',
    `version` varchar(100) NOT NULL COMMENT '版本号',
    `image` varchar(100) COMMENT '镜像地址',
    `port` integer  NOT NULL COMMENT '端口号',
    `git` varchar(100) NOT NULL COMMENT 'git仓库地址',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `interfaces` (
    `id` varchar(100) NOT NULL COMMENT '接口ID',
    `patternUrl` varchar(100) NOT NULL COMMENT '请求路径',
    `functionName` varchar(100) NOT NULL COMMENT '函数名称',
    `requestMethod` varchar(100) NOT NULL COMMENT '请求类型',
    `returnType` varchar(10) NOT NULL COMMENT '返回值类型',
    `serviceId` varchar(100),
    `featureName` varchar(100) NOT NULL COMMENT '功能',
    `slaLevel` integer NOT NULL COMMENT 'SLA等级',
    PRIMARY KEY (`id`),
    FOREIGN KEY(`serviceId`) REFERENCES `services`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `params` (
    `name` varchar(100) NOT NULL COMMENT '参数名称',
    `request` varchar(100) NOT NULL COMMENT '请求名称',
    `defaultValue` varchar(100) NOT NULL COMMENT '默认值',
    `type` varchar(100) NOT NULL COMMENT '参数类型',
    `method` varchar(10) NOT NULL COMMENT '参数传递类型',
    `interfaceId` varchar(100),
    `order` INTEGER NOT NULL COMMENT '参数序号',
    FOREIGN KEY(`interfaceId`) REFERENCES `interfaces`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `dependency` (
    `dependencyName` VARCHAR (100) ,
    `denpendencyId` VARCHAR (100) ,
    `serviceId` VARCHAR (100) NOT NULL COMMIT '服务的id',
    `serviceDependencyName` VARCHAR (100) COMMIT '依赖的服务',
    `serviceDenpendencyInterfaceName` VARCHAR (100) COMMIT '依赖的接口',
    `serviceDependencyVersion` VARCHAR (100) COMMIT '依赖的版本',
    `functionDescribe` VARCHAR (100) COMMIT '功能描述',
    `sla` INT (10) COMMIT '等级描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `deploy` (
    `podId` VARCHAR (100) NOT NULL,
    `registerId` VARCHAR (100),
    `nodeId` VARCHAR (50) NOT NULL,
    `serviceName` VARCHAR(50) NOT NULL,
    `serviceVersion` VARCHAR (50) NOT NULL,
    `ipAddress` VARCHAR (50) NOT NULL,
    PRIMARY KEY (`podId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
