import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api"; // Change if necessary

export interface StudentData {
  studentId: string;
  name: string;
  email: string;
  image: File;
}

// Register a student
export const registerStudent = async (formData: FormData): Promise<string> => {
  try {
    const response = await axios.post<string>(`${API_BASE_URL}/students/register`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  } catch (error) {
    console.error("Error registering student:", error);
    throw new Error("Failed to register student");
  }
};

// Mark attendance
export const markAttendance = async (imageFile: File): Promise<string> => {
  const formData = new FormData();
  formData.append("image", imageFile);

  try {
    const response = await axios.post<string>(`${API_BASE_URL}/attendance/mark`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  } catch (error) {
    console.error("Error marking attendance:", error);
    throw new Error("Failed to mark attendance");
  }
};
