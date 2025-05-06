import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";

import Login from "./pages/components/Auth/Login";
import Profile from "./pages/components/Auth/Profile";
import Signup from "./pages/components/Auth/Signup";
import ProtectedRoute from "./pages/components/ProtectedRoute";
import RoomDesignPage from "./pages/DesignPage";
import RoomTestDesignPage from "./pages/TestDesign";
import TestThreeDView from "./pages/TestThreeDView";
import ThreeDView from "./pages/ThreeDView";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/3d-view" element={<ThreeDView />} />
        <Route path="/testDesign" element={<RoomTestDesignPage />} />
        <Route path="/test3dView" element={<TestThreeDView />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/design"
          element={
            <ProtectedRoute>
              <RoomDesignPage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
