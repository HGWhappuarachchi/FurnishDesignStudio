const express = require("express");
const router = express.Router();
const admin = require("firebase-admin");
const db = admin.firestore();

// Middleware to verify JWT token
const verifyToken = async (req, res, next) => {
  const idToken = req.headers.authorization?.split("Bearer ")[1];

  if (!idToken) {
    return res.status(401).send("Unauthorized");
  }

  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    req.user = decodedToken;
    next();
  } catch (error) {
    console.error("Error verifying token:", error);
    res.status(401).send("Unauthorized");
  }
};

// Save a design
router.post("/", verifyToken, async (req, res) => {
  try {
    const { room, furniture } = req.body;
    const userId = req.user.uid;

    const designRef = await db.collection("designs").add({
      userId,
      room,
      furniture,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    res
      .status(201)
      .json({ id: designRef.id, message: "Design saved successfully" });
  } catch (error) {
    console.error("Error saving design:", error);
    res.status(500).send("Error saving design");
  }
});

// Get all designs for a user
router.get("/", verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    const designsSnapshot = await db
      .collection("designs")
      .where("userId", "==", userId)
      .orderBy("updatedAt", "desc")
      .get();

    const designs = [];
    designsSnapshot.forEach((doc) => {
      designs.push({
        id: doc.id,
        ...doc.data(),
      });
    });

    res.json(designs);
  } catch (error) {
    console.error("Error fetching designs:", error);
    res.status(500).send("Error fetching designs");
  }
});

// Get a single design
router.get("/:id", verifyToken, async (req, res) => {
  try {
    const designId = req.params.id;
    const designDoc = await db.collection("designs").doc(designId).get();

    if (!designDoc.exists) {
      return res.status(404).send("Design not found");
    }

    if (designDoc.data().userId !== req.user.uid) {
      return res.status(403).send("Forbidden");
    }

    res.json({
      id: designDoc.id,
      ...designDoc.data(),
    });
  } catch (error) {
    console.error("Error fetching design:", error);
    res.status(500).send("Error fetching design");
  }
});

// Update a design
router.put("/:id", verifyToken, async (req, res) => {
  try {
    const designId = req.params.id;
    const { room, furniture } = req.body;

    const designDoc = await db.collection("designs").doc(designId).get();
    if (!designDoc.exists || designDoc.data().userId !== req.user.uid) {
      return res.status(403).send("Forbidden");
    }

    await db.collection("designs").doc(designId).update({
      room,
      furniture,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    res.json({ message: "Design updated successfully" });
  } catch (error) {
    console.error("Error updating design:", error);
    res.status(500).send("Error updating design");
  }
});

// Delete a design
router.delete("/:id", verifyToken, async (req, res) => {
  try {
    const designId = req.params.id;

    const designDoc = await db.collection("designs").doc(designId).get();
    if (!designDoc.exists || designDoc.data().userId !== req.user.uid) {
      return res.status(403).send("Forbidden");
    }

    await db.collection("designs").doc(designId).delete();

    res.json({ message: "Design deleted successfully" });
  } catch (error) {
    console.error("Error deleting design:", error);
    res.status(500).send("Error deleting design");
  }
});

module.exports = router;
