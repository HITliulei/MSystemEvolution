# MEurekaServer

定制版本的 eureka 服务，相较于原版，做出下列改动：

* 当一个服务实例的状态发生变化（上线、下线等）时，向 `MClusterAgent` 发送该服务实例的相关信息，类型为`MInstanceRegisterNotifyRequest`

## 使用

* `kubectl apply -f ./eureka-server.yaml` 即可，NodePort为 **30761**