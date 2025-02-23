package attendance.service;

import attendance.model.Student;
import org.slf4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Service
public class AttendanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceService.class);

    // AWS Credentials (HARDCODED) ⚠️
    private static final String AWS_ACCESS_KEY = "AKIAW5WU5GBYZ5YXLZOL";
    private static final String AWS_SECRET_KEY = "5wMirnRIqD+CR3pt7SgoYoYvtneBtemGhEP5oSZv";
    private static final String AWS_REGION = String.valueOf(Region.US_EAST_1); // Change to your region

    private static final String STUDENTS_BUCKET = "studentsimagesbucket";
    private static final String ATTENDANCE_BUCKET = "attendancecapturesbucket";
    private static final String COLLECTION_ID = "students-collection";
    private static final String STUDENTS_TABLE = "Students";
    private static final String ATTENDANCE_TABLE = "Attendance";

    private final S3Client s3Client;
    private final RekognitionClient rekognitionClient;
    private final DynamoDbClient dynamoDbClient;

    public AttendanceService() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCreds);

        this.s3Client = S3Client.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(credentialsProvider)
                .build();

        this.rekognitionClient = RekognitionClient.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(credentialsProvider)
                .build();

        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(credentialsProvider)
                .build();
    }


    public void registerStudent(Student student, InputStream imageStream) {
        try {
            String imageKey = student.getStudentId() + ".jpg";
            LOGGER.info("🚀 Uploading student image to S3: {}", imageKey);

            // Upload image to S3
            PutObjectResponse putResponse = s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(STUDENTS_BUCKET)
                            .key(imageKey)
                            .build(),
                    RequestBody.fromInputStream(imageStream, imageStream.available())
            );

            LOGGER.info("✅ S3 Upload Response: {}", putResponse);

            // Verify Image Upload
            HeadObjectResponse headResponse = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(STUDENTS_BUCKET)
                            .key(imageKey)
                            .build()
            );
            LOGGER.info("✅ S3 Image Verified: {}", headResponse);

            // Index face in Rekognition
            LOGGER.info("🚀 Calling Rekognition IndexFaces for image: {}", imageKey);
            IndexFacesResponse response = rekognitionClient.indexFaces(
                    IndexFacesRequest.builder()
                            .collectionId(COLLECTION_ID)
                            .image(Image.builder()
                                    .s3Object(S3Object.builder()
                                            .bucket(STUDENTS_BUCKET)
                                            .name(imageKey)
                                            .build())
                                    .build())
                            .maxFaces(1)
                            .qualityFilter(QualityFilter.AUTO)
                            .build()
            );

            LOGGER.info("✅ Rekognition Response: {}", response);

            // Store student in DynamoDB
            String faceId = response.faceRecords().get(0).face().faceId();
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(STUDENTS_TABLE)
                    .item(Map.of(
                            "studentId", AttributeValue.builder().s(student.getStudentId()).build(),
                            "name", AttributeValue.builder().s(student.getName()).build(),
                            "faceId", AttributeValue.builder().s(faceId).build(),
                            "imageS3Path", AttributeValue.builder().s(imageKey).build()
                    ))
                    .build()
            );

            LOGGER.info("✅ Student registered successfully: {}", student.getStudentId());

        } catch (IOException e) {
            LOGGER.error("❌ Failed to process image stream: {}", e.getMessage());
            throw new RuntimeException("Failed to register student due to an I/O error", e);
        }
    }

    public void markAttendance(InputStream imageStream) {
        try {
            String imageKey = "attendance/" + Instant.now().toEpochMilli() + ".jpg";
            LOGGER.info("🚀 Uploading attendance image to S3: {}", imageKey);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(ATTENDANCE_BUCKET)
                            .key(imageKey)
                            .build(),
                    RequestBody.fromInputStream(imageStream, imageStream.available())
            );

            // Search for faces
            SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(
                    SearchFacesByImageRequest.builder()
                            .collectionId(COLLECTION_ID)
                            .image(Image.builder()
                                    .s3Object(S3Object.builder()
                                            .bucket(ATTENDANCE_BUCKET)
                                            .name(imageKey)
                                            .build())
                                    .build())
                            .faceMatchThreshold(90f)
                            .build()
            );

            for (FaceMatch match : response.faceMatches()) {
                String faceId = match.face().faceId();
                LOGGER.info("🔍 Searching DynamoDB for faceId: {}", faceId);

                GetItemResponse studentResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                        .tableName(STUDENTS_TABLE)
                        .key(Map.of("faceId", AttributeValue.builder().s(faceId).build()))
                        .build());

                String studentId = studentResponse.item().get("studentId").s();

                dynamoDbClient.putItem(PutItemRequest.builder()
                        .tableName(ATTENDANCE_TABLE)
                        .item(Map.of(
                                "studentId", AttributeValue.builder().s(studentId).build(),
                                "date", AttributeValue.builder().s(LocalDate.now().toString()).build(),
                                "timestamp", AttributeValue.builder().s(Instant.now().toString()).build()
                        )).build());

                LOGGER.info("✅ Attendance marked for student: {}", studentId);
            }
        } catch (IOException e) {
            LOGGER.error("❌ Failed to process attendance: {}", e.getMessage());
        }
    }
}
