package attendance.handler;

import attendance.model.Student;
import attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/students")
public class StudentRegistrationHandler {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(
            @RequestParam("studentId") String studentId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(name);
        student.setEmail(email);
        attendanceService.registerStudent(student, image.getInputStream());
        return ResponseEntity.ok("Student registered successfully");
    }
}