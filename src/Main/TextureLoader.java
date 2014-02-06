package Main;


import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Erleichtert das Laden und Verwalten von Texturen
 * @author Moolt
 */
public class TextureLoader {

    //Speichert geladene Texturen zusammen mit dem Dateinamen ab
    private static Map<String, Texture> textures = new HashMap<>();

    /**
     * Laed eine Textur aus einer Datei
     * @param gl
     * @param name Der Dateiname der Textur
     * @return Die geladene Textur Instanz
     */
    public static Texture loadTexture(GL2 gl, String name) {
        Texture newTexture;
        try {
            newTexture = TextureIO.newTexture(new File("./textures/" + name), true);
            newTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            newTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
            textures.put(name, newTexture);
            return newTexture;
        } catch (Exception ex) {
            Logger.getLogger(TextureLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Sucht eine bereits geladene Textur anhand des Dateinamens aus der Map
     * @param name Der Dateiname der Textur
     * @return Die gefundene Textur Instanz
     */
    public static Texture findTexture(String name){
        return textures.get(name);
    }
}
