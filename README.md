# Open-Light-Rpc

<p align="center">
轻量级，高性能rpc框架，客户端与服务端通信采用 Grpc-Netty 通信方式
</p>

<p align="center">
  <a href="https://search.maven.org/search?q=g:com.openbytecode%20a:open-rpc-*">
    <img alt="maven" src="https://img.shields.io/github/v/release/lijunping365/Open-Light-Rpc?include_prereleases&logo=Open-Light-Rpc&style=plastic">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="license" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>

  <a href="https://github.com/lijunping365/Open-Light-Rpc">
     <img alt="github" src="https://img.shields.io/github/stars/lijunping365/Open-Light-Rpc" >
  </a>
</p>

## 链接

* [官网地址](https://openbytecode.com/)
* [文档地址](https://openbytecode.com/starter/open-light-rpc/docs/)
* [官方讨论地址](https://openbytecode.com/community/)

## 功能

- [x] 支持同步和异步请求

- [x] 支持请求/响应拦截器

- [x] 支持服务注册与发现

- [x] 支持给服务端发送普通消息以及服务端上线下线消息

- [x] 客户端提供多种负载均衡机制供开发者使用，默认提供的负载均衡策略有随机权重、一致性哈希、最少活跃且支持用户拓展，

- [x] 客户端提供多种容错机制供开发者使用，默认提供的容错机制有故障转移模式、失败重试调用模式、广播调用模式且支持用户自定义拓展

- [x] 默认提供 Grpc-Netty 通信方式，且支持用户拓展

- [x] 默认提供 Zookeeper 和 Nacos 做服务注册与发现，且支持用户拓展

## 快速开始

### 客户端（open-rpc-client）

#### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.openbytecode</groupId>
    <artifactId>open-rpc-client</artifactId>
    <version>1.5.0</version>
</dependency>
```

#### 2. 在启动类上添加 @EnableOpenRpcClient 注解

```java
@EnableOpenRpcClient
@SpringBootApplication
public class JobDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobDashboardApplication.class, args);
    }
}
```

#### 3. 配置服务名称

```yaml
com:
  openbytecode:
    rpc:
      client:
        server-name: open-job-services
```

#### 4. 给服务端发送消息

节选自 Open-Job 代码片段，该代码仅为示例。

```java
@Slf4j
@Component
public class OpenJobClientServiceImpl implements OpenJobClientService{

    private final ClusterInvoker clusterInvoker;

    public OpenJobClientServiceImpl(ClusterInvoker clusterInvoker) {
        this.clusterInvoker = clusterInvoker;
    }

    @Override
    public void invoke(Long jobId) {
        Message message = new Message();
        message.setMsgId(String.valueOf(jobId));
        message.setNamespace(serverName);

        try {
            clusterInvoker.invokeAsync(message, (response) -> {
                // TODO
            });
        } catch (RpcException ex){
            // TODO
        }
    }
}
```

### 服务端（open-rpc-server）

#### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.openbytecode</groupId>
    <artifactId>open-rpc-server</artifactId>
    <version>1.5.0</version>
</dependency>
```

#### 2. 在启动类上添加 @EnableOpenRpcServer 注解

```java
@EnableOpenRpcServer
@SpringBootApplication
public class JobServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobServerApplication.class, args);
    }
}
```

#### 3. 配置 Grpc 服务端地址和端口、服务名称

```yaml
com:
  openbytecode:
    rpc:
      server:
        server-address: 127.0.0.1
        server-port: 5200
        server-name: open-job-services
```

#### 4. 接收客户端发来的消息进行处理

节选自 Open-Job

```java
@Slf4j
@Component
public class JobMessageProcessor extends AbstractMessageProcess {

    private final JobHandlerRegister jobHandlerRegister;
    private final JobThreadRepository jobThreadRepository;

    public JobMessageProcessor(RegistryService registryService,
                               ServerConfiguration configuration,
                               JobHandlerRegister jobHandlerRegister,
                               JobThreadRepository jobThreadRepository) {
        super(configuration, registryService);
        this.jobHandlerRegister = jobHandlerRegister;
        this.jobThreadRepository = jobThreadRepository;
    }

    @Override
    public void doProcess(Message message, MessageResponseBody responseBody, ResponseWriter responseWriter) {
        final byte[] body = message.getBody();
        final MessageBody messageBody = SerializationUtils.deserialize(body, MessageBody.class);
        CommandEnum command = CommandEnum.of(messageBody.getCommand());
        if (command == CommandEnum.SCHEDULE){
            handlerSchedule(messageBody, responseBody, responseWriter);
            return;
        }

        try {
            handlerMessage(messageBody, command, responseBody);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            responseBody.setMsg(e.getMessage());
            responseBody.setStatus(ResponseStatus.ERROR);
        } finally {
            responseWriter.write(responseBody);
        }
    }
}
```


## 自定义拓展示例

### 客户端（open-rpc-client）

#### 1. 扩展服务发现（ServiceDiscovery）

实现 ServiceDiscovery  接口

#### 2. 扩展负载均衡机制（LoadBalance）

实现 LoadBalance 接口

#### 3. 扩展容错机制（ClusterInvoker）

实现 ClusterInvoker 接口

#### 4. 扩展生成唯一请求 ID（RequestIdGenerator）

实现 RequestIdGenerator 接口

#### 5. 扩展客户端存储方式（InstanceStore）

实现 InstanceStore 接口

### 服务端（open-rpc-server）

#### 1. 扩展服务端注册方式（RegistryService）

实现 RegistryService 接口

#### 1. 自定义消息处理逻辑（MessageProcess）

实现 MessageProcess 接口


## 注意点

1. 系统默认客户端和服务端使用的注册中心是 Nacos，注意客户端与服务端使用的注册中心类型须一致，也就是说如果客户端使用 Nacos 作为注册中心，那服务端也需要使用 Nacos 作为注册中心。

### 如果使用 Nacos 作为注册中心

1. 需要添加如下 maven 依赖

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>2.0.4</version>
</dependency>
```

2. 需要注入 NamingService

```java
@Configuration(proxyBeanMethods = false)
public class SpringWebMvcConfig {
    
    @Bean
    public NamingService namingService() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.USERNAME, "nacos");
        properties.put(PropertyKeyConst.PASSWORD, "nacos");
        properties.put(PropertyKeyConst.SERVER_ADDR, "127.0.0.1:8848");
        return NacosFactory.createNamingService(properties);
    }
}
```

### 如果使用 Zookeeper 作为注册中心

1. 需要添加如下 maven 依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.zookeeper</groupId>
        <artifactId>zookeeper</artifactId>
        <version>3.7.0</version>
    </dependency>

    <dependency>
        <groupId>com.101tec</groupId>
        <artifactId>zkclient</artifactId>
        <version>0.11</version>
    </dependency>
</dependencies>
```

2. 需要注入 ZkClient

```java
@Configuration(proxyBeanMethods = false)
public class SpringWebMvcConfig {

    @Bean
    public ZkClient zkClient(){
        System.setProperty("zookeeper.sasl.client", "false");
        return new ZkClient(String.format(CommonConstant.ADDRESS_PATTERN, configuration.getAddress(), configuration.getPort()), configuration.getConnectionTimeout());
    }
}
```

## 版本更新说明

### 1.0.1 版本更新说明

1. 服务端响应添加错误信息字段

2. 修复了故障转移模式 bug

### 1.0.2 版本更新说明

1. 在服务端处理时增加异常打印输出

### 1.0.3 版本更新说明

1. 项目重构，

2. 在 grpc 通信的基础上增加 netty 通信方式

### 1.0.4 版本更新说明

1. 增加版权

### 1.0.6 版本更新说明

1. 支持应用隔离

### 1.0.7 版本更新说明

1. 优化异常

2. 响应添加 msg 字段

3. 响应及异常新增  serverId 字段

### 1.0.8 版本更新说明

1. 加载应用默认采用从配置文件中读取

### 1.0.9 版本更新说明

1. 服务端采用异步处理请求

### 1.1.0 版本更新说明

1. 去掉了上个版本的自定义业务线程池

2. 增加了带回调的 rpc 调用方法

### 1.2.0 版本更新说明

1. 重构了服务端的请求处理，在处理请求时新增了钩子函数，便于请求的异步处理

### 1.3.0 版本更新说明

1. 增加异步调用方法，增大并发性能

2. 新增了请求拦截器

### 1.3.1 版本更新说明

1. 修复异步调用回调函数为空 bug

### 1.4.0 版本更新说明

1. 新增了响应拦截器

### 1.5.0 版本更新说明

1. 客户端和服务端新增启动和停止函数

2. 服务端新增服务停止钩子函数，用于在服务停止前执行某些操作

### 2.0.0 版本更新说明

1. 组件去掉了服务注册与发现功能

2. 组件去掉了负载均衡与服务容错相关功能

3. netty 心跳处理简化

4. 修复了已知 bug

该组件只提供核心的通信功能，支持 Grpc 和 Netty 两种方式。

### 2.1.0 版本更新说明

1. 新增服务注册组件，提供 Nacos 和 Zookeeper 两种方式，默认使用 Nacos

2. 新增服务发现组件，提供 Nacos 和 Zookeeper 两种方式，默认使用 Nacos

3. 新增负载均衡组件

## 最后

欢迎使用，欢迎交流，欢迎 star