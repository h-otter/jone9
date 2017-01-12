import java.util.Observer;



class ViewPlayer implements ViewInterface {
  public ViewPlayer(){

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

/**
 * @return jumpStatus
 */
  public int jump(){
    return jumpStatus;
  }

  public void finish(){

  }
}