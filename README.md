# Open-Light-Rpc

轻量级rpc框架，客户端与服务端通信采用 Grpc-Netty 通信方式

# 服务端（open-rpc-server）

## 功能

- [x] 监听注册中心客户端的注册事件，并将注册成功的客户端保存起来。

- [x] 给客户端发送普通消息、客户端上线下线消息。

- [x] 在服务端给客户端发送消息时提供多种负载均衡机制和容错机制供开发者使用


## 快速开始

### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.saucesubfresh</groupId>
    <artifactId>open-rpc-server</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 在启动类上添加 @EnableLightRpcServer 注解

```java
@EnableLightRpcServer
@SpringBootApplication
public class JobAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAdminApplication.class, args);
    }
}
```

### 3. 配置 Grpc 服务端启动端口

```yaml
com:
  saucesubfresh:
    rpc:
      server:
        server-port: 5200
```

### 4. 给客户端发送消息

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

# 客户端（open-rpc-client）

## 功能

- [x] 客户端向注册中心注册功能

- [x] 接收服务端发来的消息并做相应处理

## 快速开始

### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.saucesubfresh</groupId>
    <artifactId>open-rpc-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 在启动类上添加 @EnableLightRpcClient 注解

```java
@EnableLightRpcClient
@SpringBootApplication
public class JobClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobClientApplication.class, args);
    }
}
```

### 3. 配置 Grpc 服务端地址和端口、客户端启动端口

```yaml
com:
  saucesubfresh:
    rpc:
      client:
        server-address: 127.0.0.1
        server-port: 5200
        client-address: 5201
```

### 4. 接收服务端发来的消息进行处理

注入 MessageProcess 接口的实现覆盖系统默认的 DefaultMessageProcess

节选自 Open-Job

```java
@Slf4j
@Component
public class JobHandlerManager implements MessageProcess, InitializingBean, ApplicationContextAware {

    //...
    
    @Override
    public boolean process(Message message) {
        final byte[] body = message.getBody();
        final MessageBody messageBody = SerializationUtils.deserialize(body, MessageBody.class);
        JobHandler jobHandler = this.getJobHandler(messageBody.getHandlerName());
        jobHandler.handler(messageBody.getParams());
        return true;
    }
}
```

## 扩展示例

### 1. 扩展客户端注册方式（RegistryService）

实现 RegistryService 接口

### 1. 自定义消息处理逻辑（MessageProcess）

实现 MessageProcess 接口

## 注意点

### 1. 客户端与服务端使用的注册中心类型须一致


### 2. 如果使用 Nacos 作为注册中心，也就是系统默认使用的注册中心.

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

### 3. 如果使用 Zookeeper 作为注册中心

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
# 最后

欢迎使用，欢迎交流，欢迎 star