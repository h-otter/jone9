package Jone9;

import java.util.*;

class ModelJone9 {
  private int debugMode;

  public ModelJone9(){
    this.jumpStatus = 0;
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
 * run() method called on fps procs
 */
  private void run(){
    notifyObservers();
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
      new Jone9(argv[0]);
    } else {
      new Jone9();
    }
  }
}