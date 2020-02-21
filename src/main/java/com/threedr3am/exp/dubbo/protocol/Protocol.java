package com.threedr3am.exp.dubbo.protocol;

import com.threedr3am.exp.dubbo.serialization.Serialization;
import java.util.Map;
import org.apache.commons.cli.CommandLine;

/**
 * @author threedr3am
 */
public interface Protocol {

  String HTTP_PROTOCOL_PATH = "HTTP_PROTOCOL_PATH";
  String HTTP_PROTOCOL_HOST = "HTTP_PROTOCOL_HOST";

  byte[] makeData(byte[] bytes, Serialization serialization, Map<String, String> extraData);

  Map<String, String> initExtraData(CommandLine cmd);
}
