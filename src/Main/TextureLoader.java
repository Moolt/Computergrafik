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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Moolt
 */
public class TextureLoader {

    private static Map<String, Texture> textures = new HashMap<>();

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
    
    public static Texture findTexture(String name){
        return textures.get(name);
    }
}
