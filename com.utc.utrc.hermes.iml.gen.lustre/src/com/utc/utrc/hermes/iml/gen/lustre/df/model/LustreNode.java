/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.lustre.df.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LustreNode extends LustreType {
	
	private Map<String,LustreSymbol> variables ;
	private List<LustreSymbol> parameters;
	private List<LustreSymbol> returns;
	private Map<String, LustreSymbol> components;
	private Map<String,LustreSymbol> lets;
	
	public LustreNode(String name) {
		super(name);
		variables = new LinkedHashMap<String,LustreSymbol>();
		components = new LinkedHashMap<String,LustreSymbol>();
		parameters = new ArrayList<LustreSymbol>();
		returns = new ArrayList<LustreSymbol>();
		lets = new LinkedHashMap<>() ;
		this.container = null;
	}
	
	public LustreNode(LustreModel m, String name) {	
		super(name);
		variables = new LinkedHashMap<String,LustreSymbol>();
		parameters = new ArrayList<LustreSymbol>();
		lets = new LinkedHashMap<>() ;
		this.container = m;
	}

	public Map<String,LustreSymbol> getVariables() {
		return variables;
	}

	public List<LustreSymbol> getParameters() {
		return parameters;
	}

	public Map<String,LustreSymbol> getComponents() {
		return components;
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
			case ASSERTION : // put assertions in lets
			case LET : lets.put(s.getName(), s); s.setContainer(this); break;			
			case PARAMETER : parameters.add(s); s.setContainer(this);break;
			case RETURN : returns.add(s); s.setContainer(this);break;
			case VAR : variables.put(s.getName(), s); s.setContainer(this);break;
			case FIELD : addField(s.getName(), s);s.setContainer(this);break;
			case COMPONENT: components.put(s.getName(),s);s.setContainer(this);break;
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


	public boolean hasReturn(String name) {
		for(LustreSymbol  v : returns) {
			if (v.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public int returnIndex(String name) {
		for(int index = 0 ; index < returns.size() ; index++) {
			if (returns.get(index).getName().equals(name)) {
				return index;
			}
		}
		return -1 ;
	}		
	
}
