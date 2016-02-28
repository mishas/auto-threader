package util;

import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class BlockTagger {
	public static void tag(Body b) {
		BlockGraph bg = new ExceptionalBlockGraph(new ExceptionalUnitGraph(b));
		List<Block> bs = bg.getBlocks();
		for (Block block : bs) {
			Iterator<Unit> iter = block.iterator();
			Unit u ;
			while (iter.hasNext()) {

				u = iter.next();
				u.addTag(new BlockTag(block));
			}
		}
	}
}
