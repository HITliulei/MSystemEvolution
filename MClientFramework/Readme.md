# MClientFramework

微服务编程框架，包含如下功能：

1. 通过日志方式，让服务具备运行时的状态汇报
2. 自动重载接口函数，让其能够自动化的进行不同接口之间的合并、拆分以及其它的内部流程控制
3. 提供额外的接口，让外部能够获取内部的接口拓扑结构

## 实现原理

1. 利用 Java 的注解方式，在编译时，对语法树进行动态的修改，进而在指定位置插入需要的功能。
    * `MApiType` 注解：对于有该注解的函数，会：
        1. 在入口以及出口添加 log 输出，参考 `MApiTypeProcessor#transformFunction`
        2. ~~重载该函数，将所有参数全部归入到 `MResponse` 类型，并自动从中抽取参数，然后调用原函数~~ 现强制要求参数类型为 `MResponse`
        3. 添加 `HttpServletRequest` 参数，获取请求来源等信息
    * `MObject` 基类：所有基础该类型的 controller，可以通过 `MFunctionType` 来实现自动初始化（类似 Autowired），并赋予一个唯一ID，进而构建拓扑结构
    * 详细请参见 `MApiTypeProcessor` ，[CSDN](https://www.cnblogs.com/jojo-feed/p/10631057.html)，[stackoverflow](https://stackoverflow.com/questions/31345893/debug-java-annotation-processors-using-intellij-and-maven/31358366#31358366)
2. 提供 controller 来携带一些接口

## 使用

[示例](https://github.com/SampleService/ali-service)

* main 中加上 `@MClient` 注解
* 接口函数加上 `@MApiType` 注解
* 接口类基础 `MObject`
