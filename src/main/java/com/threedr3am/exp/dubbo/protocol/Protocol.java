package com.threedr3am.exp.dubbo.protocol;

import com.threedr3am.exp.dubbo.serialization.Serialization;

/**
 * @author threedr3am
 */
public interface Protocol {

  byte[] makeData(byte[] bytes, Serialization serialization);
}
