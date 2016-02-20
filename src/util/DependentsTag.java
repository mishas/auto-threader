package util;

import java.util.HashSet;
import java.util.Set;

import soot.Type;
import soot.Unit;
import soot.Value;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class DependentsTag implements Tag {
	public static final String name = "autothreader.util.DependentsTag";
	
	private final Set<Unit> dependents = new HashSet<Unit>();
	private final Value originalValue;
	private final Type originalType;
	// each dependency is linked to a local which cause this dependence
	
	public DependentsTag(Value originalValue, Type originalType) {
		this.originalValue = originalValue;
		this.originalType = originalType;
	}
	
	public Set<Unit> getDependents(){
		return dependents;
	}
	
	public void addDependent(Unit u){
		dependents.add(u);
	}
	
	public void removeDependent(Unit u){
		this.dependents.remove(u);
	}
	
	public Value getOriginalValue() {
		return originalValue;
	}
	
	public Type getOriginalType() {
		return originalType;
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
