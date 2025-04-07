package org.furnish.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DesignManager {
    private static final String DESIGNS_DIR = "saved_designs";
    private static final String DESIGNS_FILE = "designs.dat";

    public static void saveDesign(Design design) {
        try {
            // Create designs directory if it doesn't exist
            File dir = new File(DESIGNS_DIR);
            if (!dir.exists()) {
                dir.mkdir();
            }

            // Save design to file
            String filename = DESIGNS_DIR + "/" + design.getName() + ".dat";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(design);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Design> loadAllDesigns() {
        List<Design> designs = new ArrayList<>();
        File dir = new File(DESIGNS_DIR);
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        Design design = (Design) ois.readObject();
                        designs.add(design);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return designs;
    }

    public static void deleteDesign(String designName) {
        File file = new File(DESIGNS_DIR + "/" + designName + ".dat");
        if (file.exists()) {
            file.delete();
        }
    }
} 