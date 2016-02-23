package util;

import soot.jimple.toolkits.annotation.logic.Loop;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class LoopTag implements Tag {
	public static final String name = "autothreader.util.LoopTag";
	
	private final Loop l;
	
	public LoopTag(Loop l) {
		this.l=l;
	}
	public Loop getLoop(){
		return l;
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
