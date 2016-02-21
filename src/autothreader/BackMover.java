package autothreader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import util.DirectedGraph;

public class BackMover {
	private static BackMover instance = new BackMover();
	private Map<Unit,DirectedGraph<Value>> depGraphs;
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
		// TODO: consider loops and conditionals
		return false;
		// TODO(Ron): Remove comment out, and fix.
		/*
		if(depGraphs == null){
			depGraphs = new HashMap<Unit,DirectedGraph<Value>>();
			calculateDepGraphs(pc);
		}
		
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
		*/
	}

	private void calculateDepGraphs(PatchingChain<Unit> pc) {
		//TODO consider loops and conditionals
		//TODO NOTE: this does not consider the fact that some assignments are splitted into two stmts
		DirectedGraph<Value> g = new DirectedGraph<Value>();
		for(Unit u:pc){
			//nothing changed, keep same graph as earlier
			if(!(u instanceof AssignStmt)){
				depGraphs.put(u, g.getCopy());
				continue;
			}
			Value defVal = ((AssignStmt) u).getLeftOp();
			g.removeAllEdges(defVal);
			List<ValueBox> useBoxes = u.getUseBoxes();
			
			//defVal depends on nothing
			if(useBoxes.isEmpty()){
				depGraphs.put(u, g.getCopy());
				continue;
			}
			for(ValueBox useVB:useBoxes){
				Value useVal = useVB.getValue();
				g.addEdge(defVal, useVal);
			}
			depGraphs.put(u, g.getCopy());
		}
	}

	private boolean dependsOn(Unit u, Unit pred) {
		if(!(pred instanceof AssignStmt))
			return false;
		Value defVal = ((AssignStmt) pred).getLeftOp();
		List<ValueBox> usedBoxes = u.getUseBoxes();
		for(ValueBox vb:usedBoxes){
			Value usedVal = vb.getValue();
			if(depGraphs.get(u).isReachable(usedVal, defVal))
				return true;
		}
		return false;
	}
}
