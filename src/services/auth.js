import { initializeApp } from "firebase/app";
import {
  createUserWithEmailAndPassword,
  onAuthStateChanged as firebaseOnAuthStateChanged,
  signOut as firebaseSignOut,
  getAuth,
  signInWithEmailAndPassword,
  updateProfile,
} from "firebase/auth";

const firebaseConfig = {
  apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
  authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID,
  storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.REACT_APP_FIREBASE_APP_ID,
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const firebaseAuth = getAuth(app);

// Login with Firebase authentication
const login = async (email, password) => {
  try {
    const userCredential = await signInWithEmailAndPassword(
      firebaseAuth,
      email,
      password
    );
    return userCredential.user;
  } catch (error) {
    throw new Error(getAuthErrorMessage(error.code));
  }
};

// Signup with Firebase authentication
const signup = async (email, password, displayName) => {
  try {
    // Create the user with email and password
    const userCredential = await createUserWithEmailAndPassword(
      firebaseAuth,
      email,
      password
    );

    // Update the user profile with the display name
    await updateProfile(userCredential.user, {
      displayName: displayName,
    });

    // Also save the user profile to your backend API if needed
    try {
      await fetch("http://localhost:5000/api/auth/profile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          uid: userCredential.user.uid,
          email,
          displayName,
        }),
      });
    } catch (apiError) {
      console.error(
        "Failed to save profile to API, but Firebase auth succeeded:",
        apiError
      );
    }

    return userCredential.user;
  } catch (error) {
    throw new Error(getAuthErrorMessage(error.code));
  }
};

// Logout function
const logout = async () => {
  try {
    await firebaseSignOut(firebaseAuth);
  } catch (error) {
    throw new Error(getAuthErrorMessage(error.code));
  }
};

// Get user profile from backend if needed
const getProfile = async (uid) => {
  try {
    // First try to get profile from Firebase auth
    const currentUser = firebaseAuth.currentUser;
    if (currentUser) {
      return {
        uid: currentUser.uid,
        email: currentUser.email,
        displayName: currentUser.displayName,
        // Add more user properties as needed
      };
    }

    // Fallback to API if needed
    const response = await fetch(
      `http://localhost:5000/api/auth/profile/${uid}`
    );
    if (!response.ok) {
      throw new Error("Failed to fetch profile");
    }
    return await response.json();
  } catch (error) {
    console.error("Error getting profile:", error);
    throw error;
  }
};

// Helper function to convert Firebase error codes to user-friendly messages
const getAuthErrorMessage = (errorCode) => {
  switch (errorCode) {
    case "auth/invalid-email":
      return "Invalid email address.";
    case "auth/user-disabled":
      return "User account has been disabled.";
    case "auth/user-not-found":
      return "User not found. Please sign up first.";
    case "auth/wrong-password":
      return "Incorrect password.";
    case "auth/email-already-in-use":
      return "Email is already in use. Please login instead.";
    case "auth/weak-password":
      return "Password is too weak. Use at least 6 characters.";
    case "auth/operation-not-allowed":
      return "Operation not allowed.";
    case "auth/too-many-requests":
      return "Too many failed login attempts. Please try again later.";
    default:
      return "An error occurred. Please try again.";
  }
};

// Create an auth service object to export
const authService = {
  login,
  signup,
  logout,
  getProfile,
  onAuthStateChanged: (callback) =>
    firebaseOnAuthStateChanged(firebaseAuth, callback),
  getCurrentUser: () => firebaseAuth.currentUser,
};

export default authService;
