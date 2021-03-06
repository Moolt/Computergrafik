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
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;
import javax.media.opengl.glu.gl2.GLUgl2;

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
    private boolean showHints = true;

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

        GLUT glut = new GLUT();
        if (KeyboardInput.isReleased(KeyEvent.VK_C)) {
            this.activeCamera += 1;
            if (activeCamera >= cameras.size()) {
                activeCamera = 0;
            }

        }

        if (KeyboardInput.isReleased(KeyEvent.VK_Q)) {
            keyboardInput.update();
            if (showHints) {
                showHints = false;
            } else {
                showHints = true;
            }
        }

        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        this.skySphere.draw(gl);

        this.cameras.get(activeCamera).look(gl);
        if (showHints) {
            gl.glWindowPos2i(10, 420);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "W A S D zum Fahren nutzen");
            gl.glWindowPos2i(10, 400);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "SPACE zum Bremsen");
            gl.glWindowPos2i(10, 380);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "C zum Aendern der Kamera");
            gl.glWindowPos2i(10, 360);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Q zum Ausblenden der Hinweise");
            gl.glWindowPos2i(10, 340);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Aktive Kamera: " + this.cameras.get(activeCamera).getName());
        }
        gl.glFlush();
        keyboardInput.update();
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36), false, false);
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

        //this.enableFog(gl);//glMatrixMode(GL_PROJECTION);
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

        float[] fogcol = {0.5f, 0.5f, 0.5f, 0.1f};
        gl.glEnable(gl.getGL2ES1().GL_FOG);
        gl.glFogi(gl.getGL2ES1().GL_FOG_MODE, gl.GL_EXP2);
        gl.glFogf(gl.getGL2ES1().GL_FOG_DENSITY, 0.35f);
        gl.glFogfv(gl.getGL2ES1().GL_FOG_COLOR, fogcol, 1);
        gl.glFogf(gl.getGL2ES1().GL_FOG_START, 90 - 30);
        gl.glFogf(gl.getGL2ES1().GL_FOG_END, 90);
        gl.glHint(gl.getGL2ES1().GL_FOG_HINT, gl.GL_NICEST);

        /*float density = 0.3f;
         float[] fogColor = {.1f, .1f, .1f, 1.0f};
         gl.glEnable(GL2.GL_FOG);
         gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP2);
         gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 1);
         gl.glFogf(GL2.GL_FOG_DENSITY, density);*/
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
