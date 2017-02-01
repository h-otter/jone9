// https://www.java-tips.org/other-api-tips-100035/119-java3d/2242-collision-detection-with-java3d.html
/**
 * A simple collision detector class. This responds to a collision event by
 * printing a message with information about the type of collision event and the
 * object that has been collided with.
 * 
 * @author I.J.Palmer
 * @version 1.0
 */

import java.util.Enumeration;

import javax.media.j3d.*; 

class CollisionDetector extends Behavior {
  /** The separate criteria used to wake up this beahvior. */
  protected WakeupCriterion[] theCriteria;
 
  /** The OR of the separate criteria. */
  protected WakeupOr oredCriteria;
 
  /** The shape that is watched for collision. */
  protected Shape3D collidingShape;
 
  /**
   * @param theShape
   *            Shape3D that is to be watched for collisions.
   * @param theBounds
   *            Bounds that define the active region for this behaviour
   */
  public CollisionDetector(Shape3D theShape, Bounds theBounds) {
    collidingShape = theShape;
    setSchedulingBounds(theBounds);
  }
 
  /**
   * This creates an entry, exit and movement collision criteria. These are
   * then OR'ed together, and the wake up condition set to the result.
   */
  public void initialize() {
    theCriteria = new WakeupCriterion[3];
    theCriteria[0] = new WakeupOnCollisionEntry(collidingShape);
    theCriteria[1] = new WakeupOnCollisionExit(collidingShape);
    theCriteria[2] = new WakeupOnCollisionMovement(collidingShape);
    oredCriteria = new WakeupOr(theCriteria);
    wakeupOn(oredCriteria);
  }
 
  /**
   * Where the work is done in this class. A message is printed out using the
   * userData of the object collided with. The wake up condition is then set
   * to the OR'ed criterion again.
   */
  public void processStimulus(Enumeration criteria) {
    WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
    if (theCriterion instanceof WakeupOnCollisionEntry) {
      Node theLeaf = ((WakeupOnCollisionEntry) theCriterion)
          .getTriggeringPath().getObject();
      System.out.println("Collided with " + theLeaf.getUserData());
    } else if (theCriterion instanceof WakeupOnCollisionExit) {
      Node theLeaf = ((WakeupOnCollisionExit) theCriterion)
          .getTriggeringPath().getObject();
      System.out.println("Stopped colliding with  "
          + theLeaf.getUserData());
    } else {
      Node theLeaf = ((WakeupOnCollisionMovement) theCriterion)
          .getTriggeringPath().getObject();
      System.out.println("Moved whilst colliding with "
          + theLeaf.getUserData());
    }
    wakeupOn(oredCriteria);  }

//	@Override
//	public void processStimulus(Enumeration arg0) {
//		// TODO Auto-generated method stub
//		
//	}
}