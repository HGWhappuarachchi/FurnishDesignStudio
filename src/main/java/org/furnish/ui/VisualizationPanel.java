package org.furnish.ui;

import org.furnish.core.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.*;

public class VisualizationPanel extends GLJPanel implements GLEventListener {
    private Design design;
    private FurnitureDesignApp parent;
    private Furniture draggedFurniture;
    private Point dragStartPoint;
    private float rotationX = 0;
    private float rotationY = 0;
    private double lastMouseX, lastMouseY;
    private float zoomFactor = 1.0f;
    private boolean is3DView = false;
    private FPSAnimator animator;

    public VisualizationPanel(FurnitureDesignApp parent) {
        super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
        this.parent = parent;
        initializePanel();
        setupListeners();

        // Set up animator for continuous rendering
        animator = new FPSAnimator(this, 60);
        animator.start();
    }

    private void initializePanel() {
        setPreferredSize(new Dimension(1000, 900));
        addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (animator != null) {
            animator.stop();
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -10.0f * zoomFactor);
        gl.glRotatef(rotationX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotationY, 0.0f, 1.0f, 0.0f);

        if (design != null) {
            if (is3DView) {
                draw3D(gl);
            } else {
                draw2D(gl);
            }
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        float aspect = (float) width / height;
        gl.glFrustum(-aspect, aspect, -1.0, 1.0, 5.0, 60.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void draw2D(GL2 gl) {
        Room room = design.getRoom();
        gl.glDisable(GL2.GL_DEPTH_TEST);

        // Draw floor
        setColor(gl, room.getFloorColor());
        drawRect(gl, 0f, 0f, 0f,
                (float) room.getLength(),
                (float) room.getWidth(),
                0f);

        // Draw furniture
        for (Furniture f : design.getFurnitureList()) {
            setColor(gl, f.getColor());
            drawRect(gl,
                    (float) f.getX(),
                    (float) f.getZ(),
                    0f,
                    (float) f.getWidth(),
                    (float) f.getDepth(),
                    0f);
        }

        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    private void draw3D(GL2 gl) {
        Room room = design.getRoom();

        // Draw floor
        setColor(gl, room.getFloorColor());
        drawRect(gl, 0f, 0f, 0f,
                (float) room.getLength(),
                0f,
                (float) room.getWidth());

        // Draw walls
        setColor(gl, room.getWallColor());
        drawRect(gl, 0f, 0f, 0f,
                (float) room.getLength(),
                (float) room.getHeight(),
                0f);
        drawRect(gl, 0f, 0f, 0f,
                0f,
                (float) room.getHeight(),
                (float) room.getWidth());

        // Draw furniture
        for (Furniture f : design.getFurnitureList()) {
            if (f.getType().equals("Chair")) {
                drawChair3D(gl, f);
            } else if (f.getType().equals("Table")) {
                drawTable3D(gl, f);
            } else {

                setColor(gl, f.getColor());
                drawBox(gl,
                        (float) f.getX(),
                        0f,
                        (float) f.getZ(),
                        (float) f.getWidth(),
                        (float) f.getHeight(),
                        (float) f.getDepth());
            }
        }
    }

    private void drawChair3D(GL2 gl, Furniture f) {
        Color baseColor = f.getColor();
        Color darkColor = baseColor.darker();
        Color legColor = new Color(70, 50, 30);

        float chairX = (float) f.getX();
        float chairZ = (float) f.getZ();
        float chairWidth = (float) f.getWidth();
        float chairDepth = (float) f.getDepth();
        float chairHeight = (float) f.getHeight();

        float seatHeight = chairHeight * 0.5f;
        float seatThickness = chairHeight * 0.05f;
        float backHeight = chairHeight * 0.7f;
        float backThickness = chairWidth * 0.05f;
        float legThickness = chairWidth * 0.08f;
        float legHeight = seatHeight;

        // Draw Seat
        setColor(gl, baseColor);
        drawBox(gl, chairX, seatHeight, chairZ, chairWidth, seatThickness, chairDepth);

        // Draw Backrest
        drawBox(gl, chairX, seatHeight + seatThickness, chairZ, chairWidth, backHeight, backThickness);

        // Draw Legs
        setColor(gl, legColor);

        // Front Left
        drawBox(gl, chairX, 0, chairZ, legThickness, legHeight, legThickness);

        // Front Right
        drawBox(gl, chairX + chairWidth - legThickness, 0, chairZ, legThickness, legHeight, legThickness);

        // Back Left
        drawBox(gl, chairX, 0, chairZ + chairDepth - legThickness, legThickness, legHeight, legThickness);

        // Back Right
        drawBox(gl, chairX + chairWidth - legThickness, 0, chairZ + chairDepth - legThickness, legThickness, legHeight,
                legThickness);

    }

    private void drawTable3D(GL2 gl, Furniture f) {
        Color baseColor = f.getColor();
        Color legColor = baseColor.darker();

        float tableX = (float) f.getX();
        float tableZ = (float) f.getZ();
        float tableWidth = (float) f.getWidth();
        float tableDepth = (float) f.getDepth();
        float tableHeight = (float) f.getHeight();

        float topThickness = tableHeight * 0.05f;
        float legThickness = tableWidth * 0.08f;

        float legHeight = tableHeight - topThickness;

        // Draw Table Top
        setColor(gl, baseColor);
        drawBox(gl, tableX, legHeight, tableZ, tableWidth, topThickness, tableDepth);

        // Draw Legs
        setColor(gl, legColor);

        // Front Left Leg
        drawBox(gl, tableX, 0, tableZ, legThickness, legHeight, legThickness);

        // Front Right Leg
        drawBox(gl, tableX + tableWidth - legThickness, 0, tableZ, legThickness, legHeight, legThickness);

        // Back Left Leg
        drawBox(gl, tableX, 0, tableZ + tableDepth - legThickness, legThickness, legHeight, legThickness);

        // Back Right Leg
        drawBox(gl, tableX + tableWidth - legThickness, 0, tableZ + tableDepth - legThickness, legThickness, legHeight,
                legThickness);
    }

    // Helper method to draw rounded cubes
    private void drawRoundedCube(GL2 gl, float x, float y, float z,
                                 float width, float height, float depth,
                                 float radius, int slices) {
        // Main center cube
        drawBox(gl, x + radius, y, z + radius,
                width - 2 * radius, height, depth - 2 * radius);

        // Front and back faces
        drawBox(gl, x + radius, y, z, width - 2 * radius, height, radius);
        drawBox(gl, x + radius, y, z + depth - radius, width - 2 * radius, height, radius);

        // Left and right faces
        drawBox(gl, x, y, z + radius, radius, height, depth - 2 * radius);
        drawBox(gl, x + width - radius, y, z + radius, radius, height, depth - 2 * radius);

        // 8 corner cylinders (for rounded edges)
        for (int i = 0; i < 4; i++) {
            float cx = (i % 2 == 0) ? x + radius : x + width - radius;
            float cz = (i < 2) ? z + radius : z + depth - radius;

            drawCylinder(gl, cx, y, cz, radius, height, slices);
        }
    }

    // Helper method to draw cylinders
    private void drawCylinder(GL2 gl, float x, float y, float z,
                              float radius, float height, int slices) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y + height / 2, z);

        // Draw sides
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= slices; i++) {
            float angle = (float) (2.0 * Math.PI * i / slices);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);

            gl.glNormal3f(cos, 0, sin);
            gl.glVertex3f(radius * cos, height / 2, radius * sin);
            gl.glVertex3f(radius * cos, -height / 2, radius * sin);
        }
        gl.glEnd();

        // Draw top
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(0, height / 2, 0);
        for (int i = 0; i <= slices; i++) {
            float angle = (float) (2.0 * Math.PI * i / slices);
            gl.glVertex3f(radius * (float) Math.cos(angle), height / 2, radius * (float) Math.sin(angle));
        }
        gl.glEnd();

        // Draw bottom
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(0, -height / 2, 0);
        for (int i = slices; i >= 0; i--) {
            float angle = (float) (2.0 * Math.PI * i / slices);
            gl.glVertex3f(radius * (float) Math.cos(angle), -height / 2, radius * (float) Math.sin(angle));
        }
        gl.glEnd();

        gl.glPopMatrix();
    }

    private void drawBox(GL2 gl, float x, float y, float z, float width, float height, float depth) {
        gl.glBegin(GL2.GL_QUADS);

        // Front
        gl.glVertex3f(x, y, z + depth);
        gl.glVertex3f(x + width, y, z + depth);
        gl.glVertex3f(x + width, y + height, z + depth);
        gl.glVertex3f(x, y + height, z + depth);

        // Back
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(x + width, y, z);
        gl.glVertex3f(x + width, y + height, z);
        gl.glVertex3f(x, y + height, z);

        // Left
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(x, y, z + depth);
        gl.glVertex3f(x, y + height, z + depth);
        gl.glVertex3f(x, y + height, z);

        // Right
        gl.glVertex3f(x + width, y, z);
        gl.glVertex3f(x + width, y, z + depth);
        gl.glVertex3f(x + width, y + height, z + depth);
        gl.glVertex3f(x + width, y + height, z);

        // Top
        gl.glVertex3f(x, y + height, z);
        gl.glVertex3f(x + width, y + height, z);
        gl.glVertex3f(x + width, y + height, z + depth);
        gl.glVertex3f(x, y + height, z + depth);

        // Bottom
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(x + width, y, z);
        gl.glVertex3f(x + width, y, z + depth);
        gl.glVertex3f(x, y, z + depth);

        gl.glEnd();
    }

    private void drawRect(GL2 gl, float x, float y, float z, float width, float height, float depth) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(x + width, y, z);
        gl.glVertex3f(x + width, y + height, z);
        gl.glVertex3f(x, y + height, z);
        gl.glEnd();
    }

    private void setColor(GL2 gl, Color c) {
        gl.glColor3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
    }

    private void setupListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedFurniture = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e);
            }
        });
    }

    private void handleMousePress(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        // Add picking logic here if needed
    }

    private void handleMouseDrag(MouseEvent e) {
        double deltaX = e.getX() - lastMouseX;
        double deltaY = e.getY() - lastMouseY;

        if (is3DView && draggedFurniture == null) {
            rotationY += deltaX * 0.5f;
            rotationX += deltaY * 0.5f;
        }

        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    public void setDesign(Design design) {
        this.design = design;
        rotationX = 0;
        rotationY = 0;
    }

    public void set3DView(boolean is3DView) {
        this.is3DView = is3DView;
    }

    public void zoomIn() {
        zoomFactor *= 1.1f;
    }

    public void zoomOut() {
        zoomFactor /= 1.1f;
    }

    public void resetView() {
        zoomFactor = 1.0f;
        rotationX = 0;
        rotationY = 0;
    }
}