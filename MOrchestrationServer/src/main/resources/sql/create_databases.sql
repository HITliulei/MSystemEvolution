CREATE DATABASE IF NOT EXISTS `service_evolution`;
USE  `service_evolution`;

CREATE TABLE `services` (
    `id` varchar(100) NOT NULL COMMENT '服务ID',
    `name` varchar(100) NOT NULL COMMENT '服务名称',
    `version` varchar(100) NOT NULL COMMENT '版本号',
    `image` varchar(100) NOT NULL COMMENT '镜像地址',
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
    `serviceId` varchar(100),
    `order` INTEGER NOT NULL COMMENT '参数序号',
    FOREIGN KEY(`interfaceId`) REFERENCES `interfaces`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
