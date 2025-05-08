import React, { useEffect, useRef, useState } from "react";
import { useLocation, Link } from "react-router-dom";
import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader.js";
import { DragControls } from "three/examples/jsm/controls/DragControls.js";

function ThreeDView() {
  const mountRef = useRef(null);
  const location = useLocation();
  const { room, furniture } = location.state || {
    room: {
      dimensions: { width: 20, length: 20 },
      wallColor: "#f5f5f5",
      floorColor: "#e0e0e0",
      floorType: "tile",
      floorTexture: "",
    },
    furniture: [],
  };

  const [furnitureColors, setFurnitureColors] = useState(() => {
    const initialColors = {};
    furniture.forEach((item) => {
      initialColors[item.id] = item.color;
    });
    return initialColors;
  });
  const [selectedFurnitureId, setSelectedFurnitureId] = useState(null);
  const [furnitureScales, setFurnitureScales] = useState(() => {
    const initialScales = {};
    furniture.forEach((item) => {
      initialScales[item.id] = 1;
    });
    return initialScales;
  });
  const [furnitureRotations, setFurnitureRotations] = useState(() => {
    const initialRotations = {};
    furniture.forEach((item) => {
      initialRotations[item.id] = 0;
    });
    return initialRotations;
  });

  const furnitureModelsRef = useRef({});
  const rendererRef = useRef(null);
  const sceneRef = useRef(null);
  const cameraRef = useRef(null);
  const controlsRef = useRef(null);
  const dragControlsRef = useRef(null);
  const isMountedRef = useRef(false);
  const raycasterRef = useRef(new THREE.Raycaster());
  const mouseRef = useRef(new THREE.Vector2());

  const FIXED_WALL_HEIGHT = 5;

  // Apply color to a model
  const applyColorToModel = (model, color) => {
    if (!model) return;
    model.traverse((child) => {
      if (child.isMesh && child.material) {
        child.material.color.set(color);
        child.material.needsUpdate = true;
      }
    });
  };

  // Update furniture colors when furniture prop changes
  useEffect(() => {
    setFurnitureColors((prev) => {
      const updatedColors = { ...prev };
      furniture.forEach((item) => {
        updatedColors[item.id] = item.color;
        const model = furnitureModelsRef.current[item.id];
        applyColorToModel(model, item.color);
      });
      return updatedColors;
    });
  }, [furniture]);

  // Handle scaling
  const handleScaleChange = (furnitureId, newScale) => {
    setFurnitureScales((prev) => ({
      ...prev,
      [furnitureId]: newScale,
    }));
    const model = furnitureModelsRef.current[furnitureId];
    if (model) {
      const item = furniture.find((f) => f.id === furnitureId);
      const baseScale = model.userData.baseScale || 1;
      model.scale.set(
        baseScale * newScale,
        baseScale * newScale,
        baseScale * newScale
      );

      const boundingBox = new THREE.Box3().setFromObject(model);
      const minY = boundingBox.min.y;
      model.position.y = -minY + ((item.models && item.models[0].yOffset) || 0);
    }
  };

  // Handle rotation
  const handleRotationChange = (furnitureId, newRotation) => {
    setFurnitureRotations((prev) => ({
      ...prev,
      [furnitureId]: newRotation,
    }));
    const model = furnitureModelsRef.current[furnitureId];
    if (model) {
      model.rotation.y = newRotation;
    }
  };

  // Handle mouse click for selection
  const handleMouseClick = (event) => {
    event.preventDefault();
    mouseRef.current.x = (event.clientX / window.innerWidth) * 2 - 1;
    mouseRef.current.y = -(event.clientY / window.innerHeight) * 2 + 1;

    raycasterRef.current.setFromCamera(mouseRef.current, cameraRef.current);
    const intersects = raycasterRef.current.intersectObjects(
      Object.values(furnitureModelsRef.current),
      true
    );

    if (intersects.length > 0) {
      let selectedModel = intersects[0].object;
      while (selectedModel && !selectedModel.userData.furnitureId) {
        selectedModel = selectedModel.parent;
      }
      if (selectedModel && selectedModel.userData.furnitureId) {
        setSelectedFurnitureId(selectedModel.userData.furnitureId);
      } else {
        setSelectedFurnitureId(null);
      }
    } else {
      setSelectedFurnitureId(null);
    }
  };

  useEffect(() => {
    if (!mountRef.current || isMountedRef.current) return;

    isMountedRef.current = true;

    // Initialize scene, camera, and renderer
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0xffffff);
    sceneRef.current = scene;

    const camera = new THREE.PerspectiveCamera(
      50,
      window.innerWidth / window.innerHeight,
      0.1,
      1000
    );
    cameraRef.current = camera;

    const renderer = new THREE.WebGLRenderer({
      antialias: true,
      powerPreference: "high-performance",
    });
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.2;
    rendererRef.current = renderer;

    // Clear existing canvases and append new one
    while (mountRef.current.firstChild) {
      mountRef.current.removeChild(mountRef.current.firstChild);
    }
    mountRef.current.appendChild(renderer.domElement);

    const roomWidth = room.dimensions.width;
    const roomLength = room.dimensions.length;
    const roomHeight = FIXED_WALL_HEIGHT;

    // Create room geometry
    const roomGroup = new THREE.Group();
    const textureLoader = new THREE.TextureLoader();

    // Floor with dynamic material
    const floorGeometry = new THREE.PlaneGeometry(roomWidth, roomLength);
    let floorMaterial;

    if (room.floorTexture) {
      textureLoader.load(
        room.floorTexture,
        (texture) => {
          texture.wrapS = texture.wrapT = THREE.RepeatWrapping;
          const repeatFactor =
            room.floorType === "tile" ? 4 : room.floorType === "carpet" ? 2 : 3;
          texture.repeat.set(
            roomWidth / repeatFactor,
            roomLength / repeatFactor
          );
          floorMaterial = new THREE.MeshStandardMaterial({
            map: texture,
            color: room.floorColor,
            roughness:
              room.floorType === "carpet"
                ? 0.9
                : room.floorType === "tile"
                ? 0.3
                : 0.5,
            metalness: room.floorType === "tile" ? 0.2 : 0,
            side: THREE.DoubleSide,
          });
          const floor = new THREE.Mesh(floorGeometry, floorMaterial);
          floor.rotation.x = -Math.PI / 2;
          floor.receiveShadow = true;
          roomGroup.add(floor);
        },
        undefined,
        (error) => {
          console.error(
            `Error loading floor texture ${room.floorTexture}:`,
            error
          );
          floorMaterial = new THREE.MeshStandardMaterial({
            color: room.floorColor,
            roughness:
              room.floorType === "carpet"
                ? 0.9
                : room.floorType === "tile"
                ? 0.3
                : 0.5,
            metalness: room.floorType === "tile" ? 0.2 : 0,
            side: THREE.DoubleSide,
          });
          const floor = new THREE.Mesh(floorGeometry, floorMaterial);
          floor.rotation.x = -Math.PI / 2;
          floor.receiveShadow = true;
          roomGroup.add(floor);
        }
      );
    } else {
      floorMaterial = new THREE.MeshStandardMaterial({
        color: room.floorColor,
        roughness:
          room.floorType === "carpet"
            ? 0.9
            : room.floorType === "tile"
            ? 0.3
            : 0.5,
        metalness: room.floorType === "tile" ? 0.2 : 0,
        side: THREE.DoubleSide,
      });
      const floor = new THREE.Mesh(floorGeometry, floorMaterial);
      floor.rotation.x = -Math.PI / 2;
      floor.receiveShadow = true;
      roomGroup.add(floor);
    }

    // Ceiling
    const ceilingMaterial = new THREE.MeshStandardMaterial({
      color: "#f8f8f8",
      roughness: 0.6,
      metalness: 0,
    });
    const ceiling = new THREE.Mesh(
      new THREE.PlaneGeometry(roomWidth, roomLength),
      ceilingMaterial
    );
    ceiling.position.set(0, roomHeight, 0);
    ceiling.rotation.x = Math.PI / 2;
    ceiling.receiveShadow = true;
    roomGroup.add(ceiling);

    // Walls
    const wallMaterial = new THREE.MeshStandardMaterial({
      color: room.wallColor,
      roughness: 0.5,
      metalness: 0,
      side: THREE.DoubleSide,
    });

    const frontWallGeometry = new THREE.PlaneGeometry(roomWidth, roomHeight);
    const frontWall = new THREE.Mesh(frontWallGeometry, wallMaterial);
    frontWall.position.set(0, roomHeight / 2, -roomLength / 2);
    frontWall.receiveShadow = true;
    roomGroup.add(frontWall);

    const backWallGeometry = new THREE.PlaneGeometry(roomWidth, roomHeight);
    const backWall = new THREE.Mesh(backWallGeometry, wallMaterial);
    backWall.position.set(0, roomHeight / 2, roomLength / 2);
    backWall.rotation.y = Math.PI;
    backWall.receiveShadow = true;
    roomGroup.add(backWall);

    const sideWallGeometry = new THREE.PlaneGeometry(roomLength, roomHeight);
    const leftWall = new THREE.Mesh(sideWallGeometry, wallMaterial);
    leftWall.position.set(-roomWidth / 2, roomHeight / 2, 0);
    leftWall.rotation.y = Math.PI / 2;
    leftWall.receiveShadow = true;
    roomGroup.add(leftWall);

    const rightWall = new THREE.Mesh(sideWallGeometry, wallMaterial);
    rightWall.position.set(roomWidth / 2, roomHeight / 2, 0);
    rightWall.rotation.y = -Math.PI / 2;
    rightWall.receiveShadow = true;
    roomGroup.add(rightWall);

    // Baseboard/trim
    const trimGeometry = new THREE.BoxGeometry(roomWidth + 0.2, 0.2, 0.1);
    const trimMaterial = new THREE.MeshStandardMaterial({
      color: "#333333",
      roughness: 0.4,
      metalness: 0.2,
    });

    const backTrim = new THREE.Mesh(trimGeometry, trimMaterial);
    backTrim.position.set(0, 0.1, roomLength / 2 - 0.05);
    roomGroup.add(backTrim);

    const sideTrimGeometry = new THREE.BoxGeometry(roomLength + 0.2, 0.2, 0.1);
    const leftTrim = new THREE.Mesh(sideTrimGeometry, trimMaterial);
    leftTrim.position.set(-roomWidth / 2 + 0.05, 0.1, 0);
    leftTrim.rotation.y = Math.PI / 2;
    roomGroup.add(leftTrim);

    const rightTrim = new THREE.Mesh(sideTrimGeometry, trimMaterial);
    rightTrim.position.set(roomWidth / 2 - 0.05, 0.1, 0);
    rightTrim.rotation.y = Math.PI / 2;
    roomGroup.add(rightTrim);

    scene.add(roomGroup);

    // Add lighting
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.3);
    scene.add(ambientLight);

    const ceilingLight = new THREE.RectAreaLight(
      0xffffff,
      5,
      roomWidth * 0.7,
      roomLength * 0.7
    );
    ceilingLight.position.set(0, roomHeight - 0.1, 0);
    ceilingLight.rotation.x = Math.PI / 2;
    scene.add(ceilingLight);

    const maxDimension = Math.max(roomWidth, roomLength);
    const sunLight = new THREE.DirectionalLight(0xfff4e6, 1.5);
    sunLight.position.set(0, FIXED_WALL_HEIGHT * 1.5, maxDimension * 1.5);
    sunLight.castShadow = true;
    sunLight.shadow.mapSize.width = 2048;
    sunLight.shadow.mapSize.height = 2048;
    sunLight.shadow.camera.near = 0.5;
    sunLight.shadow.camera.far = maxDimension * 3;
    sunLight.shadow.camera.left = -maxDimension * 0.5;
    sunLight.shadow.camera.right = maxDimension * 0.5;
    sunLight.shadow.camera.top = FIXED_WALL_HEIGHT;
    sunLight.shadow.camera.bottom = -FIXED_WALL_HEIGHT;
    sunLight.shadow.bias = -0.001;
    scene.add(sunLight);

    const fillLight1 = new THREE.DirectionalLight(0xffffff, 0.4);
    fillLight1.position.set(-roomWidth, FIXED_WALL_HEIGHT / 2, 0);
    scene.add(fillLight1);

    const fillLight2 = new THREE.DirectionalLight(0xffffff, 0.4);
    fillLight2.position.set(roomWidth, FIXED_WALL_HEIGHT / 2, 0);
    scene.add(fillLight2);

    // Load furniture models
    const gltfLoader = new GLTFLoader();
    const draggableObjects = [];

    furniture.forEach((item) => {
      gltfLoader.load(
        item.modelPath,
        (gltf) => {
          const model = gltf.scene;
          const boundingBox = new THREE.Box3().setFromObject(model);
          const modelSize = boundingBox.getSize(new THREE.Vector3());
          const baseScale = Math.min(
            item.width / (modelSize.x || 1),
            item.length / (modelSize.z || 1),
            FIXED_WALL_HEIGHT / (modelSize.y || 1)
          );
          model.scale.set(baseScale, baseScale, baseScale);
          model.userData = { furnitureId: item.id, baseScale };

          const scaledBoundingBox = new THREE.Box3().setFromObject(model);
          const minY = scaledBoundingBox.min.y;
          model.position.set(
            item.x - roomWidth / 2 + item.width / 2,
            -minY + ((item.models && item.models[0].yOffset) || 0),
            item.y - roomLength / 2 + item.length / 2
          );

          model.traverse((child) => {
            if (child.isMesh) {
              child.castShadow = true;
              child.receiveShadow = true;
              if (child.material) {
                child.material.color.set(
                  furnitureColors[item.id] || item.color
                );
                child.material.roughness = 0.3;
                child.material.metalness = 0.2;
                child.material.needsUpdate = true;
              }
            }
          });

          scene.add(model);
          furnitureModelsRef.current[item.id] = model;
          draggableObjects.push(model);

          // Apply initial scale and rotation
          const scale = furnitureScales[item.id] || 1;
          model.scale.set(
            baseScale * scale,
            baseScale * scale,
            baseScale * scale
          );
          model.rotation.y = furnitureRotations[item.id] || 0;
        },
        undefined,
        (error) => {
          console.error(`Error loading model ${item.modelPath}:`, error);
          const geometry = new THREE.BoxGeometry(item.width, 2, item.length);
          const material = new THREE.MeshStandardMaterial({
            color: furnitureColors[item.id] || item.color,
            roughness: 0.3,
            metalness: 0.2,
          });
          const cube = new THREE.Mesh(geometry, material);
          cube.position.set(
            item.x - roomWidth / 2 + item.width / 2,
            1,
            item.y - roomLength / 2 + item.length / 2
          );
          cube.userData = { furnitureId: item.id, baseScale: 1 };
          cube.castShadow = true;
          cube.receiveShadow = true;
          scene.add(cube);
          furnitureModelsRef.current[item.id] = cube;
          draggableObjects.push(cube);

          // Apply initial scale and rotation
          const scale = furnitureScales[item.id] || 1;
          cube.scale.set(scale, scale, scale);
          cube.rotation.y = furnitureRotations[item.id] || 0;
        }
      );
    });

    // Camera and controls
    const zDistance = FIXED_WALL_HEIGHT * 3 + maxDimension * 0.5;
    camera.position.set(0, FIXED_WALL_HEIGHT * 0.7, zDistance);
    camera.lookAt(0, FIXED_WALL_HEIGHT * 0.3, 0);

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.target.set(0, FIXED_WALL_HEIGHT * 0.3, 0);
    controls.minDistance = FIXED_WALL_HEIGHT * 1;
    controls.maxDistance = FIXED_WALL_HEIGHT * 10;
    controls.maxPolarAngle = Math.PI * 0.9;
    controls.enablePan = true;
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;
    controls.update();
    controlsRef.current = controls;

    // Drag controls
    const dragControls = new DragControls(
      draggableObjects,
      camera,
      renderer.domElement
    );
    dragControlsRef.current = dragControls;

    dragControls.addEventListener("dragstart", () => {
      controls.enabled = false;
    });

    dragControls.addEventListener("dragend", () => {
      controls.enabled = true;
    });

    dragControls.addEventListener("drag", (event) => {
      const model = event.object;
      const item = furniture.find((f) => f.id === model.userData.furnitureId);
      if (!item) return;

      // Constrain position within room boundaries
      const boundingBox = new THREE.Box3().setFromObject(model);
      const size = boundingBox.getSize(new THREE.Vector3());
      const halfWidth = (item.width * (furnitureScales[item.id] || 1)) / 2;
      const halfLength = (item.length * (furnitureScales[item.id] || 1)) / 2;

      model.position.x = Math.max(
        -roomWidth / 2 + halfWidth,
        Math.min(roomWidth / 2 - halfWidth, model.position.x)
      );
      model.position.z = Math.max(
        -roomLength / 2 + halfLength,
        Math.min(roomLength / 2 - halfLength, model.position.z)
      );

      // Ensure model stays on the floor
      const minY = boundingBox.min.y;
      model.position.y = -minY + ((item.models && item.models[0].yOffset) || 0);
    });

    // Add click listener for selection
    renderer.domElement.addEventListener("click", handleMouseClick);

    // Animation loop
    let animationFrameId;
    const animate = () => {
      animationFrameId = requestAnimationFrame(animate);
      controls.update();
      renderer.render(scene, camera);
    };
    animate();

    // Handle window resize
    const handleResize = () => {
      camera.aspect = window.innerWidth / window.innerHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth, window.innerHeight);
    };
    window.addEventListener("resize", handleResize);

    // Cleanup
    return () => {
      isMountedRef.current = false;

      if (animationFrameId) {
        cancelAnimationFrame(animationFrameId);
      }

      window.removeEventListener("resize", handleResize);
      renderer.domElement.removeEventListener("click", handleMouseClick);

      if (mountRef.current) {
        while (mountRef.current.firstChild) {
          mountRef.current.removeChild(mountRef.current.firstChild);
        }
      }

      if (rendererRef.current) {
        rendererRef.current.dispose();
        rendererRef.current.forceContextLoss();
        rendererRef.current = null;
      }

      if (sceneRef.current) {
        sceneRef.current.traverse((object) => {
          if (object.geometry) object.geometry.dispose();
          if (object.material) {
            if (Array.isArray(object.material)) {
              object.material.forEach((mat) => mat.dispose());
            } else {
              object.material.dispose();
            }
          }
        });
        sceneRef.current.clear();
        sceneRef.current = null;
      }

      cameraRef.current = null;
      controlsRef.current = null;
      dragControlsRef.current = null;
      furnitureModelsRef.current = {};
    };
  }, [
    room.dimensions.width,
    room.dimensions.length,
    room.wallColor,
    room.floorColor,
    room.floorType,
    room.floorTexture,
  ]);

  const handleColorChange = (furnitureId, newColor) => {
    setFurnitureColors((prev) => ({
      ...prev,
      [furnitureId]: newColor,
    }));

    const model = furnitureModelsRef.current[furnitureId];
    applyColorToModel(model, newColor);
  };

  return (
    <div className="min-h-screen bg-neutral-50 font-sans">
      <header className="flex justify-between items-center p-8">
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
        <Link
          to="/design"
          className="px-4 py-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-200"
        >
          Back to 2D Design
        </Link>
      </header>
      <div className="flex">
        <div ref={mountRef} className="w-3/4 h-[calc(100vh-80px)]" />
        <div className="w-1/4 bg-white p-6 rounded-l-2xl shadow-xl h-[calc(100vh-80px)] overflow-y-auto">
          <h3 className="text-xl font-semibold text-gray-900 mb-6 border-b pb-2">
            Furniture Editor
          </h3>
          {furniture.length === 0 ? (
            <p className="text-sm text-gray-600">No furniture added.</p>
          ) : (
            <div className="space-y-6">
              {furniture.map((item) => (
                <div
                  key={item.id}
                  className={`p-3 rounded-lg border transition-colors duration-150 ${
                    selectedFurnitureId === item.id
                      ? "border-indigo-500 bg-indigo-50"
                      : "border-gray-100 hover:bg-gray-50"
                  }`}
                >
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-medium text-gray-900">
                      {item.name}
                    </span>
                    <div className="flex items-center">
                      <div
                        className="w-6 h-6 rounded-md mr-2 border border-gray-300"
                        style={{
                          backgroundColor:
                            furnitureColors[item.id] || item.color,
                        }}
                      />
                      <input
                        type="color"
                        value={furnitureColors[item.id] || item.color}
                        onChange={(e) =>
                          handleColorChange(item.id, e.target.value)
                        }
                        className="p-1 w-8 h-8 border rounded-md cursor-pointer"
                      />
                    </div>
                  </div>
                  <div className="text-xs text-gray-500 mb-2">
                    {item.width}m × {item.length}m
                  </div>
                  {selectedFurnitureId === item.id && (
                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Scale ({furnitureScales[item.id]?.toFixed(2) || 1})
                        </label>
                        <input
                          type="range"
                          min="0.5"
                          max="2"
                          step="0.1"
                          value={furnitureScales[item.id] || 1}
                          onChange={(e) =>
                            handleScaleChange(
                              item.id,
                              parseFloat(e.target.value)
                            )
                          }
                          className="w-full h-2 bg-gray-200 rounded-lg cursor-pointer"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Rotation (
                          {(
                            (furnitureRotations[item.id] || 0) *
                            (180 / Math.PI)
                          ).toFixed(0)}
                          °)
                        </label>
                        <input
                          type="range"
                          min="0"
                          max={2 * Math.PI}
                          step={Math.PI / 180}
                          value={furnitureRotations[item.id] || 0}
                          onChange={(e) =>
                            handleRotationChange(
                              item.id,
                              parseFloat(e.target.value)
                            )
                          }
                          className="w-full h-2 bg-gray-200 rounded-lg cursor-pointer"
                        />
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ThreeDView;
