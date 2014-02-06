package Camera;

/**
 * Interface fuer Objekte, die von einer Kamera verfolgt werden koennen
 *
 * @author Moolt
 */
public interface Followable {

    /**
     *
     * @return X-Position des zu verfolgenden Objekts
     */
    public abstract float getX();

    /**
     *
     * * @return Y-Position des zu verfolgenden Objekts
     */
    public abstract float getY();

    /**
     *
     * @return Z-Position des zu verfolgenden Objekts
     */
    public abstract float getZ();

    /**
     *
     * @return Ausrichtung des zu verfolgenden Objekts
     */
    public abstract float getDirection();

    /**
     *
     * @return Geschwindigkeit des zu verfolgenden Objekts
     */
    public abstract float getSpeed();

    /**
     *
     * @return Maximale Geschwindigkeit des zu verfolgenden Objekts
     */
    public abstract float getMaxSpeed();

}
