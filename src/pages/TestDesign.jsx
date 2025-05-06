import React, { useEffect, useRef, useState } from "react";
import { Layer, Rect, Stage, Text, Transformer } from "react-konva";
import { Link, useNavigate } from "react-router-dom";
import { furnitureCatalog } from "./FurnitureCatalog";

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
        type: "single-chair",
        name: "Single Chair",
        x: 4,
        y: 4,
        width: 6,
        length: 4,
        color: "#8B4513",
      },
      {
        type: "single-bed",
        name: "Single Bed",
        x: 3,
        y: 6,
        width: 2,
        length: 2,
        color: "#8B4513",
      },
      {
        type: "stool",
        name: "Stool",
        x: 9,
        y: 6,
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
];

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

function RoomTestDesignPage() {
  const [dimensions, setDimensions] = useState({ width: "", length: "" });
  const [wallColor, setWallColor] = useState("#ffffff");
  const [floorColor, setFloorColor] = useState("#ffffff");
  const [showTemplatesPopup, setShowTemplatesPopup] = useState(false);
  const [floorType, setFloorType] = useState("tile");
  const [floorTexture, setFloorTexture] = useState("");
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [furniture, setFurniture] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState(null);
  const [selectedFurnitureId, setSelectedFurnitureId] = useState(null);
  const [zoomLevel, setZoomLevel] = useState(1);
  const [designHistory, setDesignHistory] = useState([]);
  const [historyIndex, setHistoryIndex] = useState(-1);
  const [showSaveModal, setShowSaveModal] = useState(false);
  const [designName, setDesignName] = useState("");
  const [savedDesigns, setSavedDesigns] = useState([]);
  const [showLoadModal, setShowLoadModal] = useState(false);
  const navigate = useNavigate();
  const stageRef = useRef(null);
  const transformerRef = useRef(null);

  // Scaling factors
  const scale = 10;
  const roomScale = 25;

  // Load saved designs from localStorage on component mount
  useEffect(() => {
    const saved = localStorage.getItem("savedRoomDesigns");
    if (saved) {
      try {
        setSavedDesigns(JSON.parse(saved));
      } catch (e) {
        console.error("Failed to parse saved designs", e);
      }
    }
  }, []);

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

  const SaveDesignModal = () => (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-2xl shadow-lg max-w-md w-full">
        <h2 className="text-xl font-bold text-gray-900 mb-4">Save Design</h2>
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Design Name
          </label>
          <input
            type="text"
            value={designName}
            onChange={(e) => setDesignName(e.target.value)}
            className="w-full p-2 border rounded-md focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="My Awesome Room"
          />
        </div>
        <div className="flex justify-end gap-2">
          <button
            onClick={() => setShowSaveModal(false)}
            className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
          >
            Cancel
          </button>
          <button
            onClick={handleSaveDesign}
            disabled={!designName.trim()}
            className={`px-4 py-2 rounded-md ${
              !designName.trim()
                ? "bg-indigo-300 cursor-not-allowed"
                : "bg-indigo-600 hover:bg-indigo-700"
            } text-white`}
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );

  const LoadDesignModal = () => (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-2xl shadow-lg max-w-md w-full max-h-[80vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold text-gray-900">Load Design</h2>
          <button
            onClick={() => setShowLoadModal(false)}
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
        {savedDesigns.length === 0 ? (
          <p className="text-gray-600">No saved designs found.</p>
        ) : (
          <div className="space-y-2">
            {savedDesigns.map((design) => (
              <div
                key={design.id}
                className="p-3 border rounded-lg hover:bg-gray-50 cursor-pointer"
                onClick={() => handleLoadDesign(design)}
              >
                <div className="flex justify-between items-center">
                  <h3 className="font-medium text-gray-900">{design.name}</h3>
                  <span className="text-xs text-gray-500">
                    {new Date(design.date).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-sm text-gray-600 mt-1">
                  {design.data.dimensions.width}ft ×{" "}
                  {design.data.dimensions.length}ft
                </p>
                <div className="flex flex-wrap gap-1 mt-2">
                  {design.data.furniture.slice(0, 3).map((item) => (
                    <span
                      key={item.id}
                      className="px-2 py-1 bg-gray-100 text-xs rounded-full text-gray-700"
                    >
                      {item.name}
                    </span>
                  ))}
                  {design.data.furniture.length > 3 && (
                    <span className="px-2 py-1 bg-gray-100 text-xs rounded-full text-gray-700">
                      +{design.data.furniture.length - 3} more
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );

  useEffect(() => {
    // Update transformer when selected furniture changes
    if (transformerRef.current && selectedFurnitureId) {
      const node = stageRef.current.findOne(`#${selectedFurnitureId}`);
      if (node) {
        transformerRef.current.nodes([node]);
      } else {
        transformerRef.current.nodes([]);
      }
    }
  }, [selectedFurnitureId]);

  // Save current state to history when furniture changes
  useEffect(() => {
    if (furniture.length > 0 || historyIndex === -1) {
      const newHistory = designHistory.slice(0, historyIndex + 1);
      newHistory.push({
        furniture: [...furniture],
        dimensions: { ...dimensions },
        wallColor,
        floorColor,
        floorType,
        floorTexture,
      });
      setDesignHistory(newHistory);
      setHistoryIndex(newHistory.length - 1);
    }
  }, [furniture, dimensions, wallColor, floorColor, floorType, floorTexture]);

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
    const newFurniture = {
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
    };

    setFurniture((prev) => [...prev, newFurniture]);
    setSelectedFurnitureId(newFurniture.id);
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

  const handleTransformEnd = (e, id) => {
    const node = e.target;
    const scaleFactor = roomScale / scale;

    setFurniture((prev) =>
      prev.map((f) =>
        f.id === id
          ? {
              ...f,
              x: node.x() / scaleFactor,
              y: node.y() / scaleFactor,
              width: (node.width() * node.scaleX()) / scaleFactor,
              length: (node.height() * node.scaleY()) / scaleFactor,
            }
          : f
      )
    );

    // Reset scale after transform
    node.scaleX(1);
    node.scaleY(1);
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
            width: parseFloat(dimensions.width),
            length: parseFloat(dimensions.length),
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
            parseFloat(dimensions.width),
          y:
            (item.y / (dimensions.length * scale)) *
            parseFloat(dimensions.length),
          width: item.width / scale,
          length: item.length / scale,
        })),
      },
    });
  };

  const handleZoomIn = () => {
    setZoomLevel((prev) => Math.min(prev + 0.1, 2));
  };

  const handleZoomOut = () => {
    setZoomLevel((prev) => Math.max(prev - 0.1, 0.5));
  };

  const handleResetZoom = () => {
    setZoomLevel(1);
  };

  const handleDeleteFurniture = (id) => {
    setFurniture((prev) => prev.filter((f) => f.id !== id));
    if (selectedFurnitureId === id) {
      setSelectedFurnitureId(null);
    }
  };

  const undoDesign = () => {
    if (historyIndex > 0) {
      const newIndex = historyIndex - 1;
      setHistoryIndex(newIndex);
      const historyItem = designHistory[newIndex];
      setFurniture([...historyItem.furniture]);
      setDimensions({ ...historyItem.dimensions });
      setWallColor(historyItem.wallColor);
      setFloorColor(historyItem.floorColor);
      setFloorType(historyItem.floorType);
      setFloorTexture(historyItem.floorTexture);
    }
  };

  const redoDesign = () => {
    if (historyIndex < designHistory.length - 1) {
      const newIndex = historyIndex + 1;
      setHistoryIndex(newIndex);
      const historyItem = designHistory[newIndex];
      setFurniture([...historyItem.furniture]);
      setDimensions({ ...historyItem.dimensions });
      setWallColor(historyItem.wallColor);
      setFloorColor(historyItem.floorColor);
      setFloorType(historyItem.floorType);
      setFloorTexture(historyItem.floorTexture);
    }
  };

  const handleSaveDesign = () => {
    if (!designName.trim()) return;

    const designData = {
      id: Date.now().toString(),
      name: designName.trim(),
      date: new Date().toISOString(),
      data: {
        dimensions: {
          width: parseFloat(dimensions.width),
          length: parseFloat(dimensions.length),
        },
        wallColor,
        floorColor,
        floorType,
        floorTexture,
        furniture: furniture.map((item) => ({
          ...item,
          x: item.x / scale,
          y: item.y / scale,
          width: item.width / scale,
          length: item.length / scale,
        })),
      },
    };

    const updatedDesigns = [
      designData,
      ...savedDesigns.filter((d) => d.id !== designData.id),
    ].slice(0, 20); // Keep only the 20 most recent designs

    localStorage.setItem("savedRoomDesigns", JSON.stringify(updatedDesigns));
    setSavedDesigns(updatedDesigns);
    setShowSaveModal(false);
    setDesignName("");
  };

  const handleLoadDesign = (design) => {
    const scaledFurniture = design.data.furniture.map((item) => ({
      ...item,
      x: item.x * scale,
      y: item.y * scale,
      width: item.width * scale,
      length: item.length * scale,
    }));

    setDimensions({
      width: design.data.dimensions.width.toString(),
      length: design.data.dimensions.length.toString(),
    });
    setWallColor(design.data.wallColor);
    setFloorColor(design.data.floorColor);
    setFloorType(design.data.floorType);
    setFloorTexture(design.data.floorTexture);
    setFurniture(scaledFurniture);
    setIsSubmitted(true);
    setShowLoadModal(false);
    setSelectedTemplate(null);
  };

  return (
    <div className="min-h-screen bg-neutral-50 font-sans p-4 md:p-8">
      <header className="flex justify-between items-center mb-6 md:mb-8">
        {/* Left section: Logo and title */}
        <div className="flex items-center flex-1">
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
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span className="ml-2 text-xl font-bold text-gray-900">
            Furnish Studio
          </span>
        </div>

        {/* Center section: Test Design text */}
        <div className="flex-1 text-center">
          <span className="text-lg font-medium text-gray-700">Test Design</span>
        </div>

        {/* Right section: Back to Home button */}
        <div className="flex-1 flex justify-end">
          <Link
            to="/"
            className="px-4 py-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-200 text-sm md:text-base"
          >
            Back to Home
          </Link>
        </div>
      </header>

      <div className="max-w-6xl mx-auto">
        <div className="flex justify-center">
          <div className="bg-white p-4 md:p-6 rounded-xl md:rounded-2xl shadow-md md:shadow-lg w-full max-w-2xl">
            <h2 className="text-xl md:text-2xl font-bold text-gray-900 mb-4 md:mb-6">
              {selectedTemplate ? "Customize Your Room" : "Design Your Room"}
            </h2>
            <button
              type="button"
              onClick={() => setShowTemplatesPopup(true)}
              className="w-full px-4 md:px-6 py-2 md:py-3 bg-indigo-100 text-indigo-700 rounded-full hover:bg-indigo-200 transition-colors duration-300 mb-4 md:mb-6 text-sm md:text-base"
            >
              Choose Room Template
            </button>
            <form onSubmit={handleSubmit}>
              <div className="mb-4 md:mb-6">
                <h3 className="text-base md:text-lg font-semibold text-gray-900">
                  Room Dimensions (in feet)
                </h3>
                <div className="grid grid-cols-2 gap-3 md:gap-4 mt-2">
                  <div>
                    <label
                      htmlFor="width"
                      className="block text-xs md:text-sm font-medium text-gray-700"
                    >
                      Width
                    </label>
                    <input
                      type="number"
                      id="width"
                      name="width"
                      value={dimensions.width}
                      onChange={handleDimensionChange}
                      className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600 text-sm md:text-base"
                      required
                      min="1"
                    />
                  </div>
                  <div>
                    <label
                      htmlFor="length"
                      className="block text-xs md:text-sm font-medium text-gray-700"
                    >
                      Length
                    </label>
                    <input
                      type="number"
                      id="length"
                      name="length"
                      value={dimensions.length}
                      onChange={handleDimensionChange}
                      className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600 text-sm md:text-base"
                      required
                      min="1"
                    />
                  </div>
                </div>
              </div>

              <div className="mb-4 md:mb-6">
                <h3 className="text-base md:text-lg font-semibold text-gray-900">
                  Room Colors
                </h3>
                <div className="grid grid-cols-2 gap-3 md:gap-4 mt-2">
                  <div>
                    <label
                      htmlFor="wallColor"
                      className="block text-xs md:text-sm font-medium text-gray-700"
                    >
                      Wall Color
                    </label>
                    <div className="flex items-center mt-1 gap-2">
                      <input
                        type="color"
                        id="wallColor"
                        value={wallColor}
                        onChange={(e) => setWallColor(e.target.value)}
                        className="p-1 w-8 h-8 md:w-10 md:h-10 border rounded-md cursor-pointer"
                      />
                      <span className="text-xs md:text-sm text-gray-600">
                        {wallColor}
                      </span>
                    </div>
                  </div>
                  <div>
                    <label
                      htmlFor="floorColor"
                      className="block text-xs md:text-sm font-medium text-gray-700"
                    >
                      Floor Color
                    </label>
                    <div className="flex items-center mt-1 gap-2">
                      <input
                        type="color"
                        id="floorColor"
                        value={floorColor}
                        onChange={(e) => setFloorColor(e.target.value)}
                        className="p-1 w-8 h-8 md:w-10 md:h-10 border rounded-md cursor-pointer"
                      />
                      <span className="text-xs md:text-sm text-gray-600">
                        {floorColor}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="mb-4 md:mb-6">
                <h3 className="text-base md:text-lg font-semibold text-gray-900">
                  Floor Type
                </h3>
                <div className="mt-2">
                  <label
                    htmlFor="floorType"
                    className="block text-xs md:text-sm font-medium text-gray-700"
                  >
                    Select Floor Type
                  </label>
                  <select
                    id="floorType"
                    value={floorType}
                    onChange={handleFloorTypeChange}
                    className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600 text-sm md:text-base"
                  >
                    <option value="tile">Tile</option>
                    <option value="carpet">Carpet</option>
                    <option value="material">Material Textured</option>
                  </select>
                </div>
                <div className="mt-3 md:mt-4">
                  <label
                    htmlFor="floorTexture"
                    className="block text-xs md:text-sm font-medium text-gray-700"
                  >
                    Select Texture
                  </label>
                  <select
                    id="floorTexture"
                    value={floorTexture}
                    onChange={handleFloorTextureChange}
                    className="mt-1 p-2 w-full border rounded-md focus:ring-indigo-600 focus:border-indigo-600 text-sm md:text-base"
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

              <button
                type="submit"
                className="w-full px-4 md:px-6 py-2 md:py-3 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-300 text-sm md:text-base"
              >
                {selectedTemplate ? "Continue Customizing" : "Create 2D Design"}
              </button>
            </form>
          </div>
        </div>
        {showTemplatesPopup && <TemplatesPopup />}
        {showSaveModal && <SaveDesignModal />}
        {showLoadModal && <LoadDesignModal />}

        {isSubmitted && (
          <div className="mt-6 md:mt-8">
            <div className="flex flex-col lg:flex-row gap-4 md:gap-8 w-full">
              <div className="w-full lg:w-1/4 bg-white p-3 md:p-4 rounded-xl md:rounded-2xl shadow-md md:shadow-lg">
                <h3 className="text-base md:text-lg font-semibold text-gray-900 mb-3 md:mb-4">
                  Furniture Catalog
                </h3>
                <div className="space-y-3 md:space-y-4 max-h-[300px] overflow-y-auto pr-2">
                  {furnitureCatalog.map((item) => (
                    <div
                      key={item.id}
                      className="border-b pb-2 last:border-b-0"
                    >
                      <p className="text-xs md:text-sm font-medium text-gray-900">
                        {item.name}
                      </p>
                      {item.subtypes ? (
                        item.subtypes.map((subtype) => (
                          <div
                            key={subtype.id}
                            className="p-2 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100 ml-2 md:ml-4 mt-1"
                            onClick={() => addFurniture(item, subtype)}
                          >
                            <p className="text-xs md:text-sm text-gray-900">
                              {subtype.name}
                            </p>
                            <p className="text-xxs md:text-xs text-gray-600">
                              {subtype.width}ft x {subtype.length}ft
                            </p>
                          </div>
                        ))
                      ) : (
                        <div
                          className="p-2 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100 mt-1"
                          onClick={() => addFurniture(item)}
                        >
                          <p className="text-xs md:text-sm text-gray-900">
                            {item.name}
                          </p>
                          <p className="text-xxs md:text-xs text-gray-600">
                            {item.width}ft x {item.length}ft
                          </p>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
                <div className="mt-4 space-y-2">
                  <button
                    onClick={() => setShowSaveModal(true)}
                    className="w-full px-3 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors text-sm"
                  >
                    Save Design
                  </button>
                  <button
                    onClick={() => setShowLoadModal(true)}
                    className="w-full px-3 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition-colors text-sm"
                  >
                    Load Design
                  </button>
                </div>
              </div>

              <div className="flex-1">
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-3 md:mb-4 gap-2">
                  <h3 className="text-base md:text-lg font-semibold text-gray-900">
                    2D Design Interface
                  </h3>
                  <div className="flex gap-2">
                    <div className="flex items-center gap-1 bg-gray-100 rounded-full p-1">
                      <button
                        onClick={undoDesign}
                        disabled={historyIndex <= 0}
                        className={`p-1 rounded-full transition-colors ${
                          historyIndex <= 0
                            ? "text-gray-400 cursor-not-allowed"
                            : "hover:bg-gray-200"
                        }`}
                        title="Undo"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M10 19l-7-7m0 0l7-7m-7 7h18"
                          />
                        </svg>
                      </button>
                      <button
                        onClick={redoDesign}
                        disabled={historyIndex >= designHistory.length - 1}
                        className={`p-1 rounded-full transition-colors ${
                          historyIndex >= designHistory.length - 1
                            ? "text-gray-400 cursor-not-allowed"
                            : "hover:bg-gray-200"
                        }`}
                        title="Redo"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M14 5l7 7m0 0l-7 7m7-7H3"
                          />
                        </svg>
                      </button>
                      <button
                        onClick={handleZoomOut}
                        className="p-1 rounded-full hover:bg-gray-200 transition-colors"
                        title="Zoom Out"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M20 12H4"
                          />
                        </svg>
                      </button>
                      <button
                        onClick={handleResetZoom}
                        className="text-xs px-2 py-1 hover:bg-gray-200 rounded-full transition-colors"
                      >
                        {Math.round(zoomLevel * 100)}%
                      </button>
                      <button
                        onClick={handleZoomIn}
                        className="p-1 rounded-full hover:bg-gray-200 transition-colors"
                        title="Zoom In"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M12 4v16m8-8H4"
                          />
                        </svg>
                      </button>
                    </div>
                    <button
                      onClick={goTo3DView}
                      className="px-3 md:px-4 py-1 md:py-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors text-xs md:text-sm"
                    >
                      View in 3D
                    </button>
                  </div>
                </div>
                <div className="bg-gray-100 rounded-xl md:rounded-2xl overflow-hidden relative">
                  <div
                    className="overflow-auto"
                    style={{
                      width: "100%",
                      height: "500px",
                      transform: `scale(${zoomLevel})`,
                      transformOrigin: "0 0",
                    }}
                  >
                    <Stage
                      ref={stageRef}
                      width={dimensions.width * roomScale}
                      height={dimensions.length * roomScale}
                      style={{ backgroundColor: wallColor }}
                      onClick={(e) => {
                        // Click on empty space - deselect all
                        if (e.target === e.currentTarget) {
                          setSelectedFurnitureId(null);
                        }
                      }}
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
                              id={item.id}
                              x={item.x * (roomScale / scale)}
                              y={item.y * (roomScale / scale)}
                              width={item.width}
                              height={item.length}
                              fill={item.color}
                              stroke="black"
                              strokeWidth={1}
                              draggable
                              onDragEnd={(e) => handleDragEnd(e, item.id)}
                              onTransformEnd={(e) =>
                                handleTransformEnd(e, item.id)
                              }
                              onClick={(e) => {
                                e.cancelBubble = true;
                                setSelectedFurnitureId(item.id);
                              }}
                              onTap={(e) => {
                                e.cancelBubble = true;
                                setSelectedFurnitureId(item.id);
                              }}
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
                        <Transformer
                          ref={transformerRef}
                          boundBoxFunc={(oldBox, newBox) => {
                            // Limit resize to keep within room bounds
                            if (
                              newBox.width < 20 ||
                              newBox.height < 20 ||
                              newBox.x < 0 ||
                              newBox.y < 0 ||
                              newBox.x + newBox.width >
                                dimensions.width * roomScale ||
                              newBox.y + newBox.height >
                                dimensions.length * roomScale
                            ) {
                              return oldBox;
                            }
                            return newBox;
                          }}
                        />
                      </Layer>
                    </Stage>
                  </div>
                </div>
                <div className="mt-3 md:mt-4">
                  <div className="flex justify-between items-center mb-2">
                    <h3 className="text-base md:text-lg font-semibold text-gray-900">
                      Furniture Properties
                    </h3>
                    {selectedFurnitureId && (
                      <button
                        onClick={() =>
                          handleDeleteFurniture(selectedFurnitureId)
                        }
                        className="text-xs px-2 py-1 bg-red-100 text-red-700 rounded-full hover:bg-red-200 transition-colors"
                      >
                        Delete Selected
                      </button>
                    )}
                  </div>
                  {furniture.length > 0 ? (
                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-2 md:gap-3">
                      {furniture.map((item) => (
                        <div
                          key={item.id}
                          className={`p-2 rounded-lg border ${
                            selectedFurnitureId === item.id
                              ? "border-indigo-500 bg-indigo-50"
                              : "border-gray-200"
                          }`}
                          onClick={() => setSelectedFurnitureId(item.id)}
                        >
                          <div className="flex items-center justify-between">
                            <span className="text-xs md:text-sm font-medium text-gray-900 truncate">
                              {item.name}
                            </span>
                            <div className="flex items-center gap-1">
                              <input
                                type="color"
                                value={item.color}
                                onChange={(e) =>
                                  handleColorChange(item.id, e.target.value)
                                }
                                className="p-0.5 w-6 h-6 border rounded-md cursor-pointer"
                                onClick={(e) => e.stopPropagation()}
                              />
                            </div>
                          </div>
                          <div className="text-xxs md:text-xs text-gray-600 mt-1">
                            Position: {Math.round(item.x * 10) / 10}ft,{" "}
                            {Math.round(item.y * 10) / 10}ft
                          </div>
                          <div className="text-xxs md:text-xs text-gray-600">
                            Size: {Math.round((item.width / scale) * 10) / 10}ft
                            × {Math.round((item.length / scale) * 10) / 10}ft
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-4 text-gray-500">
                      No furniture added yet. Select items from the catalog.
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default RoomTestDesignPage;
