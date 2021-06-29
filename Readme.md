# MSystemEvolution

[![警报](http://10.11.1.102:30900/api/project_badges/measure?project=com.septemberhx%3AMSystemEvolution&metric=alert_status)](http://192.168.1.102:30900/dashboard?id=com.septemberhx%3AMSystemEvolution)
[![Bugs](http://10.11.1.102:30900/api/project_badges/measure?project=com.septemberhx%3AMSystemEvolution&metric=bugs)](http://10.111.1.102:30900/dashboard?id=com.septemberhx%3AMSystemEvolution)
[![异味](http://10.11.1.102:30900/api/project_badges/measure?project=com.septemberhx%3AMSystemEvolution&metric=code_smells)](http://10.111.1.102:30900/dashboard?id=com.septemberhx%3AMSystemEvolution)
[![SQALE评级](http://10.11.1.102:30900/api/project_badges/measure?project=com.septemberhx%3AMSystemEvolution&metric=sqale_rating)](http://10.111.1.102:30900/dashboard?id=com.septemberhx%3AMSystemEvolution)

自适应演化的微服务系统，目标是提供一套完整的编程框架以及微服务系统，让整个微服务系统能够自动遵循 MAPE-K 模型，对整个服务系统进行 Monitor，Analyze，Plan 以及 Execute，进而让整个系统具备针对用户需求变化的自适应能力，以维持 QoS 稳定。

整体组件划分如下：
* `common`：存储在两个及以上组件使用到的相关 bean，utils，实体类 等
* `MClientFramework`：微服务编程框架
* `MClusterAgent`：集群中的agent，让集群能够接收外部指令
* `MEurekaServer`：定制版 eureka 中心
* `MGateway`：集群中的网关, 自己编写的路由规则
* `MCenterControl`：整个微服务自适应系统的中控
* `MServiceAnalyser`：从源码层面解析一个微服务项目，来自动获取微服务相关信息

## 边缘的日志收集

![](./img/structure.png)

## 使用

1. 部署 `MEurekaServer`
2. 部署 elasticsearch, logstash（参见[github](https://github.com/SeptemberHX/scripts/tree/master/yml/elasticsearch_logstash_kibana)）, 以及 每个kubernetes上都要部署一个 `MInfoCollector`
3. 部署 `MClusterAgent` 到 kubernetes master 节点上(懒得改代码，在 `MClientUtils` 里初始化 `MDockerManagerK8SImpl` 那里，可以指定 Kubernetes API Server 地址，理论上应该填上地址，这样才能够跑多个 agent 进行负载均衡)

## 部分细节