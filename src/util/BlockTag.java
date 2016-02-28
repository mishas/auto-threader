package util;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;
import soot.toolkits.graph.Block;

public class BlockTag implements Tag{
	public static final String name = "autothreader.util.IfThenElseTag";
	
	private final Block block;
	
	public BlockTag(Block block) {
		this.block = block;
	}
	
	public Block getBlock(){
		return block;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

}
