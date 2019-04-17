package com.sri.iml.gen.mcmt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SallySm  {
	
	private String name ;
	private Map<String,SallySymbol> variables ;
	private List<SallySymbol> parameters;
	private Map<String,SallySymbol> definitions;
	private Map<String,SallySymbol> inits;
	private Map<String,SallySymbol> trans ;
	private Map<String,SallySymbol> invar ;
	private SallyModel container ;

	private boolean is_enum ;
	private List<String> literals ;

	
	public SallySm(String name) {	
		variables = new HashMap<String,SallySymbol>();
		parameters = new ArrayList<SallySymbol>();
		definitions = new HashMap<>() ;
		inits = new HashMap<String,SallySymbol>();
		trans = new HashMap<String,SallySymbol>();
		invar = new HashMap<String,SallySymbol>(); 
		this.container = null;
		literals = new ArrayList<>();
		this.name = name ;
	}
	
	public SallySm(SallyModel m, String name) {	
		variables = new HashMap<String,SallySymbol>();
		parameters = new ArrayList<SallySymbol>();
		definitions = new HashMap<>() ;
		inits = new HashMap<String,SallySymbol>();
		trans = new HashMap<String,SallySymbol>();
		invar = new HashMap<String,SallySymbol>(); 
		this.container = m;
		literals = new ArrayList<>();
	}

	public Map<String,SallySymbol> getVariables() {
		return variables;
	}

	public List<SallySymbol> getParameters() {
		return parameters;
	}

	public Map<String, SallySymbol> getDefinitions() {
		return definitions;
	}

	public Map<String, SallySymbol> getInits() {
		return inits;
	}

	public Map<String, SallySymbol> getTrans() {
		return trans;
	}
	
	public Map<String, SallySymbol> getInvar() {
		return invar;
	}

	

	public void setContainer(SallyModel container) {
		this.container = container ;
	}
	
	public SallyModel getContainer() {
		return container;
	}
	
	public boolean hasType(String s) {
		return container.getModules().containsKey(s);
	}
	public SallySm getType(String s) {
		return container.getModules().get(s);
	}
	
	public void addSymbol(SallySymbol s) {
		switch (s.getElementType()) {
			case DEFINE : definitions.put(s.getName(), s); s.setContainer(this); break;			
			case INIT : inits.put(s.getName(), s); s.setContainer(this);break;
			case PARAMETER : parameters.add(s); s.setContainer(this);break;
			case TRANSITION : trans.put(s.getName(), s); s.setContainer(this);break;
			case VAR : variables.put(s.getName(), s); s.setContainer(this);break;
			case INVAR : invar.put(s.getName(), s) ; s.setContainer(this);break;
			default : break;
		} 
	}

	public boolean isEnum() {
		return is_enum;
	}
	
	public void setEnum(boolean e) {
		is_enum = e ;
	}
	
	public List<String> getLiterals(){
		return literals;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasParameter(String name) {
		for(SallySymbol  v : parameters) {
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