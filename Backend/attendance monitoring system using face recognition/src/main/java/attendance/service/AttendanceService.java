package attendance.service;

import attendance.model.Student;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Service
public class AttendanceService {

    private final S3Client s3Client;
    private final RekognitionClient rekognitionClient;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public AttendanceService(S3Client s3Client, RekognitionClient rekognitionClient, DynamoDbClient dynamoDbClient) {
        this.s3Client = s3Client;
        this.rekognitionClient = rekognitionClient;
        this.dynamoDbClient = dynamoDbClient;
    }

    public void registerStudent(Student student, InputStream imageStream) {
        try {
            // Upload image to S3
            String imageKey = student.getStudentId() + ".jpg";
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket("students-images-bucket")
                            .key(imageKey)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(imageStream, imageStream.available())
            );

            // Index face in Rekognition
            IndexFacesResponse response = rekognitionClient.indexFaces(IndexFacesRequest.builder()
                    .collectionId("students-collection")
                    .image(Image.builder()
                            .s3Object(S3Object.builder()
                                    .bucket("students-images-bucket")
                                    .name(imageKey)
                                    .build())
                            .build())
                    .build());

            // Store student in DynamoDB
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("Students")
                    .item(Map.of(
                            "studentId", AttributeValue.builder().s(student.getStudentId()).build(),
                            "name", AttributeValue.builder().s(student.getName()).build(),
                            "faceId", AttributeValue.builder().s(response.faceRecords().get(0).face().faceId()).build(),
                            "imageS3Path", AttributeValue.builder().s(imageKey).build()
                    )).build());
        } catch (IOException e) {
            // Handle IOException (e.g., log the error and throw a custom exception)
            System.err.println("Failed to process image stream: " + e.getMessage());
            throw new RuntimeException("Failed to register student due to an I/O error", e);
        } finally {
            // Ensure the InputStream is closed
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    System.err.println("Failed to close image stream: " + e.getMessage());
                }
            }
        }
    }

    public void markAttendance(InputStream imageStream) {
        try {
            // Upload image to S3
            String imageKey = "attendance/" + Instant.now().toEpochMilli() + ".jpg";
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket("attendance-captures-bucket")
                            .key(imageKey)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(imageStream, imageStream.available())
            );

            // Search for faces in the image
            SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(
                    SearchFacesByImageRequest.builder()
                            .collectionId("students-collection")
                            .image(Image.builder()
                                    .s3Object(S3Object.builder()
                                            .bucket("attendance-captures-bucket")
                                            .name(imageKey)
                                            .build())
                                    .build())
                            .faceMatchThreshold(90f)
                            .build());

            // Record attendance for matched faces
            response.faceMatches().forEach(match -> {
                String faceId = match.face().faceId();
                // Query DynamoDB for studentId using faceId
                GetItemResponse studentResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                        .tableName("Students")
                        .key(Map.of("faceId", AttributeValue.builder().s(faceId).build()))
                        .build());

                String studentId = studentResponse.item().get("studentId").s();
                // Insert into Attendance table
                dynamoDbClient.putItem(PutItemRequest.builder()
                        .tableName("Attendance")
                        .item(Map.of(
                                "studentId", AttributeValue.builder().s(studentId).build(),
                                "date", AttributeValue.builder().s(LocalDate.now().toString()).build(),
                                "timestamp", AttributeValue.builder().s(Instant.now().toString()).build()
                        )).build());
            });
        } catch (IOException e) {
            // Handle IOException (e.g., log the error and throw a custom exception)
            System.err.println("Failed to process image stream: " + e.getMessage());
            throw new RuntimeException("Failed to mark attendance due to an I/O error", e);
        } finally {
            // Ensure the InputStream is closed
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    System.err.println("Failed to close image stream: " + e.getMessage());
                }
            }
        }
    }
}