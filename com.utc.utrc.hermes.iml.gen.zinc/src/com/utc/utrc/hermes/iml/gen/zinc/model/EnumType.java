package com.utc.utrc.hermes.iml.gen.zinc.model;

import java.util.HashMap;
import java.util.Map;

import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGeneratorException;

import at.siemens.ct.jmz.elements.Element;


public class EnumType implements Element {
	Map<Integer,String> literals;
	String name ;
	public EnumType(String name) {
		this.name = name;
		literals = new HashMap<>();
	}
	public Map<Integer,String> getLiterals(){
		return literals;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLiteralName(int i) throws MiniZincGeneratorException {
		if (literals.containsKey(i)) {
			return literals.get(i);
		}else {
			throw new MiniZincGeneratorException(Integer.toString(i) + " is not related to any literal") ;
		}
	}
	
	public Integer getIntegerForLiteral(String literal) throws MiniZincGeneratorException {
		for(Integer i : literals.keySet()) {
			if (literals.get(i).equals(literal)) {
				return i ;
			}
		}
		throw new MiniZincGeneratorException("Literal " + literal + " is not in type " + name);
	}
	
	@Override
	public String declare() {
		StringBuilder retval = new StringBuilder();
		retval.append("set of int : " + getName() + " = 0.." + Integer.toString(literals.size() - 1) + " ; \n") ;
		return retval.toString();
	}
}
