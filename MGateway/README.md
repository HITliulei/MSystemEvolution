# MGateway
微服务网关——微服务之间/外部用户调用均通过此网关  
此网关对微服务进行负载
## 结构
- MGetExample：工具类，提供路由等信息
- Routing：网关过滤器，获取request信息并进行处理，为用户/微服务提供接口路由
## 使用说明
- 输入信息为注册中心内部以及外部用户对注册中心内部微服务的调用request
- 调用微服务接口由MClientFramework中的utils工具提供