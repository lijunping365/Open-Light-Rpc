# Open-Light-Rpc

<p align="center">
轻量级rpc框架，客户端与服务端通信采用 Grpc-Netty 通信方式
</p>

<p align="center">
  <a href="https://search.maven.org/search?q=g:com.saucesubfresh%20a:open-rpc-*">
    <img alt="maven" src="https://img.shields.io/github/v/release/lijunping365/Open-Light-Rpc?include_prereleases&logo=Open-Light-Rpc&style=plastic">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="license" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>

  <a href="https://github.com/lijunping365/Open-Light-Rpc">
     <img alt="github" src="https://img.shields.io/github/stars/lijunping365/Open-Light-Rpc" >
  </a>
</p>

# 客户端（open-rpc-client）

## 功能

- [x] 监听注册中心服务端的注册事件，并将注册成功的服务端保存起来。

- [x] 给服务端发送普通消息、服务端上线下线消息。

- [x] 在给服务端发送消息时提供多种负载均衡机制和容错机制供开发者使用


## 快速开始

### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.saucesubfresh</groupId>
    <artifactId>open-rpc-client</artifactId>
    <version>1.0.4</version>
</dependency>
```

### 2. 在启动类上添加 @EnableOpenRpcClient 注解

```java
@EnableOpenRpcClient
@SpringBootApplication
public class JobDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobDashboardApplication.class, args);
    }
}
```

### 3. 配置服务名称

```yaml
com:
  saucesubfresh:
    rpc:
      client:
        server-name: open-job-services
```

### 4. 给服务端发送消息

节选自 Open-Job

```java
@Slf4j
@Component
public class ScheduleJobExecutor implements ScheduleTaskExecutor{

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ClusterInvoker clusterInvoker;
    private final OpenJobMapper openJobMapper;

    public ScheduleJobExecutor(ApplicationEventPublisher applicationEventPublisher, ClusterInvoker clusterInvoker, OpenJobMapper openJobMapper) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.clusterInvoker = clusterInvoker;
        this.openJobMapper = openJobMapper;
    }

    @Override
    public void execute(List<Long> taskList){
        List<OpenJobDO> jobList = openJobMapper.queryList(taskList);
        if (CollectionUtils.isEmpty(jobList)){
            return;
        }
        // 1 组装任务
        List<Message> messages = jobList.stream().map(e->{
            MessageBody messageBody = new MessageBody();
            messageBody.setHandlerName(e.getHandlerName());
            messageBody.setParams(e.getParams());
            byte[] serializeData = SerializationUtils.serialize(messageBody);
            Message message = new Message();
            message.setMsgId(String.valueOf(e.getId()));
            message.setBody(serializeData);
            return message;
        }).collect(Collectors.toList());

        // 2 分发任务
        messages.forEach(message->{
            String cause = null;
            try {
                clusterInvoker.invoke(message);
            }catch (RpcException e){
                cause = e.getMessage();
            }
            createLog(Long.parseLong(message.getMsgId()), cause);
        });
    }
}
```

## 扩展示例

### 1. 扩展服务发现（ServiceDiscovery）

实现 ServiceDiscovery  接口

### 2. 扩展负载均衡机制（LoadBalance）

实现 LoadBalance 接口

### 3. 扩展容错机制（ClusterInvoker）

实现 ClusterInvoker 接口

### 4. 扩展生成唯一请求 ID（RequestIdGenerator）

实现 RequestIdGenerator 接口

### 5. 扩展客户端存储方式（InstanceStore）

实现 InstanceStore 接口

# 客户端（open-rpc-server）

## 功能

- [x] 服务端向注册中心注册功能

- [x] 接收客户端发来的消息并做相应处理

## 快速开始

### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.saucesubfresh</groupId>
    <artifactId>open-rpc-server</artifactId>
    <version>1.0.4</version>
</dependency>
```

### 2. 在启动类上添加 @EnableOpenRpcServer 注解

```java
@EnableOpenRpcServer
@SpringBootApplication
public class JobServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobServerApplication.class, args);
    }
}
```

### 3. 配置 Grpc 服务端地址和端口、服务名称

```yaml
com:
  saucesubfresh:
    rpc:
      server:
        server-address: 127.0.0.1
        server-port: 5200
        server-name: open-job-services
```

### 4. 接收客户端发来的消息进行处理

注入 MessageProcess 接口的实现覆盖系统默认的 DefaultMessageProcess

节选自 Open-Job

```java
@Slf4j
@Component
public class JobMessageProcessor implements MessageProcess {

    private final JobHandlerCollector jobHandlerCollector;

    public JobMessageProcessor(JobHandlerCollector jobHandlerCollector) {
        this.jobHandlerCollector = jobHandlerCollector;
    }

    @Override
    public byte[] process(Message message) {
        final byte[] body = message.getBody();
        final MessageBody messageBody = SerializationUtils.deserialize(body, MessageBody.class);
        String handlerName = messageBody.getHandlerName();
        OpenJobHandler openJobHandler = jobHandlerCollector.getJobHandler(handlerName);
        if (ObjectUtils.isEmpty(openJobHandler)) {
            throw new RpcException("JobHandlerName: " + handlerName + ", there is no bound JobHandler.");
        }
        try {
            openJobHandler.handler(messageBody.getParams());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RpcException("JobHandlerName: " + handlerName + ", execute exception:" + e.getMessage());
        }
        return null;
    }
}
```

## 扩展示例

### 1. 扩展服务端注册方式（RegistryService）

实现 RegistryService 接口

### 1. 自定义消息处理逻辑（MessageProcess）

实现 MessageProcess 接口

## 注意点

1. 系统默认客户端和服务端使用的注册中心是 Nacos，注意客户端与服务端使用的注册中心类型须一致，也就是说如果客户端使用 Nacos 作为注册中心，那服务端也需要使用 Nacos 作为注册中心。

## 如果使用 Nacos 作为注册中心

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

## 如果使用 Zookeeper 作为注册中心

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

## 1.0.1 版本更新说明

1. 对 client 响应的消息

2. 修复了故障转移模式 bug

## 1.0.2 版本更新说明

修复版本 bug

## 1.0.3 版本更新说明

1. 项目重构，

2. 在 grpc 通信的基础上增加 netty 通信方式

## 1.0.4 版本更新说明

1. 增加版权

## 1.0.6 版本更新说明

1. 支持应用隔离

## 1.0.7 版本更新说明

1. 优化异常

2. 响应添加 msg 字段

3. 响应及异常新增  serverId 字段

## 1.0.8 版本更新说明

1. 优化多应用支持代码

# 最后

欢迎使用，欢迎交流，欢迎 star