## MRunEnvAgent
服务运行环境感知与操作的中间件服务，现独立为单一服务，主要用于抽象底层不同的实现细节，并向上提供高级API功能

### MAgentController 
K8S集群的代理，负责服务环境的感知以及操作
- /deploy：
- /deleteInstance：