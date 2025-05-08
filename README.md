# Furnish Design Studio

Furnish Design Studio is a web application built with React that allows users to design and visualize interior spaces. Users can define room dimensions, select furniture from a catalog, arrange items in a 2D canvas, and view their creations in an interactive 3D environment.

## Features

* **Room Setup**: Define custom room dimensions (width, length, height).
* **Customization**: Choose wall colors, floor colors, and floor types (tile, carpet, textured material) with various texture options.
* **Room Templates**: Start quickly with predefined templates for Living Room, Bedroom, Home Office, and Dining Room, complete with default furniture and suggested styles.
* **2D Design Interface**:
    * Drag-and-drop furniture items from a catalog onto a 2D canvas representation of the room.
    * Position and arrange furniture items easily.
    * Customize the color of individual furniture pieces.
* **3D Visualization**:
    * View the 2D design in an interactive 3D space.
    * Orbit controls allow viewing the room from different angles.
    * Drag furniture items directly within the 3D view.
    * Adjust the scale and rotation of furniture in the 3D view.
* **Furniture Catalog**: Includes various furniture items like chairs (dining, arm, stool, computer), tables (dining, coffee), beds (single, double), sofas, dressers, nightstands, and bookshelves.

## Technologies Used

* **Frontend Framework**: React
* **Routing**: React Router
* **Styling**: Tailwind CSS, configured using Craco and PostCSS.
* **2D Canvas**: Konva / React Konva
* **3D Rendering**: Three.js via @react-three/fiber and @react-three/drei
* **State Management**: React Hooks (useState, useEffect, useRef, etc.)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Node.js (>=14.0.0 recommended based on `react-scripts` requirement)
* npm

### Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/HGWhappuarachchi/FurnishDesignStudio.git
    cd furnish-design-studio
    ```
2.  Install NPM packages:
    ```bash
    npm install
    ```

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in development mode using Craco.
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes. Lint errors may also appear in the console.

### `npm test`

Launches the test runner in interactive watch mode using Craco.

### `npm run build`

Builds the app for production to the `build` folder using Craco.
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes. Your app is ready to be deployed!

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can't go back!**

This command will remove the single build dependency (`react-scripts`) but keeps Craco configuration. It copies configuration files and transitive dependencies (webpack, Babel, ESLint, etc.) into your project for full control.

## Project Structure

* `public/`: Contains static assets and the main `index.html` file.
* `src/`: Contains the main React application code.
    * `components/`: (Likely location for reusable UI components - *inferred*)
    * `pages/`: Contains page-level components corresponding to different views (HomePage, RoomSetup, DesignPage, ThreeDView).
    * `assets/`: Contains static assets like images, 3D models, textures.
    * `App.jsx`: Main application component setting up routing.
    * `index.js`: Entry point of the React application[cite: 10].
    * `index.css`: Base Tailwind CSS setup[cite: 9].
* `craco.config.js`: Configuration for Craco to override Create React App settings (used for Tailwind)[cite: 1].
* `tailwind.config.js`: Configuration file for Tailwind CSS[cite: 6].
* `postcss.config.js`: Configuration file for PostCSS (used with Tailwind)[cite: 4].
* `package.json`: Project metadata and dependencies[cite: 3].
