# MServiceAnalyser
微服务源码分析——以springcloud框架为主
## 结构
- bean：在源码层级获得的接口路径信息
- utils：
    - GetServiceSourceCode：检索微服务开放的版本
    - GetServiceInfo：得到某版本的微服务所有对外开放接口信息
    - GetServiceDiff：获取版本之间的差异
- controller：将utils中的工具类对外rest接口形式开放
## 使用说明
- getAllversion：
    - 输入：MServiceRegisterBean（giturl + servicename）
    - 输出：返回所有的版本信息List<MService>
- getVersionInfo：
    - 输入：MFetchServiceInfoBean(特定版本的微服务源码信息)
    - 输出：该版本的微服务服务信息 MService
- getDiffBetweenTwoVersions：
    - 输入：MServiceCompareBean（内含两个版本的微服务）
    - 输出：版本之间的差异