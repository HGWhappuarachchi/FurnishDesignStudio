import React, { createContext, useContext, useState, useEffect } from "react";
import { useAuth } from "./AuthContext";

const DesignContext = createContext();

export function DesignProvider({ children }) {
  const [designs, setDesigns] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const { currentUser } = useAuth();

  const fetchDesigns = async () => {
    if (!currentUser) return;

    setLoading(true);
    setError(null);
    try {
      const idToken = await currentUser.getIdToken();
      const response = await fetch("/api/designs", {
        headers: {
          Authorization: `Bearer ${idToken}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch designs");
      }

      const data = await response.json();
      setDesigns(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const saveDesign = async (designData) => {
    if (!currentUser) return null;

    setLoading(true);
    setError(null);
    try {
      const idToken = await currentUser.getIdToken();
      const response = await fetch("/api/designs", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${idToken}`,
        },
        body: JSON.stringify(designData),
      });

      if (!response.ok) {
        throw new Error("Failed to save design");
      }

      const data = await response.json();
      await fetchDesigns();
      return data.id;
    } catch (err) {
      setError(err.message);
      return null;
    } finally {
      setLoading(false);
    }
  };

  const updateDesign = async (designId, designData) => {
    if (!currentUser) return false;

    setLoading(true);
    setError(null);
    try {
      const idToken = await currentUser.getIdToken();
      const response = await fetch(`/api/designs/${designId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${idToken}`,
        },
        body: JSON.stringify(designData),
      });

      if (!response.ok) {
        throw new Error("Failed to update design");
      }

      await fetchDesigns();
      return true;
    } catch (err) {
      setError(err.message);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const deleteDesign = async (designId) => {
    if (!currentUser) return false;

    setLoading(true);
    setError(null);
    try {
      const idToken = await currentUser.getIdToken();
      const response = await fetch(`/api/designs/${designId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${idToken}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to delete design");
      }

      setDesigns(designs.filter((design) => design.id !== designId));
      return true;
    } catch (err) {
      setError(err.message);
      return false;
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (currentUser) {
      fetchDesigns();
    } else {
      setDesigns([]);
    }
  }, [currentUser]);

  return (
    <DesignContext.Provider
      value={{
        designs,
        loading,
        error,
        saveDesign,
        updateDesign,
        deleteDesign,
        fetchDesigns,
      }}
    >
      {children}
    </DesignContext.Provider>
  );
}

export function useDesigns() {
  return useContext(DesignContext);
}
