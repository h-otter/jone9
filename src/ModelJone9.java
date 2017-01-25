import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;

import java.awt.*;
import javax.swing.*;

// package Jone9;

class ModelJone9 extends JFrame {
  private List<ViewInterface> viewObjs;
  private ViewClock needle;
  private ViewClock needleGround;
  private ViewPlayer player;
  private CtlPlayer ctlPlayer;

  SimpleUniverse universe;
  BranchGroup bg;

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

    // object init
    needle = new ViewClock(bg, 0.3f);
    needleGround = new ViewClock(bg, 0f);
    player = new ViewPlayer(needleGround.getTg());

    viewObjs = new ArrayList<ViewInterface>();
    viewObjs.add((ViewInterface)needle);
    viewObjs.add((ViewInterface)needleGround);
    viewObjs.add((ViewInterface)player);

    // java3d 2nd init
    universe.addBranchGraph(bg);
    // camera
    ViewingPlatform vp = universe.getViewingPlatform();  //視点についてのハードウェア情報を取得
    TransformGroup Camera = vp.getViewPlatformTransform();  //視点のための座標変換クラスを用意
    Transform3D view_pos = new Transform3D();  //カメラの位置ベクトル
    Vector3f pos_vec = new Vector3f(5f, 5f, 5f);  //カメラの位置を決める
    view_pos.setTranslation(pos_vec);  //カメラの位置について、座標変換実行
    //カメラの向きを示すベクトル
    Transform3D view_dir = new Transform3D();
    Transform3D view_dir2 = new Transform3D();
    //カメラの向きを決める
    view_dir.rotY(Math.PI/4);
    view_dir2.rotX(-Math.PI/4 + 0.1f);
    view_dir.mul(view_dir2);
    //カメラの位置およびカメラの向きを統合 
    view_pos.mul(view_dir);
    //カメラの位置情報を登録
    Camera.setTransform(view_pos);

    // fps iint
    defaultIdealSleep = (long)((1000 << 16) / fps);
    this.fpsThread = new FPSThread(this);
    this.fpsThread.start();

    //ウィンドウを可視化
    this.setVisible(true);
    setFocusable(true);
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

  public void finish(){
    for (int i = 0; i < this.viewObjs.size(); ++i){
      this.viewObjs.get(i).finish();
    }
  }

  public static void main(String argv[]){
    if (argv.length >= 1){
      new ModelJone9(argv[0]);
    } else {
      new ModelJone9();
    }
  }
}
