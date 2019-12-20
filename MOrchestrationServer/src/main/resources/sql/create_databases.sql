CREATE DATABASE IF NOT EXISTS `service_evolution`;
USE  `service_evolution`;

CREATE TABLE `services` (
    `id` varchar(100) NOT NULL COMMENT '服务ID',
    `name` varchar(100) NOT NULL COMMENT '服务名称',
    `version` varchar(100) NOT NULL COMMENT '版本号',
    `image` varchar(100) NOT NULL COMMENT '镜像地址',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;