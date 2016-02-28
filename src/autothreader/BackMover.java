package autothreader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;
import util.BlockTag;
import util.DirectedGraph;
import util.LoopTag;

public class BackMover {
	private static BackMover instance = new BackMover();
	private Map<Unit, DirectedGraph<Value>> depGraphs;
	private Collection<Loop> loops;

	public static BackMover v() {
		return instance;
	}

	public void moveToStart(Unit u, PatchingChain<Unit> pc) {
		pc.remove(u);
		pc.insertAfter(u, pc.getSuccOf(pc.getFirst())); // After defining
														// temp$es.
	}

	/**
	 * Bubble-sort-like
	 * 
	 * @param u
	 * @param pc
	 * @return true iff u is moved back
	 */
	public boolean moveBack(Unit u, PatchingChain<Unit> pc, Collection<Loop> loops) {
		this.loops = loops;
		LoopNestTree lnt = new LoopNestTree(loops);

		// if (depGraphs == null) {
		depGraphs = new HashMap<Unit, DirectedGraph<Value>>();
		calculateDepGraphs(pc);

		boolean isMoved = false;
		HashSet<Unit> cache = new HashSet<Unit>();
		Unit pred = pc.getPredOf(u);

		while (pred != null && !pc.getSuccOf(pc.getFirst()).equals(pred) && !cache.contains(pred)) {
			
			BlockTag uBlockTag = ((BlockTag) u.getTag(BlockTag.name));
			BlockTag predBlockTag = ((BlockTag) pred.getTag(BlockTag.name));
			/*
			 * LoopTag uTag = ((LoopTag) u.getTag(LoopTag.name));
			LoopTag predTag = ((LoopTag) pred.getTag(LoopTag.name));if (uTag != null || predTag != null) {
				if (predTag == null) {
					return isMoved;// not getting out of loop
				}
				// predTag!=null
				Loop predLoop = predTag.getLoop();
				if (uTag != null && uTag.getLoop() != predLoop || uTag == null) {
					//if (dependsOn(u, pred))
						return isMoved;// depends on something along the inner
										// loop above-not sure about it
					//Unit beforePredsLoop = pc.getPredOf(predLoop.getHead());
					//pred = beforePredsLoop;// jump over the loop
					//continue;
				}
					// uLoop=predLoop
					if (predLoop.getHead() == pred) // pred is the condition
						return isMoved;
			}
			if (uIfTag != null || predIfTag != null) {
				if (predIfTag == null) {
					return isMoved;// not getting out of conditional
				}
				// predIfTag!=null
				Block predBlock = predIfTag.getBlock();
				if (uIfTag != null && uIfTag.getBlock() != predBlock || uIfTag == null) {
						//if (dependsOn(u, pred))
							return isMoved;// depends on something along the
											// conditional-not sure about it
						//Unit beforePredsIf = pc.getPredOf(predBlock.getHead());
						//pred = beforePredsIf;// jump over the conditional
						//continue;
				}
					// uIfTag=predIfTag
					if (predBlock.getHead() == pred) // pred is the condition
						return isMoved;
				}*/
			//check stays in the same block
			if(uBlockTag == null || predBlockTag == null ||uBlockTag.getBlock()!=predBlockTag.getBlock())
				return isMoved;
			if (!dependsOn(u, pred) && !dependsOn(pred, u) && !initOf(pred, u)) {
				pc.remove(u);
				pc.insertBefore(u, pred);
				isMoved = true;
				cache = new HashSet<Unit>();
			} else if (!moveBack(pred, pc, loops)) {
				return isMoved;
			} else {
				cache.add(pred);
			}
			pred = pc.getPredOf(u);
		}
		return isMoved;
	}

	/**
	 * 
	 * @param pred
	 * @param u
	 * @return true if pred contains init for one of u's used values
	 */
	private boolean initOf(Unit pred, Unit u) {
		if (!(pred instanceof InvokeStmt) || !(((InvokeStmt) pred).getInvokeExpr() instanceof SpecialInvokeExpr))
			return false;
		SpecialInvokeExpr se = (SpecialInvokeExpr) ((InvokeStmt) pred).getInvokeExpr();
		for (ValueBox vb : u.getUseBoxes()) {
			if (se.getBase() == vb.getValue())
				return true;
		}
		return false;
	}

	private void calculateDepGraphs(PatchingChain<Unit> pc) {
		// TODO consider loops and conditionals
		DirectedGraph<Value> g = new DirectedGraph<Value>();
		DirectedGraph<Value> invGraph;
		HashSet<Unit> skipStmt = new HashSet<>();

		for (Unit u : pc) {
			invGraph = null;
			if (skipStmt.contains(u))
				continue;
			for (Loop l : loops) {
				if (l.getHead().equals(u)) {
					invGraph = invariantGraphForLoop(g, l);
					skipStmt.addAll(l.getLoopStatements());
					break;
				}
			}
			if (invGraph != null) {
				g = DirectedGraph.join(g, invGraph);
			} else {
				updateGraph(u, g);
			}
			depGraphs.put(u, g.getCopy());
		}
	}

	private void updateGraph(Unit u, DirectedGraph<Value> g) {
		// nothing changed, keep same graph as earlier
		if (!(u instanceof AssignStmt)) {
			depGraphs.put(u, g.getCopy());
			return;
		}
		Value defVal = ((AssignStmt) u).getLeftOp();
		g.removeAllEdges(defVal);
		List<ValueBox> useBoxes = u.getUseBoxes();

		// defVal depends on nothing
		if (useBoxes.isEmpty()) {
			depGraphs.put(u, g.getCopy());
			return;
		}
		for (ValueBox useVB : useBoxes) {
			Value useVal = useVB.getValue();
			g.addEdge(defVal, useVal);
		}
	}

	/**
	 * 
	 * @param stmtList
	 * @param g
	 * @return invariant graph of loop
	 */
	private DirectedGraph<Value> invariantGraphForLoop(DirectedGraph<Value> g, Loop currLoop) {
		List<Stmt> stmtList = currLoop.getLoopStatements();
		List<DirectedGraph<Value>> cache = new ArrayList<>();
		DirectedGraph<Value> inv = g.getCopy();
		HashSet<Unit> skipStmt = new HashSet<>();

		for (Stmt s : stmtList) {
			if (s == currLoop.getBackJumpStmt()) {
				depGraphs.put(s, inv.getCopy());
				s.addTag(new LoopTag(currLoop));
				break;
			}
			if (skipStmt.contains(s))
				continue;
			for (Loop l : loops) {
				if (l != currLoop && l.getHead().equals(s)) {
					skipStmt.addAll(l.getLoopStatements());
					inv = DirectedGraph.join(inv, invariantGraphForLoop(inv, l));
					continue;
				}
			}
			if (currLoop.getLoopExits().contains(s))
				cache.add(inv.getCopy());
			else
				updateGraph(s, inv);
			s.addTag(new LoopTag(currLoop));
			depGraphs.put(s, inv.getCopy());
		}
		for (DirectedGraph<Value> c : cache)
			inv = DirectedGraph.join(inv, c);
		return inv;
	}

	private boolean dependsOn(Unit u, Unit pred) {
		if (!(pred instanceof AssignStmt))
			return false;
		Value defVal = ((AssignStmt) pred).getLeftOp();
		List<ValueBox> usedBoxes = u.getUseBoxes();
		for (ValueBox vb : usedBoxes) {
			Value usedVal = vb.getValue();
			if (depGraphs.get(u) != null) {
				if (depGraphs.get(u).isReachable(usedVal, defVal))
					return true;
			} else
				System.out.println(1);// prevented by calculating depGraphs
										// every call to moveBack

		}
		return false;
	}
}
