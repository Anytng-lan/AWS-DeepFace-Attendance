# AI-Driven Attendance Monitoring System

## Overview
The **AI-Driven Attendance Monitoring System** is a real-time, facial recognition-based attendance system built using AWS cloud services. This system eliminates manual attendance tracking by using AI-powered face recognition for student authentication. It consists of:

- **Frontend**: Vite + React (Hosted via AWS Amplify, S3, CloudFront)
- **Backend**: Java Spring Boot (Integrated with AWS API Gateway, Lambda, DynamoDB, and Rekognition)
- **Authentication**: Amazon Cognito for secure user authentication and role-based access control

## Key Features
✅ **Secure Authentication** – Uses Amazon Cognito for user verification.
✅ **AI-Powered Face Recognition** – Amazon Rekognition matches faces with stored images.
✅ **Automated Attendance Tracking** – Eliminates manual roll calls.
✅ **Real-Time Reporting** – View attendance logs instantly.
✅ **Cloud Storage & Scalability** – Uses AWS DynamoDB & S3 for secure and scalable storage.

---

# 📌 System Architecture

![Architecture](https://i.imgur.com/YourArchitectureDiagram.png)

The system is designed for **high availability, scalability, and security**. It follows a microservices-based approach where each AWS service handles a specific responsibility efficiently.

## 🛠️ Technology Stack
### Frontend (User Interface)
- **Vite + React** – For fast and optimized UI rendering.
- **AWS Amplify** – Simplifies deployment and backend service integration.
- **AWS S3 + CloudFront** – Delivers static assets globally with low latency.

### Backend (Business Logic)
- **Java Spring Boot** – Handles API requests and integrates AWS services.
- **AWS Lambda** – Executes backend logic without provisioning servers.
- **AWS API Gateway** – Manages RESTful API endpoints and authentication.
- **AWS Rekognition** – Performs face detection and verification.
- **AWS DynamoDB** – Stores attendance records and user profiles.

---

# 🔧 Setup Instructions

## 1️⃣ Frontend Setup
### Clone Repository
```sh
git clone https://github.com/your-repo/ai-attendance.git
cd ai-attendance/frontend
```

### Install Dependencies
```sh
npm install
```

### Configure AWS Amplify
```sh
amplify init
```

### Deploy Frontend
```sh
npm run build
amplify add hosting
amplify publish
```

## 2️⃣ Backend Setup
### Clone Backend Repository
```sh
cd ../backend
```

### Configure AWS Credentials
```sh
aws configure
```

### Build and Run Java Spring Boot Application
```sh
./mvnw spring-boot:run
```

## 3️⃣ AWS Services Integration
### Cognito Authentication Setup
1. **Create a Cognito User Pool** in AWS Console.
2. Configure user roles (Admin, Student) in Cognito.
3. Store **User Pool ID** in `application.properties` for authentication.

### API Gateway & Lambda Setup
1. Define RESTful API endpoints in API Gateway.
2. Configure Lambda functions to handle API requests.
3. Implement API endpoints in `AttendanceController.java`.

### DynamoDB (Attendance Storage)
1. Create a **DynamoDB table** named `AttendanceRecords`.
2. Define attributes for **face ID, timestamps, and attendance status**.
3. Implement data storage/retrieval logic in `AttendanceRepository.java`.

### Rekognition (Face Matching)
1. Store student images securely in **AWS S3**.
2. Configure **AWS Rekognition Collection** for face indexing.
3. Implement face recognition logic in `FaceRecognitionService.java`.

---

# 🚀 Deployment
### Deploy Backend to AWS Lambda
```sh
mvn package
aws lambda update-function-code --function-name AttendanceFunction --zip-file fileb://target/app.jar
```

### Deploy API Gateway
```sh
aws apigateway import-rest-api --body 'file://api-definition.json'
```

---

# 📖 Usage Guide

### **Admin Users**
👤 **Login** securely via Cognito authentication.
📌 **Register new students** and upload their facial data.
📊 **Monitor attendance logs** and generate reports.

### **Student Users**
👤 **Login** and access the attendance system.
📷 **Capture a face image** to mark attendance.
📅 **View attendance history** for each course.

---

# 📊 Monitoring & Logging
### AWS CloudWatch Logging
```sh
aws logs tail /aws/lambda/AttendanceFunction --follow
```

### API Gateway Logs
```sh
aws apigateway get-stage --rest-api-id API_ID --stage-name prod
```

---

# 🔄 Future Improvements
🎯 **Multi-Region Deployment** for better reliability.
📱 **Mobile App Support** for Android & iOS.
🔒 **Enhanced AI-powered Fraud Detection** to prevent spoofing.

---

## 👨‍💻 Contributors
- **Your Name** – Lead Developer
- **Team Member** – Backend Engineer

For any issues or feature requests, please open a ticket in the **Issues** tab.

---

## 📜 License
[MIT License](LICENSE)

