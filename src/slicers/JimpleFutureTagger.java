package slicers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.Unit;
import soot.ValueBox;
import util.DependentsTag;

public class JimpleFutureTagger {

	private Body b;

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
		tag(pc);
	}

	public JimpleFutureTagger(Body b) {
		this.b = b;
		tag(this.b.getUnits());
	}

	/**
	 * Tag each assignment of variable with a set of all statements that read
	 * this variable later in the program
	 * 
	 * @param pc
	 *            The statements we want to tag, i.e. find out who depends on.
	 *            The method calls we want to auto-thread.
	 */
	// TODO address loops, conditionals
	// TODO think different in order to improve time- hold list of defined
	// locals, each local points to the defining unit. for every line update the
	// relevant defining unit if needed and tag according to used locals and
	// defining units. build the same tag
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
			 */
		}
	}

	private boolean before(Unit u1, Unit u2) {
		return u1.getJavaSourceStartLineNumber() == u2.getJavaSourceStartColumnNumber()
				? u1.getJavaSourceStartColumnNumber() < u2.getJavaSourceStartColumnNumber()
				: u1.getJavaSourceStartLineNumber() < u2.getJavaSourceStartLineNumber();
	}
}
