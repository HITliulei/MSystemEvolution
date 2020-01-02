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
    - 输入：微服务仓库地址—giturl
    - 输出：提供rest接口的类的路径信息
- getVersionInfo：
    - 输入：String version 版本
    - 输出：该版本的服务信息 MService
- getVersionInfo1：
    - 输入：由中控给出的MFetchServiceInfoBean
    - 输入：某版本的微服务信息 MService
- getDiifTwoVersions：
    - 输入：两个版本String version1， version2
    - 输出：版本之间的差异
- getDiifTwoVersions1：
    - 输入：两个版本信息MService 
    - 输出：版本之间的差异
- 