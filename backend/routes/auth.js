const express = require("express");
const admin = require("firebase-admin");
const router = express.Router();

// Sign up endpoint
router.post("/signup", async (req, res) => {
  try {
    const { email, password, displayName } = req.body;

    if (!email || !password) {
      return res.status(400).json({ error: "Email and password are required" });
    }

    // Create user in Firebase Authentication
    const userRecord = await admin.auth().createUser({
      email,
      password,
      displayName: displayName || "",
      emailVerified: false,
    });

    // Store additional user data in Firestore
    await admin
      .firestore()
      .collection("users")
      .doc(userRecord.uid)
      .set({
        email,
        displayName: displayName || "",
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });

    return res.status(201).json({
      message: "User created successfully",
      uid: userRecord.uid,
    });
  } catch (error) {
    console.error("Error creating user:", error);
    return res.status(500).json({ error: error.message });
  }
});

// Login endpoint
router.post("/login", async (req, res) => {
  try {
    const { idToken } = req.body;

    if (!idToken) {
      return res.status(400).json({ error: "ID token is required" });
    }

    // Verify the ID token
    const decodedToken = await admin.auth().verifyIdToken(idToken);

    // Generate a custom token if needed
    const customToken = await admin.auth().createCustomToken(decodedToken.uid);

    return res.status(200).json({
      uid: decodedToken.uid,
      customToken,
    });
  } catch (error) {
    console.error("Error during login:", error);
    return res.status(401).json({ error: "Invalid authentication" });
  }
});

// Get user profile endpoint
router.get("/profile/:uid", async (req, res) => {
  try {
    const { uid } = req.params;

    // Get user from Authentication
    const userRecord = await admin.auth().getUser(uid);

    // Get additional user data from Firestore
    const userDoc = await admin.firestore().collection("users").doc(uid).get();

    if (!userDoc.exists) {
      return res.status(404).json({ error: "User not found" });
    }

    const userData = userDoc.data();

    return res.status(200).json({
      uid: userRecord.uid,
      email: userRecord.email,
      displayName: userRecord.displayName,
      emailVerified: userRecord.emailVerified,
      ...userData,
    });
  } catch (error) {
    console.error("Error getting user profile:", error);
    return res.status(500).json({ error: error.message });
  }
});

// Middleware to verify Firebase ID token
const verifyToken = async (req, res, next) => {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return res.status(401).json({ error: "Unauthorized" });
  }

  const idToken = authHeader.split("Bearer ")[1];

  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    req.user = decodedToken;
    next();
  } catch (error) {
    console.error("Error verifying token:", error);
    return res.status(401).json({ error: "Invalid token" });
  }
};

// Protected route example
router.get("/protected", verifyToken, (req, res) => {
  return res.status(200).json({
    message: "Protected data accessed successfully",
    user: req.user,
  });
});

module.exports = router;
