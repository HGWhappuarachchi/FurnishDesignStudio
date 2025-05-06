import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function Profile() {
  const { currentUser, logout, getProfile } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchProfile() {
      if (currentUser) {
        try {
          const profileData = await getProfile(currentUser.uid);
          setProfile(profileData);
        } catch (error) {
          console.error("Error fetching profile:", error);
          setError("Failed to load profile data. Please try again.");
        } finally {
          setLoading(false);
        }
      }
    }

    fetchProfile();
  }, [currentUser, getProfile]);

  const handleLogout = async () => {
    try {
      await logout();
      navigate("/login");
    } catch (error) {
      setError("Failed to log out. Please try again.");
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex justify-center items-center bg-gray-50">
        <div className="flex flex-col items-center">
          <svg
            className="animate-spin h-10 w-10 text-indigo-600 mb-4"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            ></circle>
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            ></path>
          </svg>
          <p className="text-gray-600 text-lg">Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-6">
      <div className="bg-white shadow-2xl border-2 border-purple-300 rounded-2xl max-w-md w-full p-10">
        {error && (
          <div className="mb-6 p-3 bg-red-50 text-red-600 text-sm rounded-lg text-center">
            {error}
          </div>
        )}

        <div className="text-center mb-8">
          <div className="mx-auto flex items-center justify-center h-24 w-24 rounded-full bg-indigo-100 mb-6">
            <svg
              className="h-12 w-12 text-indigo-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14c-4.418 0-8 1.79-8 4v2h16v-2c0-2.21-3.582-4-8-4z"
              />
            </svg>
          </div>
          <h2 className="text-3xl font-bold text-gray-900 mb-2">
            Your Profile
          </h2>
          <p className="text-gray-500 text-sm">Manage your designer details</p>
        </div>

        {profile && (
          <div className="space-y-6">
            <div className="bg-gray-50 rounded-lg p-4">
              <p className="text-gray-700 mb-4">
                <span className="font-semibold text-gray-900 block mb-1">
                  Name
                </span>
                <span className="text-lg">{profile.displayName}</span>
              </p>
              <p className="text-gray-700 mb-4">
                <span className="font-semibold text-gray-900 block mb-1">
                  Email
                </span>
                <span className="text-lg">{profile.email}</span>
              </p>
              <p className="text-gray-700">
                <span className="font-semibold text-gray-900 block mb-1">
                  User ID
                </span>
                <span className="text-sm font-mono bg-gray-100 p-1 rounded">
                  {profile.uid}
                </span>
              </p>
            </div>

            <div className="space-y-4">
              <button
                onClick={() => navigate("/edit-profile")}
                className="w-full py-3 px-4 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Edit Profile
              </button>

              <button
                onClick={handleLogout}
                className="w-full py-3 px-4 bg-white border border-red-500 text-red-500 rounded-lg hover:bg-red-50 transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
              >
                Logout
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Profile;
