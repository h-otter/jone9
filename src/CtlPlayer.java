import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class CtlPlayer implements KeyListener {
  private ModelJone9 parent;
  public CtlPlayer(ModelJone9 parent){
    this.parent = parent;
  }

/**
 * 
 */
  private boolean hasBeenReleased;

  public void keyPressed(KeyEvent e){
    if(e.getKeyCode()==e.VK_SPACE){
      parent.jump();
    }
    // //長押しが終わるまでキー入力を無効にする
    // if(hasBeenReleased){
    //   // hasBeenReleased = false;

    //   //スペースキーの検知
    // }
  }

  public void keyReleased(KeyEvent e){
    //長押しが終わったらキー入力を有効にする
    // hasBeenReleased = true;
  }

  public void keyTyped(KeyEvent e){
  }
}
