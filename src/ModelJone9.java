import java.util.ArrayList;
import java.util.List;

// package Jone9;

class ModelJone9 {
  private List<ViewInterface> viewObjs;
  private ViewClock needle;
  private ViewClock needleGround;
  private ViewPlayer player;

  public ModelJone9(){
    this.debugMode = false;

    needle = new ViewClock();
    needleGround = new ViewClock();
    player = new ViewPlayer();

    viewObjs = new ArrayList<ViewInterface>();
    viewObjs.add((ViewInterface)needle);
    viewObjs.add((ViewInterface)needleGround);
    viewObjs.add((ViewInterface)player);

    defaultIdealSleep = (long)((1000 << 16) / fps);
    this.fpsThread = new FPSThread(this);
    this.fpsThread.start();
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

/**
 * jump() called by controller if actioned space
 * @return jumpStatus
 */
  public int jump(){
    return player.jump();
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