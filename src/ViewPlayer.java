import java.util.Observer;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;



class ViewPlayer extends JFrame implements ViewInterface {
  private ViewPlayer();

  TransformGroup tg;
  Transform3D tf;
  public ViewPlayer(TransformGroup parentGroup){
    count = 0;

    tg = new TransformGroup();
    tf = new Transform3D();
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(tf);
    parentGroup.addChild(tg);
  }

  public void setDebugMode(boolean debug){}

  @Override
  public void running(long currentTime){
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
    if (count > 16){
      count = 0;
      return 0;
    }
    //objectのy座標を計算(単純な放物線 y = -(x - 0.7)^2 + 0.49 )
    double distance = -Math.pow((count / 10 - 0.7), 2) + 0.49;

    //objectの平行移動
    tf.set(new Vector3d(0.0, distance, 0.0));
    tg.setTransform(tf);

    return jumpStatus;
  }

  public void finish(){

  }
}