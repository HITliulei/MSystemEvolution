# MClusterAgent

对整个集群进行统一的控制、接收外部指令以及监控集群状态。主要涉及到 1) Eureka; 2) Kubernetes Server API

## 接口

详情请参考 `MAgentController`

* `/magent/doRequest`：接收 `MUserRequestBean`，并按照指定的实例ID，将请求转发给指定实例
* `/magent/fetchRequestUrl`：接收来自 `MGateway` 的 `MUserDemand`，向 `MOrchestartionServer` 询问处理该需求的实例ID
* `/magent/updateGateways`：接收来自 `MOrchestrationServer` 的 `MUpdateCacheBean`，目标是将携带的路由表分发给集群内部的全部 `MGateway` 
* `/magent/allUser`：从集群内部的所有 `MGateway` 中，收集全部的用户信息
* `/magent/instanceInfoList`：获取集群内部所有服务实例的相关信息
* `/magent/deleteInstance`：删除指定的实例
* `/magent/deploy`：在指定节点上，部署指定的服务实例
* `/magent/registered`：接收来自 `MEurekaServer` 的实例信息，并在服务实例变更时，向 `MOrchestrationServer` 汇报

## 运行

* 需要修改 application.yaml 中的 mclientagent.server, mclientagent.elasticsearch 与 eureka 地址
* 所有部署的服务实例都利用了 `./yaml/template.yaml` 这个模板文件，请按需更改

## 注意

* 默认日志都写在了 `/var/mclient/log` 下（参见 `MLogUtils` 下），所以需要在 yaml 模板里面，挂载一个本地目录，这样就能够收集该节点上所有实例的日志
* **不要在部署实例的时候，在给定的实例 ID 中包含 `-` 号 !!!!**，Kubernetes 不支持