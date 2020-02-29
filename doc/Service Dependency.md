### 微服务依赖

#### 依赖说明

包含两部分：
* 用户与微服务之间
* 微服务与微服务之间

均包含一下三种依赖情况：

1. 版本依赖：`SvcVerDependency`
   * 服务名称
   * 接口
   * 版本
2. 服务依赖：`SvcSlaDependency`
   * 服务
   * 接口
   * SLA
3. 功能及SLA依赖：`SvcFuncDependency`
   * 功能
   * SLA

#### 依赖配置

需要在 `application.yaml` 中进行配置：

```yaml
mvf4ms:
	version: 版本号  # major.minor.fix 形式，不带 'v'
	dependencies:
		- name: 依赖名称
			- service: 服务名称
			  interface: 接口名称
			  version: 服务版本
			  function: 功能
			  sla: SLA等级
			  # 这些属性不会同时出现，具体参考下面的 [依赖情况]
            - service: 服务名称  # 可能会依赖于多个服务
			  interface: 接口名称
			  version: 服务版本
			  function: 功能
			  sla: SLA等级
			...
		- name: 依赖名称  # 可能有不同的依赖组合
			- service: 服务名称
			  interface: 接口名称
			  version: 服务版本
			  function: 功能
			  sla: SLA等级
            - service: 服务名称
			  interface: 接口名称
			  version: 服务版本
			  function: 功能
			  sla: SLA等级
			...
```