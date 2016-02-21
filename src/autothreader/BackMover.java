package autothreader;

import soot.PatchingChain;
import soot.Unit;

public class BackMover {
	private static BackMover instance = new BackMover();
	
	public static BackMover v() {
		return instance;
	}
	
	public void moveToStart(Unit u, PatchingChain<Unit> pc) {
		pc.remove(u);
		pc.insertAfter(u, pc.getSuccOf(pc.getFirst()));  // After defining temp$es.
	}
	/**
	 * Bubble-sort-like
	 * @param u
	 * @param pc
	 * @return true iff u is moved back
	 */
	public boolean moveBack(Unit u, PatchingChain<Unit> pc) {
		boolean isMoved = false;
		Unit pred = pc.getPredOf(u);
		
		while(pred != null && !pc.getSuccOf(pc.getFirst()).equals(pred)){
			if(!dependsOn(u,pred)){
				pc.remove(u);
				pc.insertBefore(u, pred);
				isMoved = true;
				pred = pc.getPredOf(u);
			}
			else if(!moveBack(pred,pc)){
				return isMoved;
			}
				else{
					pred = pc.getPredOf(u);
				}
		}
		return isMoved;
	}
	/*  Pseudo code:
	Unit pred = pc.getPredOf(u);
	boolean isMoved = false;
	while (!pc.getFirst().equals(pred)) {
		if (!(u dependsOn pred)) {
			pc.remove(u);
			pc.insertBefore(u, pred);
			isMoved = true;
		} else {
			if (!moveBack(pred)) {
				return isMoved;
			}
		}
	}
	*/

	private boolean dependsOn(Unit u, Unit pred) {
		return false;
	}
}
