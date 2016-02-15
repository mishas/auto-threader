package autothreader;

import soot.Body;
import soot.PatchingChain;
import soot.Unit;

public class JimpleFutureTagger {

	private Body b;

	public JimpleFutureTagger(Body b) {
		this.b = b;
		tag();
	}

	/**
	 * Tag each assignment of variable with a set of all statements that read
	 * this variable later in the program
	 * 
	 */
	private void tag() {
		PatchingChain<Unit> pc = this.b.getUnits();
		
	}
}
