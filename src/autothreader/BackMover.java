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
	}
}
