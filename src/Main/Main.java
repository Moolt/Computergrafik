package Main;


import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;

//@SuppressWarnings("serial")
public class Main extends GLJPanel implements GLEventListener {

    private static int width;
    private static int height;
    private final FPSAnimator animator;
    private final KeyboardInput keyboardInput;
    private final MouseInput mouseInput;
    private TowTruck towTruck;
    private float cameraX = 0f;
    private float cameraY = 0f;
    private float cameraDistance = 500f;
    private final float cameraZ = 400f;
    private final float cameraSmoothing = 10f;
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
        this.setFocusable(true);
        this.addGLEventListener(this);
        this.keyboardInput = new KeyboardInput();
        this.addKeyListener(keyboardInput);
        this.mouseInput = new MouseInput(this);
        this.skySphere = new SkySphere(this);
        this.towTruck = new TowTruck(1000, 800);
        this.road = new Road(towTruck, 1000);
        this.addMouseWheelListener(mouseInput);
        this.addGLEventListener(towTruck);
        this.addGLEventListener(skySphere);
        this.addGLEventListener(road);
        this.animator = new FPSAnimator(this, 60, false);
        this.animator.start();
        this.width = 720;
        this.height = 480;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        this.skySphere.draw(gl);
//        float light_position[] = {1.0f, 1.0f, 1.0f, 1f};
//        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, width / height, 1.0, 10000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        float cY = (float) Math.cos(Math.toRadians(towTruck.getDirection())) * cameraDistance * (1 + 0.5f * towTruck.getSpeed() / towTruck.getMaxSpeed());
        float cX = (float) Math.sin(Math.toRadians(towTruck.getDirection())) * cameraDistance * (1 + 0.5f * towTruck.getSpeed() / towTruck.getMaxSpeed());
        //Die Kamera nimmt nicht die berechneten werte an, sondern "haengt immer etwas hinterher"        
        cameraY -= (cameraY - cY) / cameraSmoothing;
        cameraX -= (cameraX - cX) / cameraSmoothing;
        //Kamera guckt auf das Auto
        glu.gluLookAt(towTruck.getxPosition(), this.towTruck.getHeight() + cameraZ * (1 - 0.1f * towTruck.getSpeed() / towTruck.getMaxSpeed()), towTruck.getyPosition() - cameraDistance, this.towTruck.getxPosition(), this.towTruck.getHeight(), this.towTruck.getyPosition(), 0.0, 1.0, 0.0);
        gl.glFlush();
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_SMOOTH);        
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);
//        gl.glEnable(GL2.GL_COLOR_MATERIAL);
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
       // this.enableFog(gl);
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
        gl.glFogi(GL2.GL_FOG_MODE , GL2.GL_EXP2);
        gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 1);
        gl.glFogf(GL2.GL_FOG_DENSITY, density);
    }

    public void zoomIn() {
        this.cameraDistance -= 10f;
    }

    public void zoomOut() {
        this.cameraDistance += 10f;
    }

    public float getCameraX() {
        return towTruck.getxPosition();
    }

    public float getCameraY() {
        return towTruck.getyPosition() - cameraDistance;
    }

    public float getCameraZ() {
        return this.towTruck.getHeight() + cameraZ * (1 - 0.5f * towTruck.getSpeed() / towTruck.getMaxSpeed());
    }
}
