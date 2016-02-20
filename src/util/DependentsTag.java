package util;

import java.util.HashSet;
import java.util.Set;

import soot.Unit;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class DependentsTag implements Tag {
	
	public static final String name = "autothreader.util.DependentsTag";
	private Set<Unit> dependents = new HashSet<Unit>();
	//each dependency is linked to a local which cause this dependence
	
	public Set<Unit> getDependents(){
		return dependents;
	}
	
	public void addDependent(Unit u){
		dependents.add(u);
	}
	
	public void removeDependent(Unit u){
		this.dependents.remove(u);
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
