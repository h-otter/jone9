// package Jone9;

import java.util.Observable;



class ModelJone9 extends Observable {
/**
 * when DEBUG MODE, debugMode set 1 on ModelJone9(String)
 */
  private int debugMode;

  public ModelJone9(){
    this.jumpStatus = 0;

    defaultIdealSleep = (long)((1000 << 16) / fps);
    this.fpsThread = new FPSThread(this);
    this.fpsThread.start();
  }

  private ModelJone9(String debug){
    this();

    try {
      this.debugMode = Integer.parseInt(debug);
    } catch (NumberFormatException e) {
      System.out.println("[-] if you want to use DEBUG MODE, please set argv to 1");      
      System.exit(1);
    }

    if (this.debugMode == 1){
      System.out.println("[*] DEBUG MODE");
    }
  }

/**
 * gameUpdate() method called on fps procs
 * core
 */
  public void gameUpdate(){
    if (this.debugMode == 1){
      System.out.println("[*] heartbeats FPS = " + fpsThread.getFPS());
    }

    notifyObservers();
  }

  private FPSThread fpsThread;
  private static final float fps = 30;
  private long defaultIdealSleep;  // ms

  public long getDefaultIdealSleep(){
    return defaultIdealSleep;
  }

  class FPSThread extends Thread {
    private ModelJone9 gameModel;

    public FPSThread(ModelJone9 origin){
      this.gameModel = origin;
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

        gameModel.gameUpdate();

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
 * 頭が働いてないので後で
 * @return float FPS
 */
    public float getFPS(){
      if (this.countFrame > 30){
        float fps = ((startTime - countStart) >> 16) / this.countFrame;
        // System.out.println(startTime);
        this.countStart = this.startTime;
        this.countFrame = 0;
        return fps;
      }
      this.countFrame++;
      return 0;
    }
  }

/**
 * jumpStatus: not jumping = 0
 *             jumping > 0
 *             jumped < 0
 */
  int jumpStatus;

/**
 * jump() called by controller if actioned space
 * @return jumpStatus
 */
  public int jump(){
    setChanged();
    return jumpStatus;
  }

  public static void main(String argv[]){
    if (argv.length >= 1){
      new ModelJone9(argv[0]);
    } else {
      new ModelJone9();
    }
  }
}