package com.pwc.ttn.consumeFileAndProduceTestData.utils;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public class ApplicationUtils {

  public static String getFileExtension(String fullName) {
    checkNotNull(fullName);
    String fileName = new File(fullName).getName();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
  }
}
