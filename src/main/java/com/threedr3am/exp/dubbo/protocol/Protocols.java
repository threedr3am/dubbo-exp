package com.threedr3am.exp.dubbo.protocol;

import com.threedr3am.exp.dubbo.protocol.dubbo.DubboProtocol;
import com.threedr3am.exp.dubbo.protocol.http.HttpProtocol;

/**
 * @author threedr3am
 */
public enum Protocols {
  dubbo("dubbo",new DubboProtocol()),
  http("http",new HttpProtocol()),
  ;

  private String name;

  private Protocol protocol;

  Protocols(String name,
      Protocol protocol) {
    this.name = name;
    this.protocol = protocol;
  }

  public static Protocol getProtocol(String name) {
    for (Protocols protocols : Protocols.values()) {
      if (protocols.name.equalsIgnoreCase(name))
        return protocols.protocol;
    }
    return null;
  }
}
