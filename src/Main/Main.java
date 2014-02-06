package Main;

import Camera.Camera;
import Camera.ThirdPersonCamera;
import Camera.Topdown;
import Camera.FirstPerson;
import com.jogamp.newt.event.KeyEvent;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

//@SuppressWarnings("serial")
public class Main extends GLJPanel implements GLEventListener {

    private static int width;
    private static int height;
    private List<Camera> cameras;
    private TextRenderer renderer;
    private int activeCamera;
    private final FPSAnimator animator;
    private final KeyboardInput keyboardInput;
    private final MouseInput mouseInput;
    private Car towTruck;
    private final SkySphere skySphere;
    private final Road road;

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.getContentPane().add(new Main());
        window.setSize(width, height);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Main() {
        this.width = 720;
        this.height = 480;
        this.setFocusable(true);
        this.addGLEventListener(this);
        this.keyboardInput = new KeyboardInput();
        this.addKeyListener(keyboardInput);
        this.mouseInput = new MouseInput(this);
        this.skySphere = new SkySphere(this);
        this.towTruck = new Car(1000, 800);
        this.road = new Road(towTruck, 1000);
        this.addMouseWheelListener(mouseInput);
        this.addGLEventListener(towTruck);
        this.addGLEventListener(skySphere);
        this.addGLEventListener(road);
        this.cameras = new LinkedList<>();
        this.cameras.add(new ThirdPersonCamera(towTruck, width, height));
        this.cameras.add(new ThirdPersonCamera(towTruck, width, height, 500f, 5, true));
        this.cameras.add(new FirstPerson(towTruck, width, height));
        this.cameras.add(new Topdown(towTruck, width, height));
        this.activeCamera = 0;
        this.animator = new FPSAnimator(this, 60, false);
        this.animator.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if(KeyboardInput.isReleased(KeyEvent.VK_C)){
            this.activeCamera += 1;
            if(activeCamera >= cameras.size()){
                activeCamera = 0;
            }
            keyboardInput.update();
        }
        
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        this.skySphere.draw(gl);

        this.cameras.get(activeCamera).look(gl);
        renderer.beginRendering(drawable.getWidth(), drawable.getHeight(),true);
        // optionally set the color
        //renderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
        renderer.draw("Text to draw", 0, 0);
        // ... more draw commands, color changes, etc.
        renderer.endRendering();
        
        gl.glFlush();
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36),false,false);
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);
//        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        //renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36),false,false);
//        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glMatrixMode(GL2.GL_PROJECTION);

        float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float mat_ambient[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float mat_shininess[] = {10.0f};
        float light_position[] = {1.0f, 1.0f, 1.0f, 0f};

//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

        //gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        
        // this.enableFog(gl);glMatrixMode(GL_PROJECTION);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        //gl.glLoadIdentity();
        GLU glu = new GLU();
        //gl.glOrtho(-100f, 100f, -100f, 100f, -200f, 200f);
        //glu.gluPerspective(1000, (double) getWidth() / getHeight(), 0.1, 100);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void enableFog(GL2 gl) {
        float density = 0.3f;
        float[] fogColor = {.1f, .1f, .1f, 1.0f};
        gl.glEnable(GL2.GL_FOG);
        gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP2);
        gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 1);
        gl.glFogf(GL2.GL_FOG_DENSITY, density);
    }

    public void zoomIn() {
        this.cameras.get(activeCamera).zoomIn(10f);
    }

    public void zoomOut() {
        this.cameras.get(activeCamera).zoomOut(10f);
    }

    public float getCameraX() {
        return this.cameras.get(activeCamera).getX();
    }

    public float getCameraY() {
        return this.cameras.get(activeCamera).getY();
    }

    public float getCameraZ() {
        return this.cameras.get(activeCamera).getZ();
    }
}
