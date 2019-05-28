package com.pwc.ttn.consumeFileAndProduceTestData.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"com.pwc.ttn.consumeFileAndProduceTestData"})
public class AppConfig {

  @Value("${file.max-size}")
  public int maxFileSize;

  @Value("${file.upload-dir}")
  public String fileUploadDir;

  @Value("${file.supported-formats}")
  public String supportedFileFormats;

  @Value("${file.test-data-dir}")
  public  String testDataDir;

//  @Bean
//  public static PropertySourcesPlaceholderConfigurer properties() {
//    PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
//    ppc.setLocations(new ClassPathResource("datamanagementapi.properties"));
//    ppc.setIgnoreResourceNotFound(false);
//    ppc.setIgnoreUnresolvablePlaceholders(false);
//    return ppc;
//  }

  public List<String> getSupportedFileFormats() {
    return Arrays.asList(supportedFileFormats.split(","));
  }

  public int getMaxFileSize() {
    return maxFileSize;
  }

  public String getFileUploadDir() {
    return fileUploadDir;
  }

  public String getTestDataDir() {
    return testDataDir;
  }
}
