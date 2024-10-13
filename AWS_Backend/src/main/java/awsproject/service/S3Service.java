package awsproject.service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class S3Service {

    private final AmazonS3 s3Client;

//    @Value("${aws.s3.bucket-name}")
    private String bucketName = "filesrepobucket";

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    // Search for files in the user's folder based on the search term
    public List<String> searchFiles(String userName, String searchTerm) {
        String prefix = userName + "/";
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix);

        ListObjectsV2Result result = s3Client.listObjectsV2(request);
        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .filter(key -> key.contains(searchTerm))
                .collect(Collectors.toList());
    }

    // Download a file from the user's folder
    public InputStream downloadFile(String userName, String fileName) {
        String key = userName + "/" + fileName;
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));
        return s3Object.getObjectContent();
    }

    // Upload a file to the user's folder
    public void uploadFile(String userName, String fileName, InputStream inputStream, long contentLength) {
        String key = userName + "/" + fileName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);

        s3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata));
    }
}
