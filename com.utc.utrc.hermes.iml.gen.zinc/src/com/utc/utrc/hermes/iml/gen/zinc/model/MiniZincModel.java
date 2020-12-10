package com.utc.utrc.hermes.iml.gen.zinc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.siemens.ct.jmz.ModelBuilder;
import at.siemens.ct.jmz.expressions.integer.IntegerVariable;

public class MiniZincModel extends ModelBuilder {
	
	Map<String,IntegerFunction> functions ;
	Map<String,EnumType> enums ;
	Map<String,EnumVar> enumvars;
	
	public MiniZincModel() {
		super() ;
		functions = new HashMap<String, IntegerFunction>() ;
		enums = new HashMap<>();
		enumvars = new HashMap<>();
	}
	
	public void add(IntegerFunction f) {
	
		functions.put(f.getName(),f);
	}
	
	public Set<String> getFunctionNames(){
		return functions.keySet();
	}
	
	public IntegerFunction getFunction(String name) {
		if (functions.containsKey(name)) {
			return functions.get(name);
		}
		return null;
	}
	
	public Map<String,EnumType> getEnums(){
		return enums;
	}
	
	public Map<String,EnumVar> getEnumVars(){
		return enumvars;
	}

	
	
}
