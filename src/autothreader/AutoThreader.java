package autothreader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import slicers.JimpleFutureTagger;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.toolkits.scalar.pre.SootFilter;

public class AutoThreader {
	public static final void main(String[] args) throws IOException {
		String srcPath = System.getProperty("os.name").toLowerCase().contains("windows") ? 
				";.\\src" : ":./src"; //for the sake of development comfort..
		
		Scene.v().setSootClassPath(Scene.v().defaultClassPath() + srcPath);
		
		SootClass c = Scene.v().loadClassAndSupport("test.Test1");
		Scene.v().addBasicClass("java.util.concurrent.ExecutorService", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.util.concurrent.Executors", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.util.concurrent.Future", SootClass.SIGNATURES);
		Scene.v().loadNecessaryClasses();
		c.setApplicationClass();

		if (!c.declaresMethodByName(SootMethod.staticInitializerName)) {
			c.addMethod(Utils.v().createClinit());
		}
		c.addField(Utils.v().createEsField());
		
		PackManager.v().getPack("jtp").add(new Transform("jtp.Threadalizer", new Transformer()));
		PackManager.v().runPacks();
        PackManager.v().writeOutput();
	}
	
	public static class Transformer extends BodyTransformer {
		@Override
		protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
			PatchingChain<Unit> pc = b.getUnits();
			
			JimpleFutureTagger jft = new JimpleFutureTagger(b);
			jft.onePassTag(pc);

			if (SootMethod.staticInitializerName.equals(b.getMethod().getName())) {
				Utils.v().addExecutorField(b, pc);
			}
			
			Local esLocal = null;
			if ("main".equals(b.getMethod().getName())) {
				esLocal = Utils.v().addEsLocal(b, pc);
				// TODO: What if we don't have a main method in this class?
				Utils.v().addFinalizer(b, esLocal, pc);
			}
			
			for (Unit u : new LinkedList<Unit>(pc)) {
				// TODO(Ron): This needs to be done only to lines which needs to be extracted into threads.
				if (u instanceof AssignStmt) {
					AssignStmt stmt = (AssignStmt) u;
					if (stmt.containsInvokeExpr()
							&& (stmt.getInvokeExpr() instanceof StaticInvokeExpr)
							&& stmt.getInvokeExpr().getArgCount() == 0
							&& !stmt.getInvokeExpr().getMethod().isNative()
							&& !b.getMethod().getName().equals(SootMethod.staticInitializerName)) {
						if (stmt.getInvokeExpr().getArgCount() == 0) {
							System.out.println(b.getMethod().getName() + "   " + u);
							if (esLocal == null) {
								esLocal = Utils.v().addEsLocal(b, pc);
							}
							Utils.v().toThread(b, u, esLocal, pc);
						}
					}
				}
				
				// TODO(Ron): This needs to be done to any usage of the variable that is now a Future.
				if (u instanceof InvokeStmt)  {
					InvokeExpr invokeExpr = ((InvokeStmt) u).getInvokeExpr();
					for (Value v : invokeExpr.getArgs()) {
						if (SootFilter.isLocal(v) && (v.toString().equals("a") || v.toString().equals("b"))) {
							Utils.v().fixCallsite(b, u, v, Scene.v().getType("java.lang.Integer"), pc);
						}
					}
				}
			}
			
			System.out.println("Done processing " + b.getMethod());
		}
	}
}
