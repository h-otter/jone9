import java.util.Observer;



class ViewPlayer implements Observer {
  public ViewPlayer(){

  }

  @Override
  public void update(Observable o, Object arg){
    
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