package slicers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import util.DirectedGraph;

public class SimpleJimpleSlicer {

	private Map<Unit, List<Unit>> stmtToSlicedSet = new HashMap<>();
	private Body b;

	public SimpleJimpleSlicer(Body b) {
		this.b = b;
		slice();
	}

	private void slice() {
		System.out.println("Slicing.");
		
		DirectedGraph<Value> g = new DirectedGraph<>();
		PatchingChain<Unit> pc = this.b.getUnits();
		List<ValueBox> used;
		List<ValueBox> defined;
		Value defVal;
		Value usedVal;
		//TODO  this is not correct yet-
		//TODO  must handle loops and conditionals correctly and maybe procedure inline
		//TODO  destructive update: remove edges when needed
		//TODO  
		for (Unit u : pc) {
			used = u.getUseBoxes();
			defined = u.getDefBoxes();
			
			for (ValueBox vbd : defined){
				defVal = vbd.getValue();
				g.addVertex(defVal);
				for (ValueBox vbu : used){
					//TODO check if method, if so take only arguments recursively using a help method
					usedVal = vbu.getValue();
					g.addVertex(usedVal);
					g.addEdge(defVal, usedVal);
				}
			}
		}
		System.out.println(g.toString());
		System.out.println("Done slicing.");
	}
}
