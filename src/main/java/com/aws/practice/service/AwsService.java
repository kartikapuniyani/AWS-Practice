package com.aws.practice.service;

import java.util.List;

public interface AwsService {

    void createService(List<String> services);

    List<String> getData(String type);

    String getS3Data(String name);

    Integer getS3FilesCount(String name);

    List<String> getS3Files(String name, String pattern);

}
