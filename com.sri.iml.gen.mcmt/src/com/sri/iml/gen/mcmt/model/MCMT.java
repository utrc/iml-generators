package com.sri.iml.gen.mcmt.model;

import java.util.Collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MCMT  {

	/* We have tables for named things, for quick access */
	
	private Map<String,NamedStateType>    statetypes;
	private Map<String,NamedStateFormula> stateformulas;
	private Map<String,NamedStateTransition> statetransitions;
	private Map<String,NamedTransitionSystem> statetransitionsystems;
	
	private Collection<Query> queries;

	// ... but we also record them in the order we were given them
	private LinkedList<Instruction> instructions;
	
	
	
	public MCMT() {
		statetypes    = new HashMap<String,NamedStateType>();
		stateformulas = new HashMap<String,NamedStateFormula>();
		statetransitions = new HashMap<String,NamedStateTransition>();
		statetransitionsystems = new HashMap<String,NamedTransitionSystem>();
		queries       = new HashSet<Query>();
		instructions  = new LinkedList<Instruction>();
	}
	
	public void addStateType(NamedStateType statetype) {
		statetypes.put(statetype.toString(),statetype); 
		instructions.addLast(statetype); 
	}

	public void addStateFormula(NamedStateFormula formula) {
		stateformulas.put(formula.toString(),formula); 
		instructions.addLast(formula); 
	}

	public void addStateTransition(NamedStateTransition transition) {
		statetransitions.put(transition.toString(),transition); 
		instructions.addLast(transition); 
	}

	public void addTransitionSystem(NamedTransitionSystem system) {
		statetransitionsystems.put(system.toString(),system); 
		instructions.addLast(system); 
	}

	public NamedStateType getStateType(String name) {
		return statetypes.get(name); 
	}

	public NamedStateFormula getStateFormula(String name) {
		return stateformulas.get(name); 
	}

	public NamedStateTransition getStateTransition(String name) {
		return statetransitions.get(name); 
	}

	public NamedTransitionSystem getTransitionSystem(String name) {
		return statetransitionsystems.get(name); 
	}

	public void addQuery(Query query) {
		queries.add(query); 
		instructions.addLast(query); 
	}
	
	public LinkedList<Sexp<String>> toSexps(){
		LinkedList<Sexp<String>> result = new LinkedList<Sexp<String>>(); 
		for (Instruction i : instructions) {
			result.addLast(i.toSexp());
		}
		return result;
	}

	public String toString(){
		StringBuilder result = new StringBuilder();
		for (Instruction i : instructions) {
			result.append(i.toSexp().toString());
			result.append("\n");
		}
		return result.toString();
	}
	
//	private String name ;
//	private Map<String,SallySymbol> variables ;
//	private List<SallySymbol> parameters;
//	private Map<String,SallySymbol> definitions;
//	private Map<String,SallySymbol> inits;
//	private Map<String,SallySymbol> trans ;
//	private Map<String,SallySymbol> invar ;
//	private SallyModel container ;
//
//	private boolean is_enum ;
//	private List<String> literals ;
//
//	
//	public SallySm(String name) {	
//		variables = new HashMap<String,SallySymbol>();
//		parameters = new ArrayList<SallySymbol>();
//		definitions = new HashMap<>() ;
//		inits = new HashMap<String,SallySymbol>();
//		trans = new HashMap<String,SallySymbol>();
//		invar = new HashMap<String,SallySymbol>(); 
//		this.container = null;
//		literals = new ArrayList<>();
//		this.name = name ;
//	}
//	
//	public SallySm(SallyModel m, String name) {	
//		variables = new HashMap<String,SallySymbol>();
//		parameters = new ArrayList<SallySymbol>();
//		definitions = new HashMap<>() ;
//		inits = new HashMap<String,SallySymbol>();
//		trans = new HashMap<String,SallySymbol>();
//		invar = new HashMap<String,SallySymbol>(); 
//		this.container = m;
//		literals = new ArrayList<>();
//	}
//
//	public Map<String,SallySymbol> getVariables() {
//		return variables;
//	}
//
//	public List<SallySymbol> getParameters() {
//		return parameters;
//	}
//
//	public Map<String, SallySymbol> getDefinitions() {
//		return definitions;
//	}
//
//	public Map<String, SallySymbol> getInits() {
//		return inits;
//	}
//
//	public Map<String, SallySymbol> getTrans() {
//		return trans;
//	}
//	
//	public Map<String, SallySymbol> getInvar() {
//		return invar;
//	}
//
//	
//
//	public void setContainer(SallyModel container) {
//		this.container = container ;
//	}
//	
//	public SallyModel getContainer() {
//		return container;
//	}
//	
//	public boolean hasType(String s) {
//		return container.getModules().containsKey(s);
//	}
//	public SallySm getType(String s) {
//		return container.getModules().get(s);
//	}
//	
//	public void addSymbol(SallySymbol s) {
//		switch (s.getElementType()) {
//			case DEFINE : definitions.put(s.getName(), s); s.setContainer(this); break;			
//			case INIT : inits.put(s.getName(), s); s.setContainer(this);break;
//			case PARAMETER : parameters.add(s); s.setContainer(this);break;
//			case TRANSITION : trans.put(s.getName(), s); s.setContainer(this);break;
//			case VAR : variables.put(s.getName(), s); s.setContainer(this);break;
//			case INVAR : invar.put(s.getName(), s) ; s.setContainer(this);break;
//			default : break;
//		} 
//	}
//
//	public boolean isEnum() {
//		return is_enum;
//	}
//	
//	public void setEnum(boolean e) {
//		is_enum = e ;
//	}
//	
//	public List<String> getLiterals(){
//		return literals;
//	}
//	
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//	
//	public boolean hasParameter(String name) {
//		for(SallySymbol  v : parameters) {
//			if (v.getName().equals(name)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public int paramIndex(String name) {
//		for(int index = 0 ; index < parameters.size() ; index++) {
//			if (parameters.get(index).getName().equals(name)) {
//				return index;
//			}
//		}
//		return -1 ;
//	}	
//	
//	
	
}
