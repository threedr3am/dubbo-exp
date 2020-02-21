package com.threedr3am.exp.dubbo.protocol.http;

import com.threedr3am.exp.dubbo.protocol.Protocol;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.cli.CommandLine;
import org.apache.dubbo.common.io.Bytes;

/**
 * @author threedr3am
 */
public class HttpProtocol implements Protocol {

  public byte[] makeData(byte[] bytes, Serialization serialization, Map<String, String> extraData) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    StringBuilder headBuilder = new StringBuilder()
        .append("POST").append(" ").append(extraData.get(Protocol.HTTP_PROTOCOL_PATH)).append(" ").append("HTTP/1.1").append("\r\n")
        .append("Content-Type: application/x-java-serialized-object").append("\r\n")
        .append("User-Agent: Java/1.8.0_121").append("\r\n")
        .append("Host: ").append(extraData.get(Protocol.HTTP_PROTOCOL_HOST)).append("\r\n")
        .append("Content-Length: %d").append("\r\n")
        .append("Connection: keep-alive").append("\r\n")
        .append("\r\n");
    try {
      byteArrayOutputStream.write(String.format(headBuilder.toString(), bytes.length).getBytes());
      byteArrayOutputStream.write(bytes);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return byteArrayOutputStream.toByteArray();
  }

  @Override
  public Map<String, String> initExtraData(CommandLine cmd) {
    String target = cmd.hasOption("t") ? cmd.getOptionValue("t")
        : cmd.getOptionValue("target");
    String host = "";
    String path = "/";
    int index = target.indexOf("/");
    if (index != -1) {
      host = target.substring(0, index);
      path = target.substring(index);
    } else {
      System.err.println("http协议需要target参数带有path路径，例：127.0.0.1:8080/com.threedr3am.dubbo.DemoService");
      System.exit(1);
    }
    Map<String, String> extraData = new HashMap();
    extraData.put(Protocol.HTTP_PROTOCOL_PATH, path);
    extraData.put(Protocol.HTTP_PROTOCOL_HOST, host);
    return extraData;
  }

}
