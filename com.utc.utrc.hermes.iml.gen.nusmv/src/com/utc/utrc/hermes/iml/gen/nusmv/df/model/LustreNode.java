package com.utc.utrc.hermes.iml.gen.nusmv.df.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;

public class LustreNode extends LustreType {
	
	private Map<String,LustreSymbol> variables ;
	private List<LustreSymbol> parameters;
	private List<LustreSymbol> returns;
	private Map<String,LustreSymbol> lets;
	
	
	public LustreNode(String name) {
		super(name);
		variables = new HashMap<String,LustreSymbol>();
		parameters = new ArrayList<LustreSymbol>();
		returns = new ArrayList<LustreSymbol>();
		lets = new HashMap<>() ;
		this.container = null;
	}
	
	public LustreNode(LustreModel m, String name) {	
		super(name);
		variables = new HashMap<String,LustreSymbol>();
		parameters = new ArrayList<LustreSymbol>();
		lets = new HashMap<>() ;
		this.container = m;
	}

	public Map<String,LustreSymbol> getVariables() {
		return variables;
	}

	public List<LustreSymbol> getParameters() {
		return parameters;
	}
	public List<LustreSymbol> getReturns() {
		return returns;
	}

	public Map<String, LustreSymbol> getLets() {
		return lets;
	}

	

	public void setContainer(LustreModel container) {
		this.container = container ;
	}
	
	public LustreModel getContainer() {
		return container;
	}
	
	public boolean hasType(String s) {
		return container.getNodes().containsKey(s);
	}
	public LustreNode getType(String s) {
		return container.getNodes().get(s);
	}
	
	public void addSymbol(LustreSymbol s) {
		switch (s.getElementType()) {
			case LET : lets.put(s.getName(), s); s.setContainer(this); break;			
			case PARAMETER : parameters.add(s); s.setContainer(this);break;
			case RETURN : returns.add(s); s.setContainer(this);break;
			case VAR : variables.put(s.getName(), s); s.setContainer(this);break;
			case FIELD : addField(s.getName(), s);
			default : break;
		} 
	}

	public boolean hasParameter(String name) {
		for(LustreSymbol  v : parameters) {
			if (v.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public int paramIndex(String name) {
		for(int index = 0 ; index < parameters.size() ; index++) {
			if (parameters.get(index).getName().equals(name)) {
				return index;
			}
		}
		return -1 ;
	}	
	
	
	
}
