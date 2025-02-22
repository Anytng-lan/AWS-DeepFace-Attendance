import React, { useState } from "react";
import { registerStudent, markAttendance } from "../services/api";


const MarkAttendance = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setSelectedFile(event.target.files[0]);
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedFile) {
      setMessage("Please select an image file.");
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      const response = await markAttendance(selectedFile);
      setMessage(response);
    } catch (error) {
      setMessage("Failed to mark attendance.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-lg mx-auto bg-white rounded-xl shadow-md">
      <h2 className="text-xl font-bold mb-4">Mark Attendance</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input type="file" onChange={handleFileChange} className="border p-2 w-full"/>
        <button type="submit" className="bg-green-500 text-white p-2 rounded w-full">
          {loading ? "Marking..." : "Mark Attendance"}
        </button>
      </form>
      {message && <p className="mt-2 text-green-600">{message}</p>}
    </div>
  );
};

export default MarkAttendance;
