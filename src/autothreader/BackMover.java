package autothreader;

import soot.PatchingChain;
import soot.Unit;

public class BackMover {
	private static BackMover instance = new BackMover();
	
	public static BackMover v() {
		return instance;
	}
	
	public void moveBack(Unit u, PatchingChain<Unit> pc) {
		pc.remove(u);
		pc.insertAfter(u, pc.getSuccOf(pc.getFirst()));  // After defining temp$es.

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
	}
}
