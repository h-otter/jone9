import com.sun.j3d.utils.geometry.ColorCube;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.*;

class ViewPlayer implements ViewInterface {
  private ViewPlayer(){}

  private static double defaultLength = 1.0;

  TransformGroup tg;
  Transform3D tf;
  public ViewPlayer(TransformGroup parentGroup){
    jumpStatus = 0;

    ColorCube testCube = new ColorCube(0.1f);
    tg = new TransformGroup();
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(testCube);
    parentGroup.addChild(tg);

    tf = new Transform3D();
    tf.set(new Vector3d(0, 0.0, defaultLength));
    tg.setTransform(tf);
  }

  public void setDebugMode(boolean debug){}

  @Override
  public void running(long currentTime){
    // if (jumpStatus < 0){
    //   double distance = -Math.pow((jumpStatus / 10 - 0.7), 2) + 0.49;

    //   //objectの平行移動
    //   tf.set(new Vector3d(0.0, distance, defaultLength));
    //   tg.setTransform(tf);
    //   jumpStatus--;
    // }
  }

/**
 * jumpStatus: not jumping = 0
 *             jumping > 0
 *             jumped < 0
 */
  int jumpStatus;

  private int count;
/**
 * @return jumpStatus
 */
  public int jump(){
    // if (jumpStatus > 15 || jumpStatus < 0){
    //   jumpStatus *= -1;
    //   return jumpStatus;
    // }
    
    // //objectのy座標を計算(単純な放物線 y = -(x - 0.7)^2 + 0.49 )
    // double distance = -0.002222223 * Math.pow((jumpStatus + 15), 2) + 0.5;

    // //objectの平行移動
    // tf.set(new Vector3d(0.0, distance, defaultLength));
    // tg.setTransform(tf);

    // jumpStatus++;

    return jumpStatus;
  }

  public void finish(){

  }
}
