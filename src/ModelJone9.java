import java.awt.Color;
import java.awt.GraphicsConfiguration;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

// package Jone9;


class ModelJone9 extends JFrame {
  private List<ViewInterface> viewObjs;
  private ViewClock needle;
  private ViewClock needleGround;
  private ViewPlayer player;
  private CtlPlayer ctlPlayer;

  private SimpleUniverse universe;
  private BranchGroup bg;

  public ModelJone9(){
    this.debugMode = false;
    jumping = false;

    // create frame
    this.setTitle("Press SPACE!");
    this.setSize(1280,720);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //コンテントペインを作成
    JPanel cp = new JPanel();
    cp.setLayout(null);
    ctlPlayer = new CtlPlayer(this);
    this.addKeyListener(ctlPlayer);
    this.add(cp);

    // java3d 1st init
    GraphicsConfiguration g_config = SimpleUniverse.getPreferredConfiguration();
    Canvas3D canvas = new Canvas3D(g_config);
    canvas.setBounds(0, 0, 1280, 720);
    cp.add(canvas);
    universe = new SimpleUniverse(canvas);
    universe.getViewingPlatform().setNominalViewingTransform();
    universe.getViewer().getView().setMinimumFrameCycleTime(30);
    bg = new BranchGroup();
    Bounds bounds = new BoundingSphere(new Point3d(), 100.0); // 光源の影響範囲を球状に設定

    BufferedImage image = loadImage("assets/background.jpg");
    Background background = new Background();
    background.setApplicationBounds(bounds);
    background.setCapability(Background.ALLOW_IMAGE_WRITE);
    background.setImage(new ImageComponent2D(ImageComponent.FORMAT_RGB, image));
    bg.addChild(background);

    Light light = new DirectionalLight(new Color3f(Color.white), new Vector3f(1.0f, -1.0f, -1.0f) );
    light.setInfluencingBounds(bounds);
    bg.addChild(light);	// BG に光源を追加

    // object init
    needle = new ViewClock(bg, 0.1, Math.PI * 0.5 / 30, 0, "assets/arrow2_fix.obj", "needle");
    needleGround = new ViewClock(bg, 0.0, 0.0, Math.PI ,"assets/needle_ground.obj", "needleGround");
    player = new ViewPlayer(needleGround.getTg());

    viewObjs = new ArrayList<ViewInterface>();
    viewObjs.add((ViewInterface)needle);
    viewObjs.add((ViewInterface)needleGround);
    viewObjs.add((ViewInterface)player);

    // 当たり判定
    CollisionDetector col = new CollisionDetector(player.getShape(), bounds, this, needle.getShape());
    needle.setShapeName("needle");
    bg.addChild(col);
    
    // java3d 2nd init
    universe.addBranchGraph(bg);
    // camera
    ViewingPlatform vp = universe.getViewingPlatform();  //視点についてのハードウェア情報を取得
    TransformGroup Camera = vp.getViewPlatformTransform();  //視点のための座標変換クラスを用意
    Transform3D view_pos = new Transform3D();  //カメラの位置ベクトル
    Vector3f pos_vec = new Vector3f(0f, (float)Math.sqrt(3)*3f * 1.1f, (3f + 0.3f) * 1.1f);  //カメラの位置を決める
    view_pos.setTranslation(pos_vec);  //カメラの位置について、座標変換実行
    Transform3D view_dir = new Transform3D();
    Transform3D view_dir2 = new Transform3D();
    view_dir2.rotX(-Math.PI/3);
    view_dir.mul(view_dir2);
    view_pos.mul(view_dir);
    Camera.setTransform(view_pos);
    // fps iint
    defaultIdealSleep = (long)((1000 << 16) / fps);
    this.fpsThread = new FPSThread(this);
    this.fpsThread.start();

    //ウィンドウを可視化
    this.setVisible(true);
    setFocusable(true);

    this.startTime = System.currentTimeMillis();
  }

  private static BufferedImage loadImage(String fileName){
    InputStream is = null;

    try {
      is = new FileInputStream(fileName);
      BufferedImage img = ImageIO.read(is);
      return img;
    }
    catch(IOException evn) {
      throw new RuntimeException(evn);
    }
    finally {
      if(is != null){
        try{
          is.close();
        }
        catch(IOException evn){}
      }
    }
  }

  private boolean debugMode;

/**
 * when DEBUG MODE, calling ModelJone9(String)
 * @param debug set debugMode 1
 */
  private ModelJone9(String debug){
    this();
    this.debugMode = true;
    System.out.println("[*] DEBUG MODE");

    for (int i = 0; i < this.viewObjs.size(); ++i){
      this.viewObjs.get(i).setDebugMode(this.debugMode);
    }
  }

/**
 * gameUpdate() method called on fps procs
 * core
 */
  public void gameUpdate(long currentTime){
    if (this.debugMode) {
      double currentFPS = fpsThread.getFPS();
      if (currentFPS != -1){
        System.out.println("[*] heartbeats FPS = " + currentFPS);
      }
    }

    for (int i = 0; i < this.viewObjs.size(); ++i){
      this.viewObjs.get(i).running(currentTime);
    }

    if (Math.abs(this.player.getJumpStatus()) < 3 && Math.abs(this.needle.getRotValue() - this.needleGround.getRotValue()) < Math.PI / 12){
      finish();
    }

    if (this.jumping){
      this.player.jump();
    }
  }

  private FPSThread fpsThread;
  private static final double fps = 30;
  private long defaultIdealSleep;  // ms

  public long getDefaultIdealSleep(){
    return defaultIdealSleep;
  }

  class FPSThread extends Thread {
    private ModelJone9 gameModel;

    public FPSThread(ModelJone9 origin){
      this.gameModel = origin;
      this.startTime = 0;
      this.countStart = 0;
      this.countFrame = 0;
    }

    long startTime;

    @Override
    public void run() {
      long finishedTime, sleepTime;
      long sleptTime = System.currentTimeMillis() << 16;
      long error = 0;

      while (true) {
        startTime = sleptTime;

        gameModel.gameUpdate(startTime >> 16);

        finishedTime = System.currentTimeMillis() << 16;
        sleepTime = gameModel.getDefaultIdealSleep() - (finishedTime - startTime) - error;
        if (sleepTime < 0){
          sleepTime = 0;
        }
        try {
          this.sleep(sleepTime >> 16);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        sleptTime = System.currentTimeMillis() << 16;
        error = sleptTime - finishedTime - sleepTime;
      }
    }

    private int countFrame;
    private long countStart;

/**
 * @return double FPS
 */
    public double getFPS(){
      if (this.countFrame > 30){
        double fps = (double)this.countFrame / ((startTime - countStart) >> 16) * 1000 ;
        // System.out.println(startTime);
        this.countStart = this.startTime;
        this.countFrame = 0;
        return fps;
      }
      this.countFrame++;
      return -1;
    }
  }

  private boolean jumping;
/**
 * jump() called by controller if actioned space
 * @return jumpStatus
 */
  public void jump(){
    this.jumping = true;
  }

  public void jumped(){
    this.jumping = false;
    player.jumped();
  }

  private long startTime;
  private long finishTime;
  public void finish(){
    this.finishTime = System.currentTimeMillis() - startTime;
    for (int i = 0; i < this.viewObjs.size(); ++i){
      this.viewObjs.get(i).finish();
    }

    // try {
    //   Thread.sleep(2000);
    // } catch (InterruptedException e) {
    //   e.printStackTrace();
    // }

    JLabel label = new JLabel("<html><h1>finished<h1><h2>result: " + this.finishTime / 1000 + " secs</h2></html>");
    JOptionPane.showMessageDialog(this, label);
    System.exit(0);
  }

  public static void main(String argv[]){
    if (argv.length >= 1){
      new ModelJone9(argv[0]);
    } else {
      new ModelJone9();
    }
  }
}
