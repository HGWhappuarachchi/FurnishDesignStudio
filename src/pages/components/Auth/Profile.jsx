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
            className="animate-spin h-8 w-8 text-indigo-600 mb-4"
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
          <p className="text-gray-600 text-sm">Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 bg-white p-8 rounded-xl shadow-lg border border-gray-100">
        {error && (
          <div className="p-3 bg-red-50 text-red-600 text-sm rounded-lg text-center">
            {error}
          </div>
        )}

        <div className="text-center space-y-4">
          <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-indigo-100">
            <svg
              className="h-8 w-8 text-indigo-600"
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
          <h2 className="text-2xl font-bold text-gray-900">Your Profile</h2>
          <p className="text-sm text-gray-500">Manage your designer details</p>
        </div>

        {profile && (
          <div className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  Name
                </label>
                <p className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700">
                  {profile.displayName}
                </p>
              </div>
              <div className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  Email
                </label>
                <p className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700">
                  {profile.email}
                </p>
              </div>
              <div className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  User ID
                </label>
                <p className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 font-mono text-sm">
                  {profile.uid}
                </p>
              </div>
            </div>

            <div className="mt-4 space-y-4">
              <button
                onClick={() => navigate("/edit-profile")}
                className="w-full py-2 px-4 rounded-lg text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all"
              >
                Edit Profile
              </button>

              <button
                onClick={handleLogout}
                className="w-full py-2 px-4 rounded-lg text-red-500 border border-red-500 hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-all"
              >
                Log out
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Profile;
