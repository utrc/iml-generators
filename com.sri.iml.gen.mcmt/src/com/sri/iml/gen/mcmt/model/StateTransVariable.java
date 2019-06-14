package com.sri.iml.gen.mcmt.model;

// Class for state variables as referred to in a state transition formula,
// i.e. necessarily with a flag indicating whether we consider the variable before the transition of after the transition.
// By making this class implement StateTransFormulaVariable,
// we declare that these variables can be used in a StateTransFormula

public class StateTransVariable implements StateTransFormulaVariable {

	private StateNext which;
	private StateFormulaVariable var;
	
	public StateTransVariable(StateNext which, StateFormulaVariable var) {
		this.which = which;
		this.var = var;
	}
	
	public String toString() {
		return which.toString() + var;
	}
	
}
