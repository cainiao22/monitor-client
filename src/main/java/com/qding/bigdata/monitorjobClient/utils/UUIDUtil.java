package com.qding.bigdata.monitorjobClient.utils;

import java.util.UUID;

public class UUIDUtil {

  public static String createId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

}
