package com.example.plugins.aws.s3upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3Proxy {

  private S3Client s3Client;

  public S3Proxy(String accessKey, String secretKey, String region) {
    AwsCredentials credentials = new AwsCredentials() {
      @Override
      public String accessKeyId() {
        return accessKey;
      }

      @Override
      public String secretAccessKey() {
        return secretKey;
      }
    };
    AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
    s3Client = S3Client.builder().region(Region.of(region)).credentialsProvider(credentialsProvider).build();
  }

  public Optional<File> fetchFromS3BucketToLocation(String fileObjectKeyName, String bucket, String destinationPath) throws IOException {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(fileObjectKeyName).build();
    Path destination = Paths.get(destinationPath);
    Files.deleteIfExists(destination);
    s3Client.getObject(request, destination);
    return Optional.of(destination.toFile());
  }

  public PutObjectResponse uploadToS3Bucket(String fileName, String fileObjectKeyName, String bucket) {
    PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(fileObjectKeyName).build();
    return s3Client.putObject(request, Paths.get(fileName));
  }

}
