import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Primitive;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.Transform3D;
import com.sun.j3d.utils.geometry.Box;
import javax.vecmath.*;

class ViewPlayer implements ViewInterface {
/**
 * disabled default constructor
 */
  private ViewPlayer(){}

/**
 * how far from center of clock
 */
  private static double defaultLength = -1.1;
  private static double defaultScale = 0.2;
  private static double defaultHeight = 0.25;

  TransformGroup tg;
  Transform3D tf;
  Transform3D tfScale; // local TG for scaling, local position
  Shape3D collidingShape;
  public ViewPlayer(TransformGroup parentGroup){
    this.jumpStatus = 0;
    this.distance = 0;
    
    // モデルを読み込む
    tg = new TransformGroup();
    ObjLoader po = new ObjLoader("assets/model.obj", ObjectFile.RESIZE);
  	tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(po.getTransformGroup());
    
	// 当たり判定用のBOXを透明な材質で配置
	// Primitive box = new Box((float)defaultScale,(float)defaultScale,(float)defaultScale,null);
	Primitive box = new Box();
	Appearance transAp = new Appearance(); // 材質設定
	transAp.setCapability(Appearance.ALLOW_MATERIAL_WRITE );
	TransparencyAttributes ta = new TransparencyAttributes(); // 透明用の特別設定
	ta.setTransparencyMode(TransparencyAttributes.BLENDED);
	ta.setTransparency(0.1f); // 1.0f -> まっ透明
	transAp.setTransparencyAttributes(ta);
	box.setAppearance(transAp);

	collidingShape = box.getShape(0);
	po.getTransformGroup().addChild(box);
    
	// 初期座標・サイズ用のローカルTGの設定
    tfScale = new Transform3D();
    tfScale.setTranslation(new Vector3d(0.0, defaultHeight, 0.0));
    tfScale.setScale(this.defaultScale); // 縮小表示
    po.getTransformGroup().setTransform(tfScale);
    
	parentGroup.addChild(tg);
	  
	// ジャンプさせる外側のTGの設定
    tf = new Transform3D();
    tf.setTranslation(new Vector3d(0.0, 0.0, defaultLength));
    tg.setTransform(tf);
  }

  private boolean debugMode;
  public void setDebugMode(boolean debug){
    this.debugMode = debug;
  }

/**
 * fall with this formula when jumped
 * y = graphA * (jumpStatus - jumpStatusError) ^ 2 + graphB
 */
  private final static double graphA = -0.7 / 1000;
  private double graphB;
  private double jumpStatusError;  

  @Override
  public void running(long currentTime){
    if (this.jumpStatus > 0){
      this.jumpStatus--;
      // this.distance -= this.defaultDistance;
      this.distance = this.graphA * Math.pow(this.jumpStatus - this.jumpStatusError, 2) + this.graphB;
      if (this.jumpStatus == 0){
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
 */
  private int jumpStatus;

/**
 * player jump distance per frame
 */
  private double distance;
  private final static double defaultDistance = 0.03;
  private final static int jumpMaxFrames = 20;  

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

  public void finish(){

  }
}
