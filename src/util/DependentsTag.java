package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Unit;
import soot.ValueBox;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class DependentsTag implements Tag {
	
	public static final String name = "autothreader.util.DependentsTag";
	private Map<ValueBox, Set<Unit>> dependents = new HashMap<ValueBox,Set<Unit>>();
	//each dependency is linked to a local which cause this dependence
	
	public Map<ValueBox, Set<Unit>> getDependents(){
		return dependents;
	}
	
	public void addDependent(ValueBox vb,Unit u){
		Set<Unit> s;
		if(!dependents.containsKey(vb))
			dependents.put(vb, new HashSet<Unit>());
		s = dependents.get(vb);
		s.add(u);
	}
	
	public void removeDependent(ValueBox vb,Unit u){
		if(!dependents.containsKey(vb))
			return;
		this.dependents.get(vb).remove(u);
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
