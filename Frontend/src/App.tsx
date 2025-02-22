import React, { useState, useCallback, useRef } from 'react';
import Webcam from 'react-webcam';
import axios from 'axios';
import {
  Camera,
  Bell,
  LogOut,
  Download,
  Search,
  Users,
  Calendar,
  BookOpen,
  RefreshCw,
  StopCircle,
} from 'lucide-react';

function App() {
  const [isCapturing, setIsCapturing] = useState(false);
  const webcamRef = useRef<Webcam | null>(null);
  const [selectedClass, setSelectedClass] = useState('CS-2024');
  const [selectedSubject, setSelectedSubject] = useState('Advanced A');
  const [selectedHour, setSelectedHour] = useState('09:00');

  const currentDate = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  const startSession = useCallback(() => {
    setIsCapturing(true);
  }, []);

  const stopSession = useCallback(() => {
    setIsCapturing(false);
  }, []);

  const captureImage = useCallback(async () => {
    if (webcamRef.current) {
      const imageSrc = webcamRef.current.getScreenshot();
      if (imageSrc) {
        try {
          // Replace with your actual API endpoint
          const response = await axios.post('/api/attendance', {
            image: imageSrc,
            class: selectedClass,
            subject: selectedSubject,
            date: new Date().toISOString(),
            classHour: selectedHour,
          });
          console.log('Attendance recorded:', response.data);
        } catch (error) {
          console.error('Error recording attendance:', error);
        }
      }
    }
  }, [selectedClass, selectedSubject, selectedHour]);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-[#6366F1] text-white px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Camera className="w-6 h-6" />
          <h1 className="text-xl font-semibold">AI Attendance System</h1>
        </div>
        <div className="flex items-center gap-4">
          <button className="p-2 text-white/80 hover:text-white">
            <Bell className="w-5 h-5" />
          </button>
          <div className="flex items-center gap-2">
            <span>Dr. Aman</span>
            <button className="p-2 text-white/80 hover:text-white">
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="p-6 max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold">Teacher Dashboard</h2>
          <div className="flex gap-3">
            {!isCapturing ? (
              <button
                onClick={startSession}
                className="flex items-center gap-2 px-4 py-2 bg-[#6366F1] text-white rounded-lg hover:bg-[#4F46E5]"
              >
                <RefreshCw className="w-4 h-4" />
                Start Session
              </button>
            ) : (
              <button
                onClick={stopSession}
                className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                <StopCircle className="w-4 h-4" />
                Stop Session
              </button>
            )}
            <button className="flex items-center gap-2 px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700">
              <Download className="w-4 h-4" />
              Export Report
            </button>
          </div>
        </div>

        {/* Filters */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white p-4 rounded-lg shadow-sm">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <Users className="w-4 h-4 inline mr-2" />
              Select Section
            </label>
            <select
              value={selectedClass}
              onChange={(e) => setSelectedClass(e.target.value)}
              className="w-full border-gray-200 rounded-lg"
            >
              <option>K23UP</option>
              <option>K23CH</option>
            </select>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-sm">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <BookOpen className="w-4 h-4 inline mr-2" />
              Select Subject
            </label>
            <select
              value={selectedSubject}
              onChange={(e) => setSelectedSubject(e.target.value)}
              className="w-full border-gray-200 rounded-lg"
            >
              <option>CSE322</option>
              <option>MTH302</option>
              <option>CSE306</option>
              <option>CSE307</option>
              <option>PEA305</option>
              <option>CSM350</option>
              <option>CSM230</option>
            </select>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-sm">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <Calendar className="w-4 h-4 inline mr-2" />
              Class Time
            </label>
            <select
              value={selectedHour}
              onChange={(e) => setSelectedHour(e.target.value)}
              className="w-full border-gray-200 rounded-lg"
            >
              <option value="09:00">09 - 10 </option>
              <option value="10:00">10 - 11 </option>
              <option value="11:00">11 - 12 </option>
              <option value="12:00">12 - 01 </option>
              <option value="13:00">13 - 14 </option>
              <option value="14:00">14 - 15 </option>
              <option value="15:00">15 - 16 </option>
              <option value="16:00">16 - 17 </option>
            </select>
            <p className="mt-2 text-sm text-gray-500">{currentDate}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-sm">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Search
            </label>
            <div className="relative">
              <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input
                type="text"
                placeholder="Search student"
                className="pl-10 pr-4 py-2 w-full border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="bg-sky-50 p-6 rounded-xl">
            <div className="flex justify-between items-center">
              <h3 className="text-sky-700">Total Count</h3>
              <span className="text-sky-600">📋</span>
            </div>
            <div className="mt-2">
              <span className="text-3xl font-bold text-sky-700">65</span>
              <span className="text-sky-600 ml-2">100%</span>
            </div>
          </div>
          <div className="bg-emerald-50 p-6 rounded-xl">
            <div className="flex justify-between items-center">
              <h3 className="text-emerald-700">Present</h3>
              <span className="text-emerald-600">✓</span>
            </div>
            <div className="mt-2">
              <span className="text-3xl font-bold text-emerald-700">53</span>
              <span className="text-emerald-600 ml-2">81.5%</span>
            </div>
          </div>
          <div className="bg-red-50 p-6 rounded-xl">
            <div className="flex justify-between items-center">
              <h3 className="text-red-700">Absent</h3>
              <span className="text-red-600">✕</span>
            </div>
            <div className="mt-2">
              <span className="text-3xl font-bold text-red-700">12</span>
              <span className="text-red-600 ml-2">18.4%</span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Webcam Section */}
          <div className="bg-white p-6 rounded-xl shadow-sm">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Face Recognition Session</h3>
            <div className="aspect-video bg-gray-100 rounded-lg overflow-hidden mb-4">
              {isCapturing ? (
                <Webcam
                  ref={webcamRef}
                  audio={false}
                  screenshotFormat="image/jpeg"
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center">
                  <Camera className="w-16 h-16 text-gray-400" />
                </div>
              )}
            </div>
            {!isCapturing ? (
              <button
                onClick={startSession}
                className="w-full py-3 bg-[#6366F1] text-white rounded-lg hover:bg-[#4F46E5] flex items-center justify-center gap-2"
              >
                <Camera className="w-5 h-5" />
                Mark Attendance
              </button>
            ) : (
              <button
                onClick={stopSession}
                className="w-full py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 flex items-center justify-center gap-2"
              >
                <StopCircle className="w-5 h-5" />
                Confirm Attendance
              </button>
            )}
          </div>

          {/* Attendance Records */}
          <div className="bg-white rounded-xl shadow-sm">
            <div className="p-6 border-b border-gray-200 flex justify-between items-center">
              <h3 className="text-lg font-medium text-gray-900">Attendance Records</h3>
              <button className="text-gray-400 hover:text-gray-500">
                <Download className="w-5 h-5" />
              </button>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Student</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Time</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Verification</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  <tr>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">Ayushman</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">15 - 16 </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                        Present
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">Face</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-indigo-600 hover:text-indigo-900">
                      <button>Edit</button>
                    </td>
                  </tr>
                  <tr>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">Sajal</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">15 - 16 </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                        Absent
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">Face</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-indigo-600 hover:text-indigo-900">
                      <button>Edit</button>
                    </td>
                  </tr>
                  <tr>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">Deepak</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">NIL </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-black text-white">
                       Blocked
                    </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">Face</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-indigo-600 hover:text-indigo-900">
                      <button>Edit</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;