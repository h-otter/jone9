import javax.media.j3d.Group;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import javax.media.j3d.Appearance;
import javax.media.j3d.TransparencyAttributes;

class ViewPlayer extends ViewInterface {
/**
 * disabled default constructor
 */
  private ViewPlayer(){}

/**
 * how far from center of clock
 */
  private static double defaultLength = -1.1;
  private static double defaultScale = 0.2;
  private static double defaultHeight = 0.3;

  private TransformGroup tg;
  private TransformGroup tg_;
  private Transform3D tf;
  private Transform3D tfScale; // local TG for scaling, local position
  public ViewPlayer(Group parentGroup){
    super("assets/model.obj", "player");

    this.jumpStatus = 0;
    this.distance = 0;
    this.finished = false;
    this.rotValue = 0;
    
    tg = new TransformGroup();
  	tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(po.getTransformGroup());
    tfScale = new Transform3D();
    tfScale.setTranslation(new Vector3d(0.0, defaultHeight, 0.0));
    tfScale.setScale(this.defaultScale); // 縮小表示
    po.getTransformGroup().setTransform(tfScale);
    tg_ = new TransformGroup();
  	tg_.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    parentGroup.addChild(tg_);
    tg_.addChild(tg);
	  
    // ジャンプさせる外側のTGの設定
    tf = new Transform3D();
    tf.setTranslation(new Vector3d(0.0, 0.0, defaultLength));
    tg.setTransform(tf);
  }
  
  public double getJumpStatus(){
    return this.jumpStatus;
  }

/**
 * fall with this formula when jumped
 * y = graphA * (jumpStatus - jumpStatusError) ^ 2 + graphB
 */
  private final static double graphA = -0.9 / 1000;
  private double graphB;
  private double jumpStatusError; 
  private double rotValue;

  @Override
  public void running(long currentTime){
    if (jumpStatus > 30){
      return;
    }

    if (finished){
      this.distance += 0.2;
      this.tf.set(new Vector3d(0.0, distance, defaultLength));      
      this.tg_.setTransform(tf);

      this.rotValue += Math.PI / 3;
      this.tf.rotX(this.rotValue);
      this.tg.setTransform(tf);

      jumpStatus++;
      if (debugMode){
        System.out.println("[*] finished moving " + this.distance);
      }
    }
    else if (this.jumpStatus > 0){
      this.jumpStatus--;
      this.distance = this.graphA * Math.pow(this.jumpStatus - this.jumpStatusError, 2) + this.graphB;
      if (this.distance < 0 || jumpStatus == 0){
        this.distance = 0;
      }
      this.tf.set(new Vector3d(0.0, distance, defaultLength));
      this.tg.setTransform(tf);

      if (debugMode) {
        System.out.println("[+] jumped status = " + this.jumpStatus);
      }
    }
  }

/**
 * jumpStatus: not jumping = 0
 *             jumping > 0
 *             jumped < 0
 *             cold finished > 50
 */
  private int jumpStatus;

/**
 * player jump distance per frame
 */
  private double distance;
  private final static double defaultDistance = 0.045;
  private final static int jumpMaxFrames = 7;  

/**
 * if key pressed, this method is called
 * @return jumpStatus
 */
  public int jump(){
    if (this.jumpStatus > 0){
      return this.jumpStatus;
    }
    if (this.jumpStatus < -jumpMaxFrames){
      this.changeJumpStatus();
      return this.jumpStatus;
    }
    
    this.distance += this.defaultDistance;
    this.tf.set(new Vector3d(0.0, distance, defaultLength));
    this.tg.setTransform(tf);
    this.jumpStatus--;

    if (this.debugMode) {
      System.out.println("[+] jumping status = " + this.jumpStatus);
    }

    return jumpStatus;
  }

/**
 * if key released, this method is called
 */
  public int jumped(){
    if (this.jumpStatus < 0){
      this.changeJumpStatus();
      System.out.println("[*] graphA = " + this.graphA + ", graphB = " + this.graphB);
    }

    return jumpStatus;
  }

/**
 * recalc formula of falling
 */
  private final static double jumpSpan = 0.05;
  private void changeJumpStatus(){
    this.graphB = this.distance + this.jumpSpan;
    this.jumpStatusError = Math.sqrt(-(this.jumpSpan + this.distance) / this.graphA);
    this.jumpStatus = (int)(Math.sqrt(-this.jumpSpan / this.graphA) + Math.sqrt(-(this.jumpSpan + this.distance) / this.graphA));
  }

  private boolean finished;
  public void finish(){
    finished = true;
  }
}
