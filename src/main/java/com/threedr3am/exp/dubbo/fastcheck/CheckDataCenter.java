package com.threedr3am.exp.dubbo.fastcheck;

import com.threedr3am.exp.dubbo.payload.Payloads;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author threedr3am
 */
public class CheckDataCenter {


  public static void initFastCheckData(String fastcheck) {
    Map<String, String[]> data = new HashMap();

    try {
      Properties properties = new Properties();
      properties.load(Files.newBufferedReader(Paths.get(fastcheck)));
      for (Map.Entry<Object, Object> entry : properties.entrySet()) {
        String key = String.valueOf(entry.getKey());
        String value = String.valueOf(entry.getValue());
        data.put(key, value.split(","));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    Payloads[] payloads = Payloads.values();
    for (int i = 0; i < payloads.length; i++) {
      payloads[i].getPayload().injectDefaultArgs(data.get(payloads[i].getCheckParam().name()));
    }
  }
}
