package slicers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import util.DirectedGraph;

public class SimpleJimpleSlicer {

	private Map<Unit, List<Unit>> stmtToSlicedSet = new HashMap<>();
	private Body b;

	public SimpleJimpleSlicer(Body b) {
		this.b = b;
		for(Unit u:b.getUnits())
			if(u instanceof InvokeStmt)
				slice(u);
	}

	private void slice(Unit u) {
		System.out.println("SLICING on "+u);
		HashMutablePDG pdg = new HashMutablePDG(new ExceptionalUnitGraph(b));
		BlockGraph bg = pdg.getBlockGraph();
		UnitGraph cfg = pdg.getCFG();
		Unit a=cfg.getTails().get(0);
		Set<Unit> delta ,preds = new HashSet<Unit>();
		preds.add(u);
		preds.addAll(cfg.getPredsOf(u));
		boolean changed = true;
		int size=0;
		while(changed == true){
			changed=false;
			delta= new HashSet<Unit>();
			for(Unit u2:preds){
				delta.addAll(cfg.getPredsOf(u2));
			}
			size = preds.size();
			preds.addAll(delta);
			if(size<preds.size())
				changed=true;			
		}
		System.out.println("preds: "+preds);
		System.out.println("Slicing done.");
	}
}
