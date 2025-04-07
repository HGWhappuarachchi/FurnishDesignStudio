Below is a `README.md` file written in Markdown format for your `FurnitureDesignApp` project. It provides an overview of the application, instructions for setup and usage, and details about its features and structure.

---

# Furniture Design App

![Java](https://img.shields.io/badge/Java-17-orange)

![Java](https://img.shields.io/badge/Java-22-orange)
![Swing](https://img.shields.io/badge/UI-Swing-blue)

The **Furniture Design App** is a Java-based desktop application built using the Swing framework. It allows users to create virtual room designs, add furniture (chairs and tables), and visualize them in both 2D and 3D views. Users can customize furniture properties such as position, size, color, and shading, and save or load their designs.

## Features

- **Room Creation**: Define room dimensions and colors for the floor and walls.
- **Furniture Placement**: Add chairs and tables with customizable properties.
- **2D View**: A top-down view of the room and furniture layout.
- **3D View**: An isometric 3D view with rotation capabilities using mouse dragging.
- **Furniture Manipulation**: Drag furniture in 2D mode and adjust properties via a panel.
- **Save/Load Designs**: Serialize and deserialize room designs to/from files.
- **Properties Panel**: Edit furniture position (X, Z), size (width, depth, height), color, and shade.

## Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher recommended.
- **IDE**: Any Java-compatible IDE (e.g., IntelliJ IDEA, Eclipse) or a simple text editor with a command-line compiler.

## Installation

1. **Clone or Download the Project**:
   - Clone this repository or download the source code as a ZIP file.
   - Extract the files to a directory of your choice.

2. **Open in an IDE**:
   - Import the project into your preferred IDE as a Java project.
   - Ensure the package structure (`org.furnish`) is maintained.

3. **Compile and Run**:
   - Compile the project using your IDE’s build tools.
   - Run the `FurnitureDesignApp` class (contains the `main` method).

   Alternatively, from the command line:
   ```bash
   javac org/Ghapurachchi/FurnitureDesignApp.java
   java org.furnish.FurnitureDesignApp
   ```

## Usage

1. **Starting the App**:
   - Launch the application to see the main window with a menu bar.

2. **Creating a New Design**:
   - Go to `File > New` to open the "New Room" dialog.
   - Enter room dimensions (length, width, height) and choose colors for the floor and walls.
   - Click "OK" to create the room.

3. **Adding Furniture**:
   - Use `Edit > Add Chair` or `Edit > Add Table` to place furniture in the room.
   - Furniture appears at a default position (X: 1.0, Z: 1.0).

4. **Switching Views**:
   - Select `View > 2D View` for a top-down perspective.
   - Select `View > 3D View` for an isometric 3D view.

5. **Interacting with Furniture**:
   - In 2D mode: Click and drag furniture to reposition it within the room boundaries.
   - In 3D mode: Click and drag empty space to rotate the view; furniture dragging is disabled in this mode.
   - Select furniture by clicking it to edit its properties in the right panel.

6. **Editing Properties**:
   - Use the "Properties" panel to adjust:
     - **X, Z**: Position in the room.
     - **Width, Depth, Height**: Size of the furniture.
     - **Color**: Choose a new color using the color chooser.
     - **Shade**: Adjust brightness with the slider (0% dark, 100% full brightness).

7. **Saving and Loading**:
   - Save your design via `File > Save` and choose a file location.
   - Load a saved design via `File > Open`.

8. **Deleting Furniture**:
   - Select a piece of furniture and click `Edit > Delete Selected` to remove it.

## Project Structure

```
src/
└── org/
    └── Ghapurachchi/
        ├── FurnitureDesignApp.java  # Main application frame and entry point
        ├── Room.java               # Represents a room with dimensions and colors
        ├── Furniture.java          # Represents furniture with properties
        ├── Design.java             # Holds room and furniture data
        ├── VisualizationPanel.java # Handles 2D/3D rendering and interactions
        ├── PropertiesPanel.java    # UI for editing furniture properties
        └── RoomDialog.java         # Dialog for creating a new room
```

## Limitations

- **3D Rendering**: Uses a simple isometric projection without depth sorting, which may cause visual overlap issues.
- **Furniture Dragging in 3D**: Not implemented; dragging is limited to 2D mode.
- **Basic Graphics**: Relies on Swing’s `Graphics2D` for rendering, lacking advanced 3D capabilities.

## Future Improvements

- Implement depth sorting (e.g., painter’s algorithm or Z-buffer) for accurate 3D rendering.
- Add furniture dragging in 3D mode with proper perspective adjustments.
- Integrate a camera system for zooming and panning in 3D view.
- Enhance furniture types with more detailed models (e.g., sofas, shelves).
- Use a 3D library like Java3D or JOGL for improved graphics performance.

## Contributing

Feel free to fork this repository, make improvements, and submit pull requests. Suggestions and bug reports are welcome via issues.

## License

This project is open-source and available under the [MIT License](LICENSE).

---

You can copy this text into a `README.md` file in your project directory. If you need any modifications or additional sections (e.g., screenshots, specific build instructions), let me know!
