package slicers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.StaticInvokeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGPostDominatorsFinder;
import soot.toolkits.graph.pdg.HashMutablePDG;
import util.DependentsTag;

public class JimpleFutureTagger {

	private Body b;
	private HashMutablePDG pdg;
	private ExceptionalUnitGraph g;
	/**
	 * Tag only Units that are in pc. The units in pc are meant to be
	 * auto-threaded therefore need to be tagged with units they affect
	 * 
	 * @param b
	 *            method body
	 * @param pc
	 *            Units that should be auto-threaded
	 */
	public JimpleFutureTagger(Body b, PatchingChain<Unit> pc) {
		this.b = b;
		initialize();
	}

	public JimpleFutureTagger(Body b) {
		this.b = b;
		initialize();
	}
	private void initialize(){
		g= new ExceptionalUnitGraph(b);
		pdg = new HashMutablePDG(g);
	}
	/**
	 * Tag each assignment of variable with a set of all statements that read
	 * this variable later in the program
	 * 
	 * @param pc
	 *            The statements we want to tag, i.e. find out who depends on.
	 *            The method calls we want to auto-thread.
	 */
	public void onePassTag(PatchingChain<Unit> pc){
		Map<Local,Set<Unit>> localsOfInterest = new HashMap<Local,Set<Unit>>();//variable to defining unit
		List<ValueBox> useBoxes;
		
		//calculate dominator relations
		MHGPostDominatorsFinder postDomFinder = new MHGPostDominatorsFinder(g);
		
		for(Unit u:b.getUnits()){
			if(pc.contains(u) && u instanceof AssignStmt){//def locals are of interest here
				AssignStmt stmt = (AssignStmt) u;
				if (stmt.containsInvokeExpr()
						&& (stmt.getInvokeExpr() instanceof StaticInvokeExpr)
						&& stmt.getInvokeExpr().getArgCount() == 0
						&& !stmt.getInvokeExpr().getMethod().isNative()
						&& !b.getMethod().getName().equals(SootMethod.staticInitializerName)){
					assert pc.getSuccOf(u) instanceof AssignStmt;
					Value defVal = ((AssignStmt) pc.getSuccOf(u)).getLeftOp();
					if(defVal instanceof Local){
						Local l = (Local)defVal;
						if(!localsOfInterest.containsKey(l))
							localsOfInterest.put(l, new HashSet<Unit>());
						Set<Unit> preDefUnits = localsOfInterest.get(l);
						
						//destructive only of u is post-dominator to previous assignment
						for(Unit preDef:preDefUnits)
							if(postDomFinder.isDominatedBy(preDef, u))
								preDefUnits.remove(preDef);
						preDefUnits.add(u);
					}
				}					
			}
			
			useBoxes = u.getUseBoxes();
			if(useBoxes.isEmpty())
				continue;
			
			for(ValueBox useVB:useBoxes){
				Value useVal = useVB.getValue();
				if(useVal instanceof Local){
					Local l = (Local) useVal;
					if(localsOfInterest.keySet().contains(l)){
						Set<Unit> defUnits = localsOfInterest.get(l);
						for(Unit defUnit:defUnits){
							//for cases like def before conditional that defines all
							if(defInAllPaths(defUnit,u,l,localsOfInterest))
								localsOfInterest.get(l).remove(defUnit);
							
							if(!defUnit.hasTag(DependentsTag.name))
								defUnit.addTag(new DependentsTag(useVal, useVal.getType()));
							((DependentsTag) defUnit.getTag(DependentsTag.name)).addDependent(u);
							//System.out.println("Unit "+u+"\n\tdepends on unit\n\t"+defUnit+"\n\twith local "+l);
						}
					}
				}
				else{
					//System.out.println("Non-local use value: "+useVal);
				}
			}
		}
		System.out.println("Done tagging.");
	}
	
	private boolean defInAllPaths(Unit defUnit, Unit u, Local l,Map<Local,Set<Unit>> localsOfInterest) {
		List<Unit> path;
		for(Unit succ:g.getSuccsOf(defUnit)){
			path=null;//TODO no good. find all paths, really. = g.getExtendedBasicBlockPathBetween(succ,u);
			if(!isDefInPath(path,l))
				return false;
		}			
		return true;
	}

	private boolean isDefInPath(List<Unit> path, Local l) {
		if(path==null) return false;
		for(Unit u:path){
			List<ValueBox> defBoxes = u.getDefBoxes();
			for(ValueBox vb:defBoxes)
				if(vb.getValue() == l)
					return true;
		}
		return false;		
	}

	/*
	private void tag(PatchingChain<Unit> pc) {
		List<ValueBox> defBoxes;// all defined locals in a unit
		List<ValueBox> usedBoxes;// all used locals in a unit
		Set<ValueBox> toSkip;// locals that were defined again later on
		List<ValueBox> redefBoxes;// locals defined in defBoxes but also later
									// on
		Set<Local> usedLocals;
		DependentsTag dt;

		for (Unit defUnit : pc) {
			dt = new DependentsTag();
			defUnit.addTag(dt);
			defBoxes = defUnit.getDefBoxes();
			toSkip = new HashSet<ValueBox>();

			for (Unit u : this.b.getUnits()) {
				if (before(u, defUnit))
					continue;// look only ahead..

				// first ignore all locals that are defined here (if not same
				// line)
				if (u != defUnit) {
					redefBoxes = u.getDefBoxes();
					for (ValueBox vb : redefBoxes)
						for (ValueBox db : defBoxes)
							if (db.getValue() == vb.getValue()) {
								System.out
										.println(db.getValue() + " from " + defUnit + " was added to skip after " + u);
								toSkip.add(db);
							}
				}

				usedLocals = new HashSet<Local>();
				usedBoxes = u.getUseBoxes();

				for (ValueBox usedBox : usedBoxes) {
					Object l = usedBox.getValue();
					if (l instanceof Local)
						usedLocals.add((Local) l);
				}
				for (Local l : usedLocals) {
					for (ValueBox vb : defBoxes) {
						if (toSkip.contains(vb)) {
							continue;
						}
						if (vb.getValue() == l)
							dt.addDependent(vb, u);
					}
				}
			}
			/*
			 * if(dt.getDependents().keySet().size()==0) continue;
			 * System.out.println("Unit: "+defUnit+" line "
			 * +defUnit.getJavaSourceStartLineNumber()+" \nDependents: ");
			 * for(ValueBox vb:dt.getDependents().keySet()){ System.out.println(
			 * "\tLocal: "+vb.getValue().toString()); for(Unit
			 * dep:dt.getDependents().get(vb)) System.out.println("\t\t\t"+dep);
			 * }
			 
		}
	}

	private boolean before(Unit u1, Unit u2) {
		return u1.getJavaSourceStartLineNumber() == u2.getJavaSourceStartColumnNumber()
				? u1.getJavaSourceStartColumnNumber() < u2.getJavaSourceStartColumnNumber()
				: u1.getJavaSourceStartLineNumber() < u2.getJavaSourceStartLineNumber();
	}
	*/
}
