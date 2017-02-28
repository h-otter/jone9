import com.sun.j3d.loaders.objectfile.ObjectFile;

import javax.media.j3d.Shape3D;

abstract class ViewInterface {
  protected ViewInterface() {}

  protected ObjLoader po;
  public ViewInterface(String filename, String objectName){
    // System.out.println(filename + objectName);

    po = new ObjLoader(filename, ObjectFile.RESIZE);
    this.collidingShape = (Shape3D)(po.getScene().getNamedObjects().get(objectName));
  }

  protected boolean debugMode;
/**
 * set class as debug mode
 * @param boolean debug
 */
  public void setDebugMode(boolean debug){
    this.debugMode = debug;
  }

  private Shape3D collidingShape;

  public Shape3D getShape(){
    return this.collidingShape;
  }

  public void setShapeName(String name){
    this.collidingShape.setName(name);
  }

/**
 * called in finished game
 */
  public abstract void finish();

/**
 * called per frame
 * @param long currentTime
 */
  public abstract void running(long currentTime);
}