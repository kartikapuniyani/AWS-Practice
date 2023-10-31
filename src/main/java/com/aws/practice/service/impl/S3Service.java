package com.aws.practice.service.impl;

import com.aws.practice.domain.ServiceRecord;
import com.aws.practice.repository.AwsRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

@Service
public class S3Service implements Runnable{

    private AwsRepository awsRepository;

    public S3Service(AwsRepository awsRepository) {
        this.awsRepository = awsRepository;
    }

    @Override
    public void run() {
        software.amazon.awssdk.regions.Region region = software.amazon.awssdk.regions.Region.AP_SOUTH_1;

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        String name = "demobucket1027";

        String id = createBucket(s3,name);
        ServiceRecord record = new ServiceRecord( name, "S3" ,id, null);
        awsRepository.save(record);
    }

    private String createBucket( S3Client s3Client, String bucketName) {
        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            return waiterResponse.matched().response().get().responseMetadata().requestId();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

}
