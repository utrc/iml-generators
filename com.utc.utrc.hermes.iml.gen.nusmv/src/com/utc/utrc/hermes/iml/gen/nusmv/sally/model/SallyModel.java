package com.utc.utrc.hermes.iml.gen.nusmv.sally.model;

import java.util.HashMap;
import java.util.Map;

import com.utc.utrc.hermes.iml.gen.nusmv.systems.Connection;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.sexpr.SExpr;

public class SallyModel {
	
	private SallyState state ;
	private Map<String,SExpr> init ;
	private Map<String,SExpr> transition ;
	private Map<String,SExpr> statedef; 	
	private Map<SallySymbol,SallySymbol> equivalence ;
	
	
	private ImlStdLib stdLibs ;
	
	public SallyModel(ImlStdLib stdLibs) {
		this.stdLibs = stdLibs ;
		state = new SallyState(stdLibs);
		init = new HashMap<String, SExpr>();
		transition = new HashMap<String, SExpr>();
		statedef = new HashMap<String, SExpr>();
		
	}
	
	public void addState(String prefix, SymbolDeclaration s) {
		state.addVariable(prefix, s);
	}
	
	public void addInit(String prefix, SExpr i) {
		init.put(prefix, i) ;
	}
	public void addTransition(String prefix, SExpr i) {
		transition.put(prefix,i);
	}
	
	public void addDefinition(String prefix, SExpr i) {
		statedef.put(prefix,i);
	}
	
	public void addEquivalence(SallySymbol s, SallySymbol t) {
		equivalence.put(s, t);
	}

	public SallyState getState() {
		return state;
	}

	public Map<String, SExpr> getInit() {
		return init;
	}

	public Map<String, SExpr> getTransition() {
		return transition;
	}
	public Map<String, SExpr> getDefinitions() {
		return statedef;
	}

	public Map<SallySymbol, SallySymbol> getConnections() {
		return equivalence;
	}

	@Override
	public String toString() {
		String retval = state.toString();
		retval += "INIT \n" ;
		for(String i : init.keySet()) {
			retval += i + " := " + init.get(i).toString() + "\n";
		}
		retval += "TRANSITION \n" ;
		for(String i : transition.keySet()) {
			retval += i + " := " + transition.get(i).toString() + "\n";
		}
		return retval;
	}
	
	
	
	
}
