import React, { useState, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Stage, Layer, Rect, Text } from "react-konva";
import { furnitureCatalog } from "./FurnitureCatalog";
import { useAuth } from "../../src/pages/context/AuthContext";
import { db } from "../firebase";
import {
  collection,
  addDoc,
  getDocs,
  updateDoc,
  deleteDoc,
  doc,
} from "firebase/firestore";

// Predefined room templates
const roomTemplates = [
  {
    id: "living-room",
    name: "Living Room",
    dimensions: { width: 50, length: 50 },
    defaultFurniture: [
      {
        type: "sofa",
        name: "Sofa",
        x: 4,
        y: 4,
        width: 8,
        length: 3,
        color: "#8B4513",
      },
      {
        type: "coffee-table",
        name: "Coffee Table",
        x: 8,
        y: 8,
        width: 4,
        length: 2,
        color: "#4682B4",
      },
      {
        type: "arm-chair",
        name: "Arm Chair",
        x: 12,
        y: 4,
        width: 3,
        length: 3,
        color: "#228B22",
      },
    ],
    suggestedWallColor: "#f5f5f5",
    suggestedFloorColor: "#ffffff",
    suggestedFloorType: "tile",
    suggestedFloorTexture: "/assets/marble.jpg",
  },
  {
    id: "bedroom",
    name: "Bedroom",
    dimensions: { width: 50, length: 50 },
    defaultFurniture: [
      {
        type: "double-bed",
        name: "Double Bed",
        x: 4,
        y: 4,
        width: 8,
        length: 6,
        color: "#ffffff",
      },
      {
        type: "dresser",
        name: "Dresser",
        x: 12,
        y: 4,
        width: 3,
        length: 2,
        color: "#8B4513",
      },
      {
        type: "nightstand",
        name: "Nightstand",
        x: 2,
        y: 4,
        width: 2,
        length: 2,
        color: "#8B4513",
      },
    ],
    suggestedWallColor: "#f5f5f5",
    suggestedFloorColor: "#ffffff",
    suggestedFloorType: "tile",
    suggestedFloorTexture: "/assets/marble.jpg",
  },
  {
    id: "home-office",
    name: "Home Office",
    dimensions: { width: 50, length: 50 },
    defaultFurniture: [
      {
        type: "desk",
        name: "Desk",
        x: 2,
        y: 2,
        width: 6,
        length: 3,
        color: "#2F4F4F",
      },
      {
        type: "computer-chair",
        name: "Computer Chair",
        x: 4,
        y: 6,
        width: 2,
        length: 2,
        color: "#333333",
      },
      {
        type: "bookshelf",
        name: "Bookshelf",
        x: 9,
        y: 2,
        width: 2,
        length: 1,
        color: "#8B4513",
      },
    ],
    suggestedWallColor: "#f5f5f5",
    suggestedFloorColor: "#ffffff",
    suggestedFloorType: "tile",
    suggestedFloorTexture: "/assets/marble.jpg",
  },
  {
    id: "dining-room",
    name: "Dining Room",
    dimensions: { width: 20, length: 20 },
    defaultFurniture: [
      {
        type: "dining-table",
        name: "Dining Table",
        x: 4,
        y: 4,
        width: 10,
        length: 10,
        color: "#FFFFFF",
      },
    ],
    suggestedWallColor: "#f5f5f5",
    suggestedFloorColor: "#ffffff",
    suggestedFloorType: "tile",
    suggestedFloorTexture: "/assets/marble.jpg",
  },
];

function RoomDesignPage() {
  const [dimensions, setDimensions] = useState({ width: "", length: "" });
  const [wallColor, setWallColor] = useState("#ffffff");
  const [floorColor, setFloorColor] = useState("#ffffff");
  const [showTemplatesPopup, setShowTemplatesPopup] = useState(false);
  const [showDesignsPopup, setShowDesignsPopup] = useState(false);
  const [floorType, setFloorType] = useState("tile");
  const [floorTexture, setFloorTexture] = useState("");
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [furniture, setFurniture] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState(null);
  const [designs, setDesigns] = useState([]);
  const [selectedDesignId, setSelectedDesignId] = useState(null);
  const [designName, setDesignName] = useState("");
  const { currentUser } = useAuth();
  const navigate = useNavigate();
  const stageRef = useRef(null);

  // Scaling factors
  const scale = 10;
  const roomScale = 25;

  const textureOptions = {
    tile: [
      { name: "Ceramic", path: "/assets/tile.jpg" },
      { name: "Marble", path: "/assets/marble.jpg" },
    ],
    carpet: [
      { name: "Gray Wool", path: "/textures/carpet_gray.jpg" },
      { name: "Beige Shag", path: "/textures/carpet_beige.jpg" },
    ],
    material: [
      { name: "Hardwood Oak", path: "/textures/hardwood_oak.jpg" },
      { name: "Bamboo", path: "/textures/bamboo.jpg" },
    ],
  };

  // Sanitize email for Firestore path
  const sanitizeEmail = (email) => {
    return email.replace(/[@.]/g, "_");
  };

  // Fetch designs on mount or user change
  useEffect(() => {
    if (currentUser && currentUser.email) {
      fetchDesigns();
    } else {
      setDesigns([]);
    }
  }, [currentUser]);

  // Cleanup Konva stage
  useEffect(() => {
    return () => {
      if (stageRef.current && stageRef.current.container()) {
        stageRef.current.destroy();
        stageRef.current = null;
      }
    };
  }, []);

  // Fetch designs from Firestore
  const fetchDesigns = async () => {
    if (!currentUser || !currentUser.email) return;
    try {
      const sanitizedEmail = sanitizeEmail(currentUser.email);
      const designsCollection = collection(
        db,
        `designs/${sanitizedEmail}/userDesigns`
      );
      const snapshot = await getDocs(designsCollection);
      const designsData = snapshot.docs.map((doc) => ({
        id: doc.id,
        ...doc.data(),
      }));
      setDesigns(designsData);
    } catch (error) {
      console.error("Error fetching designs:", error);
      alert("Failed to fetch designs.");
    }
  };

  // Save or update design
  const saveDesign = async () => {
    if (!currentUser || !currentUser.email) {
      alert("You must be logged in to save designs.");
      return;
    }
    if (!designName) {
      alert("Please enter a design name.");
      return;
    }

    const designData = {
      name: designName,
      dimensions: {
        width: parseFloat(dimensions.width) || 0,
        length: parseFloat(dimensions.length) || 0,
      },
      wallColor,
      floorColor,
      floorType,
      floorTexture,
      furniture,
      templateId: selectedTemplate,
      userEmail: currentUser.email,
      createdAt: new Date().toISOString(),
    };

    try {
      const sanitizedEmail = sanitizeEmail(currentUser.email);
      if (selectedDesignId) {
        // Update existing design
        const designRef = doc(
          db,
          `designs/${sanitizedEmail}/userDesigns`,
          selectedDesignId
        );
        await updateDoc(designRef, designData);
        alert("Design updated successfully!");
      } else {
        // Save new design
        const designsCollection = collection(
          db,
          `designs/${sanitizedEmail}/userDesigns`
        );
        const docRef = await addDoc(designsCollection, designData);
        setSelectedDesignId(docRef.id);
        alert("Design saved successfully!");
      }
      fetchDesigns();
    } catch (error) {
      console.error("Error saving design:", error);
      alert("Failed to save design: " + error.message);
    }
  };

  // Load a design
  const loadDesign = (design) => {
    setSelectedDesignId(design.id);
    setDesignName(design.name);
    setDimensions({
      width: design.dimensions.width.toString(),
      length: design.dimensions.length.toString(),
    });
    setWallColor(design.wallColor);
    setFloorColor(design.floorColor);
    setFloorType(design.floorType);
    setFloorTexture(design.floorTexture);
    setFurniture(design.furniture);
    setSelectedTemplate(design.templateId);
    setIsSubmitted(true);
    setShowDesignsPopup(false);
  };

  // Delete a design
  const deleteDesign = async (designId) => {
    if (!currentUser || !currentUser.email) {
      alert("You must be logged in to delete designs.");
      return;
    }
    if (!window.confirm("Are you sure you want to delete this design?")) return;
    try {
      const sanitizedEmail = sanitizeEmail(currentUser.email);
      const designRef = doc(
        db,
        `designs/${sanitizedEmail}/userDesigns`,
        designId
      );
      await deleteDoc(designRef);
      alert("Design deleted successfully!");
      fetchDesigns();
      if (selectedDesignId === designId) {
        resetDesign();
      }
    } catch (error) {
      console.error("Error deleting design:", error);
      alert("Failed to delete design: " + error.message);
    }
  };

  // Reset design state
  const resetDesign = () => {
    setSelectedDesignId(null);
    setDesignName("");
    setDimensions({ width: "", length: "" });
    setWallColor("#ffffff");
    setFloorColor("#ffffff");
    setFloorType("tile");
    setFloorTexture("");
    setFurniture([]);
    setSelectedTemplate(null);
    setIsSubmitted(false);
  };

  const TemplatesPopup = () => (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-2xl shadow-lg max-w-2xl w-full max-h-[80vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-bold text-gray-900">Room Templates</h2>
          <button
            onClick={() => setShowTemplatesPopup(false)}
            className="text-gray-500 hover:text-gray-700"
          >
            <svg
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {roomTemplates.map((template) => (
            <div
              key={template.id}
              className={`p-4 border rounded-lg cursor-pointer transition-all duration-200 ${
                selectedTemplate === template.id
                  ? "border-indigo-500 bg-indigo-50"
                  : "border-gray-200 hover:border-indigo-300 hover:bg-gray-50"
              }`}
              onClick={() => {
                applyTemplate(template);
                setShowTemplatesPopup(false);
              }}
            >
              <h3 className="text-lg font-semibold text-gray-900">
                {template.name}
              </h3>
              <p className="text-sm text-gray-600 mt-1">
                {template.dimensions.width}ft × {template.dimensions.length}ft
              </p>
              <div className="mt-2 flex flex-wrap gap-1">
                {template.defaultFurniture.map((item) => (
                  <span
                    key={item.name}
                    className="px-2 py-1 bg-gray-100 text-xs rounded-full text-gray-700"
                  >
                    {item.name}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );

  const SavedDesignsPopup = () => (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-2xl shadow-lg max-w-2xl w-full max-h-[80vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-bold text-gray-900">Saved Designs</h2>
          <button
            onClick={() => setShowDesignsPopup(false)}
            className="text-gray-500 hover:text-gray-700"
          >
            <svg
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {designs.length === 0 ? (
            <p className="text-gray-600 col-span-2">No saved designs found.</p>
          ) : (
            designs.map((design) => (
              <div
                key={design.id}
                className={`p-4 border rounded-lg transition-all duration-200 ${
                  selectedDesignId === design.id
                    ? "border-indigo-500 bg-indigo-50"
                    : "border-gray-200 hover:border-indigo-300 hover:bg-gray-50"
                }`}
              >
                <div className="flex justify-between items-center">
                  <div
                    className="cursor-pointer flex-1"
                    onClick={() => loadDesign(design)}
                  >
                    <h3 className="text-lg font-semibold text-gray-900">
                      {design.name}
                    </h3>
                    <p className="text-sm text-gray-600 mt-1">
                      {design.dimensions.width}ft × {design.dimensions.length}ft
                    </p>
                  </div>
                  <button
                    onClick={() => deleteDesign(design.id)}
                    className="text-red-600 hover:text-red-800"
                    title="Delete design"
                  >
                    <svg
                      className="h-5 w-5"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5-4h4M9 7v12m6-12v12"
                      />
                    </svg>
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );

  const applyTemplate = (template) => {
    setSelectedTemplate(template.id);
    setDimensions({
      width: template.dimensions.width.toString(),
      length: template.dimensions.length.toString(),
    });
    setWallColor(template.suggestedWallColor);
    setFloorColor(template.suggestedFloorColor);
    setFloorType(template.suggestedFloorType);
    setFloorTexture(template.suggestedFloorTexture);

    const scaledFurniture = template.defaultFurniture.map((item) => {
      const catalogItem = furnitureCatalog.find(
        (f) =>
          f.id === item.type ||
          (f.subtypes && f.subtypes.some((s) => s.id === item.type))
      );

      let modelInfo;
      if (catalogItem) {
        if (catalogItem.subtypes) {
          const subtype = catalogItem.subtypes.find((s) => s.id === item.type);
          modelInfo = subtype.models[0];
        } else {
          modelInfo = catalogItem.models[0];
        }
      }

      return {
        id: `${item.type}-${Date.now()}`,
        type: item.type,
        name: item.name,
        x: item.x * scale,
        y: item.y * scale,
        width: item.width * scale,
        length: item.length * scale,
        color: item.color,
        modelId: modelInfo?.id || "default",
        modelPath: modelInfo?.path || "",
      };
    });

    setFurniture(scaledFurniture);
    setIsSubmitted(true);
  };

  const addFurniture = (item, subtype) => {
    const furnitureItem = subtype || item;
    setFurniture((prev) => [
      ...prev,
      {
        id: `${furnitureItem.id}-${Date.now()}`,
        type: furnitureItem.id,
        name: furnitureItem.name,
        x: 50,
        y: 50,
        width: furnitureItem.width * scale,
        length: furnitureItem.length * scale,
        color: furnitureItem.color,
        modelId: furnitureItem.models[0].id,
        modelPath: furnitureItem.models[0].path,
      },
    ]);
  };

  const handleDragEnd = (e, id) => {
    const node = e.target;
    const scaleFactor = roomScale / scale;
    setFurniture((prev) =>
      prev.map((f) =>
        f.id === id
          ? {
              ...f,
              x: node.x() / scaleFactor,
              y: node.y() / scaleFactor,
            }
          : f
      )
    );
  };

  const handleColorChange = (id, newColor) => {
    setFurniture((prev) =>
      prev.map((f) => (f.id === id ? { ...f, color: newColor } : f))
    );
  };

  const handleDimensionChange = (e) => {
    const { name, value } = e.target;
    setDimensions((prev) => ({ ...prev, [name]: value }));
    setSelectedTemplate(null);
  };

  const handleFloorTypeChange = (e) => {
    setFloorType(e.target.value);
    setFloorTexture("");
  };

  const handleFloorTextureChange = (e) => {
    setFloorTexture(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setIsSubmitted(true);
  };

  const goTo3DView = () => {
    navigate("/test3dView", {
      state: {
        room: {
          dimensions: {
            width: parseFloat(dimensions.width) || 0,
            length: parseFloat(dimensions.length) || 0,
          },
          wallColor,
          floorColor,
          floorType,
          floorTexture,
        },
        furniture: furniture.map((item) => ({
          ...item,
          x:
            (item.x / (dimensions.width * scale)) *
            (parseFloat(dimensions.width) || 1),
          y:
            (item.y / (dimensions.length * scale)) *
            (parseFloat(dimensions.length) || 1),
          width: item.width / scale,
          length: item.length / scale,
        })),
      },
    });
  };

  return (
    <div className="min-h-screen bg-neutral-50 font-sans p-8">
      <header className="flex justify-between items-center mb-8">
        <div className="flex items-center">
          <svg
            className="h-8 w-8 text-indigo-600"
            fill="currentColor"
            stroke="currentColor"
            viewBox="0 0 640 512"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M64 160C64 89.3 121.3 32 192 32H448c70.7 0 128 57.3 128 128v33.6c-36.5 7.4-64 39.7-64 78.4v48H128V272c0-38.7-27.5-71-64-78.4V160zM544 272c0-20.9 13.4-38.7 32-45.3c5-1.8 10.4-2.7 16-2.7c26.5 0 48 21.5 48 48V448c0 17.7-14.3 32-32 32H576c-17.7 0-32-14.3-32-32H96c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V272c0-26.5 21.5-48 48-48c5.6 0 11 1 16 2.7c18.6 6.6 32 24.4 32 45.3v48 32h32H512h32V320 272z"
            />
          </svg>
          <span className="ml-2 text-xl font-bold text-gray-900">
            Furnish Studio
          </span>
        </div>
        <div className="flex gap-2">
          <Link
            to="/"
            className="px-4 py-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700"
          >
            Back to Home
          </Link>
        </div>
      </header>

      <div className="max-w-6xl mx-auto">
        {/* Customize Your Room */}
        <div className="flex justify-center">
          <div className="bg-white p-6 rounded-2xl shadow-lg w-full max-w-2xl">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              {selectedTemplate ? "Customize Your Room" : "Design Your Room"}
            </h2>
            <button
              type="button"
              onClick={() => setShowTemplatesPopup(true)}
              className="w-full px-6 py-3 bg-indigo-100 text-indigo-700 rounded-full hover:bg-indigo-200 transition-colors duration-300 mb-4"
            >
              Choose Room Template
            </button>
            <button
              type="button"
              onClick={() => setShowDesignsPopup(true)}
              className="w-full px-6 py-3 bg-indigo-100 text-indigo-700 rounded-full hover:bg-indigo-200 transition-colors duration-300 mb-6"
            >
              Choose Saved Design
            </button>
            <form onSubmit={handleSubmit}>
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900">
                  Design Name
                </h3>
                <input
                  type="text"
                  value={designName}
                  onChange={(e) => setDesignName(e.target.value)}
                  placeholder="Enter design name"
                  className="mt-2 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600"
                  required
                />
              </div>
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900">
                  Room Dimensions (in feet)
                </h3>
                <div className="grid grid-cols-2 gap-4 mt-2">
                  <div>
                    <label
                      htmlFor="width"
                      className="block text-sm font-medium text-gray-700"
                    >
                      Width
                    </label>
                    <input
                      type="number"
                      id="width"
                      name="width"
                      value={dimensions.width}
                      onChange={handleDimensionChange}
                      className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600"
                      required
                    />
                  </div>
                  <div>
                    <label
                      htmlFor="length"
                      className="block text-sm font-medium text-gray-700"
                    >
                      Length
                    </label>
                    <input
                      type="number"
                      id="length"
                      name="length"
                      value={dimensions.length}
                      onChange={handleDimensionChange}
                      className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600"
                      required
                    />
                  </div>
                </div>
              </div>
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900">
                  Room Colors
                </h3>
                <div className="grid grid-cols-2 gap-4 mt-2">
                  <div>
                    <label
                      htmlFor="wallColor"
                      className="block text-sm font-medium text-gray-700"
                    >
                      Wall Color
                    </label>
                    <input
                      type="color"
                      id="wallColor"
                      value={wallColor}
                      onChange={(e) => setWallColor(e.target.value)}
                      className="mt-1 p-1 w-full h-10 border rounded-md"
                    />
                  </div>
                  <div>
                    <label
                      htmlFor="floorColor"
                      className="block text-sm font-medium text-gray-700"
                    >
                      Floor Color
                    </label>
                    <input
                      type="color"
                      id="floorColor"
                      value={floorColor}
                      onChange={(e) => setFloorColor(e.target.value)}
                      className="mt-1 p-1 w-full h-10 border rounded-md"
                    />
                  </div>
                </div>
              </div>
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900">
                  Floor Type
                </h3>
                <div className="mt-2">
                  <label
                    htmlFor="floorType"
                    className="block text-sm font-medium text-gray-700"
                  >
                    Select Floor Type
                  </label>
                  <select
                    id="floorType"
                    value={floorType}
                    onChange={handleFloorTypeChange}
                    className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600"
                  >
                    <option value="tile">Tile</option>
                    <option value="carpet">Carpet</option>
                    <option value="material">Material Textured</option>
                  </select>
                </div>
                <div className="mt-4">
                  <label
                    htmlFor="floorTexture"
                    className="block text-sm font-medium text-gray-700"
                  >
                    Select Texture
                  </label>
                  <select
                    id="floorTexture"
                    value={floorTexture}
                    onChange={handleFloorTextureChange}
                    className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600"
                    disabled={!floorType}
                  >
                    <option value="">Select a texture</option>
                    {floorType &&
                      textureOptions[floorType].map((texture) => (
                        <option key={texture.path} value={texture.path}>
                          {texture.name}
                        </option>
                      ))}
                  </select>
                </div>
              </div>
              <div className="flex gap-2">
                <button
                  type="submit"
                  className="flex basis-1/4 px-4 py-3 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-300"
                >
                  {selectedTemplate
                    ? "Continue Customizing"
                    : "Create 2D Design"}
                </button>
                <button
                  type="button"
                  onClick={saveDesign}
                  className="flex basis-1/4 px-4 py-3 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-300"
                >
                  Save Design
                </button>
                <button
                  type="button"
                  onClick={saveDesign}
                  className="flex basis-1/4 px-4 py-3 bg-green-600 text-white rounded-full hover:bg-green-700 transition-colors duration-300"
                >
                  {selectedDesignId ? "Update Design" : "Save Design"}
                </button>
                <button
                  type="button"
                  onClick={resetDesign}
                  className="flex basis-1/4 px-4 py-3 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-300"
                >
                  New Design
                </button>
              </div>
            </form>
          </div>
        </div>
        {showTemplatesPopup && <TemplatesPopup />}
        {showDesignsPopup && <SavedDesignsPopup />}

        {/* Furniture Catalog and 2D Design Interface */}
        {isSubmitted && (
          <div className="mt-8 flex justify-center">
            <div className="flex gap-8 w-full max-w-5xl">
              <div className="w-1/4 bg-white p-4 rounded-2xl shadow-lg">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">
                  Furniture Catalog
                </h3>
                <div className="space-y-4">
                  {furnitureCatalog.map((item) => (
                    <div key={item.id}>
                      <p className="text-sm font-medium text-gray-900">
                        {item.name}
                      </p>
                      {item.subtypes ? (
                        item.subtypes.map((subtype) => (
                          <div
                            key={subtype.id}
                            className="p-2 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100 ml-4"
                            onClick={() => addFurniture(item, subtype)}
                          >
                            <p className="text-sm text-gray-900">
                              {subtype.name}
                            </p>
                            <p className="text-xs text-gray-600">
                              {subtype.width}ft x {subtype.length}ft
                            </p>
                          </div>
                        ))
                      ) : (
                        <div
                          className="p-2 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100"
                          onClick={() => addFurniture(item)}
                        >
                          <p className="text-sm text-gray-900">{item.name}</p>
                          <p className="text-xs text-gray-600">
                            {item.width}ft x {item.length}ft
                          </p>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </div>
              <div className="flex-1">
                <div className="flex justify-between items-center mb-4">
                  <h3 className="text-lg font-semibold text-gray-900">
                    2D Design Interface
                  </h3>
                  <button
                    onClick={goTo3DView}
                    className="px-4 py-3 bg-indigo-600 text-white rounded-full hover:bg-indigo-700"
                  >
                    View in 3D
                  </button>
                </div>
                <div className="bg-gray-100 rounded-2xl overflow-hidden">
                  <Stage
                    ref={stageRef}
                    width={dimensions.width * roomScale}
                    height={dimensions.length * roomScale}
                    style={{ backgroundColor: wallColor }}
                  >
                    <Layer>
                      <Rect
                        x={0}
                        y={0}
                        width={dimensions.width * roomScale}
                        height={dimensions.length * roomScale}
                        fill={floorColor}
                        stroke="black"
                        strokeWidth={2}
                      />
                      {furniture.map((item) => (
                        <React.Fragment key={item.id}>
                          <Rect
                            x={item.x * (roomScale / scale)}
                            y={item.y * (roomScale / scale)}
                            width={item.width}
                            height={item.length}
                            fill={item.color}
                            stroke="black"
                            strokeWidth={1}
                            draggable
                            onDragEnd={(e) => handleDragEnd(e, item.id)}
                          />
                          <Text
                            x={item.x * (roomScale / scale)}
                            y={item.y * (roomScale / scale) + item.length / 2}
                            width={item.width}
                            align="center"
                            text={item.name}
                            fontSize={12}
                            fontStyle="bold"
                            fill="black"
                          />
                        </React.Fragment>
                      ))}
                    </Layer>
                  </Stage>
                </div>
                <div className="mt-4">
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    Furniture Colors
                  </h3>
                  <div className="grid grid-cols-2 gap-4">
                    {furniture.map((item) => (
                      <div key={item.id} className="flex items-center gap-2">
                        <label className="text-sm font-medium text-gray-700">
                          {item.name}
                        </label>
                        <input
                          type="color"
                          value={item.color}
                          onChange={(e) =>
                            handleColorChange(item.id, e.target.value)
                          }
                          className="p-1 w-10 h-10 border rounded-md"
                        />
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default RoomDesignPage;
