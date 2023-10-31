package com.aws.practice.service.impl;

import com.aws.practice.domain.ServiceRecord;
import com.aws.practice.repository.AwsRepository;
import com.aws.practice.service.AwsService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AwsServiceImpl implements AwsService {

    private AwsRepository awsRepository;

    public AwsServiceImpl(AwsRepository awsRepository) {
        this.awsRepository = awsRepository;
    }

    @Override
    public void createService(List<String> services) {

        services.forEach(s -> {
            if(s.equals("EC2")) {
                Ec2Service ec2Service = new Ec2Service(awsRepository);
                Thread t1 = new Thread(ec2Service);
                t1.start();

            } else if(s.equals("S3")) {
                S3Service s3Service = new S3Service(awsRepository);
                Thread t2 = new Thread(s3Service);
                t2.start();
            }
        });
    }

    @Override
    public List<String> getData(String type) {
        List<ServiceRecord> records = awsRepository.findAll().stream()
                .filter(serviceRecord -> type.equals(serviceRecord.getType())).collect(Collectors.toList());
        if("EC2".equals(type)) {
            return records.stream().map(ServiceRecord::getInstanceId).collect(Collectors.toList());
        } else if("S3".equals(type)) {
            return records.stream().map(ServiceRecord::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public String getS3Data(String bucketName) {
        software.amazon.awssdk.regions.Region region = software.amazon.awssdk.regions.Region.AP_SOUTH_1;

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        return listBucketObjects(s3Client, bucketName);
    }

    @Override
    public Integer getS3FilesCount(String name) {
        ServiceRecord serviceRecord = awsRepository.findByNameAndType(name, "S3");
        return serviceRecord.getFiles().size();
    }

    @Override
    public List<String> getS3Files(String name, String pattern) {
        ServiceRecord serviceRecord = awsRepository.findByNameAndType(name, "S3");
        return serviceRecord.getFiles().stream()
                .filter(value -> value.toLowerCase().contains(pattern.toLowerCase()))
                .collect(Collectors.toList());
    }

    private String listBucketObjects(S3Client s3, String bucketName) {
        String requestId = "";
        List<String> files = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                files.add(myValue.key());
            }
            requestId = res.responseMetadata().requestId();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        ServiceRecord serviceRecord = awsRepository.findByNameAndType(bucketName, "S3");
        serviceRecord.setFiles(files);
        awsRepository.save(serviceRecord);
        return requestId;
    }

}
