package org.furnish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.furnish.core.Design;
import org.furnish.core.Furniture;
import org.furnish.utils.CloseButtonUtil;

public class FurnitureDesignApp extends JFrame {
    private VisualizationPanel visualizationPanel;
    PropertiesPanel propertiesPanel;
    private Design currentDesign;
    private Furniture selectedFurniture;
    private JLabel statusLabel;
    private JToggleButton view2D3DToggle;

    public FurnitureDesignApp() {
        initializeModernUI();
        setupModernMenuBar();
        setupToolbar();
        setupPanels();
        setupStatusBar();
    }

    private void initializeModernUI() {
        setTitle("Furnish Studio - Designer");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1200, 850, 30, 30));

        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(23, 23, 38),
                        getWidth(), getHeight(), new Color(42, 42, 74));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(new Color(255, 255, 255, 15));
                for (int i = 0; i < 5; i++) {
                    g2d.fillOval(150 + i * 180, 50, 100, 100);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Furnish Studio");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);

        JButton minimizeButton = new JButton("—");
        styleControlButton(minimizeButton);
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));
        controlPanel.add(minimizeButton);

        JButton closeButton = CloseButtonUtil.createCloseButton();
        styleControlButton(closeButton);
        controlPanel.add(closeButton);

        topPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        return mainPanel;
    }

    private void styleControlButton(JButton button) {
        button.setFont(new Font("Montserrat", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 90));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 80, 110));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 60, 90));
            }
        });
    }

    private void setupModernMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 30, 45));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JMenu fileMenu = createStyledMenu("File");
        addMenuItems(fileMenu,
                createStyledMenuItem("New Design", "../images/close.png", e -> newDesign()),
                createStyledMenuItem("Open Design", "../images/close.png", e -> openDesign()),
                createStyledMenuItem("Save Design", "../images/close.png", e -> saveDesign()),
                new JSeparator(),
                createStyledMenuItem("Exit", "../images/close.png", e -> dispose()));

        JMenu editMenu = createStyledMenu("Edit");
        addMenuItems(editMenu,
                createStyledMenuItem("Undo", "../images/close.png", e -> System.out.println("Undo")),
                createStyledMenuItem("Redo", "../images/close.png", e -> System.out.println("Redo")),
                new JSeparator(),
                createStyledMenuItem("Delete Selected", "../images/close.png", e -> deleteSelectedFurniture()));

        JMenu viewMenu = createStyledMenu("View");
        addMenuItems(viewMenu,
                createStyledMenuItem("2D/3D Toggle", "../images/close.png", e -> toggle2D3DView()));

        JMenu furnitureMenu = createStyledMenu("Furniture");
        addMenuItems(furnitureMenu,
                createStyledMenuItem("Add Chair", "../images/close.png", e -> addFurniture("Chair")),
                createStyledMenuItem("Add Table", "../images/close.png", e -> addFurniture("Table")),
                createStyledMenuItem("Add Sofa", "../images/close.png", e -> addFurniture("Sofa")),
                createStyledMenuItem("Add Cabinet", "../images/close.png", e -> addFurniture("Cabinet")));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(furnitureMenu);
        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Montserrat", Font.PLAIN, 14));
        menu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return menu;
    }

    private JMenuItem createStyledMenuItem(String text, String iconPath, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setForeground(Color.WHITE);
        item.setBackground(new Color(60, 60, 90));
        item.setFont(new Font("Montserrat", Font.PLAIN, 13));
        item.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        item.addActionListener(action);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            if (icon.getImage() != null) {
                item.setIcon(icon);
            }
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                item.setBackground(new Color(80, 80, 110));
            }

            public void mouseExited(MouseEvent evt) {
                item.setBackground(new Color(60, 60, 90));
            }
        });

        return item;
    }

    private void setupToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(40, 40, 60));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton newButton = createToolbarButton("New", "../images/close.png");
        newButton.addActionListener(e -> newDesign());
        toolBar.add(newButton);

        JButton openButton = createToolbarButton("Open", "../images/close.png");
        openButton.addActionListener(e -> openDesign());
        toolBar.add(openButton);

        JButton saveButton = createToolbarButton("Save", "../images/close.png");
        saveButton.addActionListener(e -> saveDesign());
        toolBar.add(saveButton);

        toolBar.addSeparator();

        JButton chairButton = createToolbarButton("Chair", "../images/close.png");
        chairButton.addActionListener(e -> addFurniture("Chair"));
        toolBar.add(chairButton);

        JButton tableButton = createToolbarButton("Table", "../images/close.png");
        tableButton.addActionListener(e -> addFurniture("Table"));
        toolBar.add(tableButton);

        JButton sofaButton = createToolbarButton("Sofa", "../images/close.png");
        sofaButton.addActionListener(e -> addFurniture("Sofa"));
        toolBar.add(sofaButton);

        toolBar.addSeparator();

        view2D3DToggle = new JToggleButton("3D View", new ImageIcon(getClass().getResource("../images/close.png")));
        styleToolbarButton(view2D3DToggle);
        view2D3DToggle.addActionListener(e -> {
            boolean is3D = view2D3DToggle.isSelected();
            visualizationPanel.set3DView(is3D);
            view2D3DToggle.setText(is3D ? "3D View" : "2D View");
            view2D3DToggle.setIcon(new ImageIcon(getClass().getResource(
                    is3D ? "images/3d.png" : "images/2d.png")));
        });
        toolBar.add(view2D3DToggle);

        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    private JButton createToolbarButton(String tooltip, String iconPath) {
        JButton button = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            button.setIcon(icon);
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }
        button.setToolTipText(tooltip);
        styleToolbarButton(button);
        return button;
    }

    private void styleToolbarButton(AbstractButton button) {
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setBackground(new Color(60, 60, 90));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Montserrat", Font.PLAIN, 12));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 80, 110));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 60, 90));
            }
        });
    }

    private void setupPanels() {
        visualizationPanel = new VisualizationPanel(this);
        visualizationPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));
        visualizationPanel.setBackground(new Color(50, 50, 70));

        propertiesPanel = new PropertiesPanel();
        propertiesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 15),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(80, 80, 110)),
                        "Properties",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Montserrat", Font.BOLD, 14),
                        new Color(200, 200, 200))));
        propertiesPanel.setBackground(new Color(40, 40, 60));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(visualizationPanel),
                propertiesPanel);
        splitPane.setDividerLocation(800);
        splitPane.setDividerSize(3);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBackground(new Color(40, 40, 60));

        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private void setupStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80, 80, 110)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        statusPanel.setBackground(new Color(40, 40, 60));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(180, 180, 180));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JLabel versionLabel = new JLabel("Furnish Studio v1.0");
        versionLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(180, 180, 180));
        statusPanel.add(versionLabel, BorderLayout.EAST);

        getContentPane().add(statusPanel, BorderLayout.SOUTH);
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void newDesign() {
        RoomDialog dialog = new RoomDialog(this);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            currentDesign = new Design(dialog.getRoom());
            visualizationPanel.setDesign(currentDesign);
            updateStatus("New design created with room dimensions: " + dialog.getRoom());
            repaint();
        }
    }

    private void saveDesign() {
        if (currentDesign == null) {
            showErrorDialog("No design to save. Please create a new design first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Design");
        fileChooser.setSelectedFile(new File("design.furnish"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".furnish")) {
                file = new File(file.getParentFile(), file.getName() + ".furnish");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file))) {
                oos.writeObject(currentDesign);
                updateStatus("Design saved to: " + file.getAbsolutePath());
            } catch (IOException ex) {
                showErrorDialog("Error saving design: " + ex.getMessage());
            }
        }
    }

    private void openDesign() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Design");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Furnish Design Files (*.furnish)", "furnish"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(fileChooser.getSelectedFile()))) {
                currentDesign = (Design) ois.readObject();
                visualizationPanel.setDesign(currentDesign);
                updateStatus("Design loaded from: " + fileChooser.getSelectedFile().getAbsolutePath());
                repaint();
            } catch (IOException | ClassNotFoundException ex) {
                showErrorDialog("Error loading design: " + ex.getMessage());
            }
        }
    }

    private void addFurniture(String type) {
        if (currentDesign == null) {
            showErrorDialog("Please create a room design first before adding furniture.");
            return;
        }

        Color defaultColor = type.equals("Chair") ? new Color(180, 120, 70)
                : type.equals("Table") ? new Color(150, 100, 50)
                        : type.equals("Sofa") ? new Color(120, 80, 180) : new Color(100, 150, 100);

        Furniture f = new Furniture(
                type,
                1.0, 0, 1.0,
                type.equals("Chair") ? 1.0 : type.equals("Table") ? 2.0 : type.equals("Sofa") ? 1.8 : 1.5,
                type.equals("Chair") ? 1.0 : type.equals("Table") ? 0.8 : type.equals("Sofa") ? 0.7 : 1.2,
                defaultColor);

        currentDesign.addFurniture(f);
        updateStatus("Added " + type + " to the design");
        repaint();
    }

    private void deleteSelectedFurniture() {
        if (currentDesign == null) {
            showErrorDialog("No design loaded to delete from.");
            return;
        }

        if (selectedFurniture == null) {
            showErrorDialog("No furniture selected to delete.");
            return;
        }

        String type = selectedFurniture.getType();
        currentDesign.getFurnitureList().remove(selectedFurniture);
        selectedFurniture = null;
        propertiesPanel.update((Graphics) null);
        updateStatus("Deleted " + type + " from the design");
        repaint();
    }

    public void setSelectedFurniture(Furniture f) {
        selectedFurniture = f;
        propertiesPanel.update(f);
        updateStatus("Selected " + f.getType() + " - Click and drag to move, use properties panel to edit");
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
        updateStatus("Error: " + message);
    }

    private void addMenuItems(JMenu menu, Component... items) {
        for (Component item : items) {
            if (item instanceof JSeparator) {
                menu.addSeparator();
            } else if (item instanceof JMenuItem) {
                ((JMenuItem) item).setFont(new Font("Montserrat", Font.PLAIN, 13));
                menu.add(item);
            }
        }
    }

    private void toggle2D3DView() {
        boolean is3D = view2D3DToggle.isSelected();
        view2D3DToggle.setSelected(!is3D);
        visualizationPanel.set3DView(!is3D);
        view2D3DToggle.setText(!is3D ? "3D View" : "2D View");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception ex) {
            System.err.println("Failed to set modern look and feel");
        }

        SwingUtilities.invokeLater(() -> {
            FurnitureDesignApp app = new FurnitureDesignApp();
            app.setOpacity(0f);
            app.setVisible(true);

            Timer timer = new Timer(10, new ActionListener() {
                float opacity = 0f;

                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity += 0.05f;
                    if (opacity >= 1f) {
                        ((Timer) e.getSource()).stop();
                    }
                    app.setOpacity(opacity);
                }
            });
            timer.start();
        });
    }
}