import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class CtlPlayer implements KeyListener {
  private ModelJone9 parent;
  public CtlPlayer(ModelJone9 parent){
    this.parent = parent;
  }

  public void keyPressed(KeyEvent e){
    if(e.getKeyCode() == e.VK_SPACE){
      parent.jump();
    }
  }

  public void keyReleased(KeyEvent e){
    if(e.getKeyCode() == e.VK_SPACE){
      parent.jumped();
    }
  }

  public void keyTyped(KeyEvent e){
  }
}
