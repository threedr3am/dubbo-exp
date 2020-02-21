package com.threedr3am.exp.dubbo.serialization;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.protocol.Protocol;
import com.threedr3am.exp.dubbo.protocol.Protocols;

/**
 * @author threedr3am
 */
public interface Serialization {

  byte[] makeData(Payload payload, String[] args, String protocol);

  byte getType();

  default Protocol choiceProtocol(String protocol) {
    return Protocols.getProtocol(protocol);
  }
}
