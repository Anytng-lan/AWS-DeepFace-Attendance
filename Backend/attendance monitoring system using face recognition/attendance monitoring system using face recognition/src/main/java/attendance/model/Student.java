package attendance.model;

import lombok.Data;

@Data
public class Student {
    private String studentId;
    private String name;
    private String email;
    private String faceId;
    private String imageS3Path;
}