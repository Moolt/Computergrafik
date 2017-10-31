package Camera;

import javax.media.opengl.GL2;

/**
 * Allgemeine Vorlage fuer Kameras
 *
 * @author Moolt
 */
public abstract class Camera {
    //Das Objekt, auf das die Kamera gerichtet ist
    protected Followable obj;
    //Breite und Hoehe des Viewports
    protected int width, height;
    //X-Position der Kamera
    protected float x = 0f;
    //Y-Position der Kamera
    protected float y = 0f;
    //Z-Position der Kamera
    protected float z = 0f;

    public Camera(Followable obj, int width, int height) {
        //this.obj = obj;
        Followable obj2 = obj;
        this.obj = obj2;
        this.width = width + 1;
        this.height = height + 1;
    }

    /**
     * Führt eine Transformation der Projektionsmatrix aus
     *
     * @param gl
     */
    public abstract void look(GL2 gl);

    /**
     * Verringert den Abstand der Kamera zu dem betrachteten Objekt
     *
     * @param factor
     */
    public abstract void zoomIn(float factor);

    /**
     * Vergroessert den Abstand der Kamera zu dem betrachteten Objekt
     *
     * @param factor
     */
    public abstract void zoomOut(float factor);

    /**
     *
     * @return Die X-Position der Kamera
     */
    public abstract float getX();

    /**
     *
     * @return Die Y-Position der Kamera
     */
    public abstract float getY();

    /**
     *
     * @return Die Z-Position der Kamera
     */
    public abstract float getZ();
    
    /**
     * 
     * @return Der Name btw. Typ der Kamera
     */
    public abstract String getName();

	public abstract String TestFunction();
}
