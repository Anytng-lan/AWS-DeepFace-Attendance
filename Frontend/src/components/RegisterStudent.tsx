import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { registerStudent } from "../services/api";

const RegisterStudent = () => {
  const { register, handleSubmit, reset } = useForm();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const onSubmit = async (data: any) => {
    setLoading(true);
    setMessage("");

    const formData = new FormData();
    formData.append("studentId", data.studentId);
    formData.append("name", data.name);
    formData.append("email", data.email);
    formData.append("image", data.image[0]);

    try {
      const response = await registerStudent(formData);
      setMessage(response);
      reset();
    } catch (error) {
      setMessage("Failed to register student.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-lg mx-auto bg-white rounded-xl shadow-md">
      <h2 className="text-xl font-bold mb-4">Register Student</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <input type="text" {...register("studentId")} placeholder="Student ID" required className="border p-2 w-full"/>
        <input type="text" {...register("name")} placeholder="Name" required className="border p-2 w-full"/>
        <input type="email" {...register("email")} placeholder="Email" required className="border p-2 w-full"/>
        <input type="file" {...register("image")} required className="border p-2 w-full"/>

        <button type="submit" className="bg-blue-500 text-white p-2 rounded w-full">
          {loading ? "Registering..." : "Register"}
        </button>
      </form>
      {message && <p className="mt-2 text-green-600">{message}</p>}
    </div>
  );
};

export default RegisterStudent;
