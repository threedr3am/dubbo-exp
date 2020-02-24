package com.threedr3am.exp.dubbo.serialization;

import com.threedr3am.exp.dubbo.payload.Payload;

/**
 * @author threedr3am
 */
public interface Serialization {

  byte[] makeData(Payload payload, String[] args, String protocol);

  byte getType();

  void setPayload(Payload payload);

  Payload getPayload();

}
