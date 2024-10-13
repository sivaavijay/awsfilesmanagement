package awsproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 s3Client() {
        // Replace these with your actual AWS access and secret keys
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA3RYC6DYDLVNCZBGW", "f547gWuSEbV7D4ALoGwtBpVqKC2apCLHa1kT/xna");

        return AmazonS3ClientBuilder.standard()
                .withRegion("ap-south-1") 
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}

