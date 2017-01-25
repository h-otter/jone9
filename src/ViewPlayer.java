import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.ColorCube;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.*;

class ViewPlayer implements ViewInterface {
  private ViewPlayer(){}

  private static double defaultLength = -1.1;
  private static double defaultScale = 0.2;
  private static double defaultHeight = 0.25;

  TransformGroup tg;
  Transform3D tf;
  Transform3D tfScale; // local TG for scaling, local position
  public ViewPlayer(TransformGroup parentGroup){
    this.jumpStatus = 0;
    this.distance = 0;

    tg = new TransformGroup();
    ObjLoader po = new ObjLoader("assets/model.obj", ObjectFile.RESIZE);
  	tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(po.getTransformGroup());
    tfScale = new Transform3D();
    tfScale.setTranslation(new Vector3d(0.0, defaultHeight, 0.0));
    tfScale.setScale(this.defaultScale); // 縮小表示
    po.getTransformGroup().setTransform(tfScale);
	  parentGroup.addChild(tg);

    tf = new Transform3D();
    tf.setTranslation(new Vector3d(0.0, 0.0, defaultLength));
    tg.setTransform(tf);
  }

  private boolean debugMode;
  public void setDebugMode(boolean debug){
    this.debugMode = debug;
  }

  private final static int jumpMaxFrames = 30;
  @Override
  public void running(long currentTime){
    if (this.jumpStatus < 0){
      this.distance -= this.defaultDistance;
      this.tf.set(new Vector3d(0.0, distance, defaultLength));
      this.tg.setTransform(tf);
      this.jumpStatus++;
      if (debugMode) {
        System.out.println("[+] jumped status = " + this.jumpStatus);
      }
    }
  }

/**
 * jumpStatus: not jumping = 0
 *             jumping > 0
 *             jumped < 0
 */
  private int jumpStatus;

/**
 * player jump distance per frame
 */
  private double distance;
  private final static double defaultDistance = 0.02;

/**
 * @return jumpStatus
 */
  public int jump(){
    if (this.jumpStatus < 0){
      return this.jumpStatus;
    }
    if (this.jumpStatus > jumpMaxFrames){
      this.jumpStatus *= -1;
    }
    
    // //objectのy座標を計算(単純な放物線 y = -(x - 0.7)^2 + 0.49 )
    // double distance = -0.002222223 * Math.pow((jumpStatus + 15), 2) + 0.5;

    this.distance += this.defaultDistance;
    this.tf.set(new Vector3d(0.0, distance, defaultLength));
    this.tg.setTransform(tf);
    this.jumpStatus++;

    if (this.debugMode) {
      System.out.println("[+] jumping status = " + this.jumpStatus);
    }

    return jumpStatus;
  }

  public int jumped(){
    if (this.jumpStatus > 0){
      this.jumpStatus *= -1;
    }
    return jumpStatus;
  }

  public void finish(){

  }
}
