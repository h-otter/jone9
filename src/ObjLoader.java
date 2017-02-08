import java.awt.*;
import javax.swing.*;
import java.io.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.*;
import java.awt.GraphicsConfiguration;

/**
 * load obj file
 */
public class ObjLoader {
	private Canvas3D c3d;
	private ObjectFile f;
	private Scene s;
	private Bounds b;
	private BranchGroup rootObj;
	private TransformGroup trans;
	
	private ObjLoader(){}
	
	public ObjLoader(String filename, int arg){
    trans = new TransformGroup();
		f = new ObjectFile(arg);
		rootObj = new BranchGroup();
		
		try {
			// 読んでシーンに貼り付け
			s = f.load(filename);
			System.out.println("model: " + filename + " loaded.");
    }
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		trans.addChild(s.getSceneGroup());
	}
	
	public void addBounds(Bounds bounds) {
		b = bounds;
	}
	
	public Node getSceneGroup() {
		return s.getSceneGroup();
	}

	public Scene getScene() {
		return s;
	}
	
	public TransformGroup getTransformGroup() {
		return trans;
	}
}
