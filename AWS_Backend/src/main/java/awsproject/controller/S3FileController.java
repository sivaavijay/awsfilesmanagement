package awsproject.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.model.AmazonS3Exception;

import awsproject.service.S3Service;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/files")
public class S3FileController {

    private final S3Service s3Service;

    public S3FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // Search for files by username and search term
    @GetMapping("/search")
    public ResponseEntity<?> searchFiles(
            @RequestParam String userName,
            @RequestParam String searchTerm) {
        List<String> files = s3Service.searchFiles(userName, searchTerm);

        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body("No files found for the given search term.");
        }

        return ResponseEntity.status(HttpStatus.SC_OK).body("Files in the bucket :"+files);

    }
    
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String userName, @RequestParam String fileName)
    {    		
        InputStream inputStream = null;

        try {
            // Get the InputStream for the requested file
            inputStream = s3Service.downloadFile(userName, fileName);
            
            // Create a byte array to hold the file content
            byte[] content = inputStream.readAllBytes(); 

            // Prepare headers for the file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            // Return the response with the file content
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(content);
                    
        } catch (AmazonS3Exception e) {
            // Handle specific S3 exceptions like file not found or access denied
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body("File not found or access denied: " + e.getErrorMessage());

        } catch (IOException e) {
            // Handle IO related exceptions
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("Error reading the file: " + e.getMessage());
        } finally {
            // Close the InputStream
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Log the exception (optional)
                    e.printStackTrace();
                }
            }
        }

}
}
