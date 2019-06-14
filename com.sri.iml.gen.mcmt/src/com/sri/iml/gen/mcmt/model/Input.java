package com.sri.iml.gen.mcmt.model;

// Class for input variables.
// By making it implement StateTransFormulaVariable,
// we declare that these variables can be used in a StateTransFormula,
// and similarly for StateFormulaVariable
public class Input extends Variable implements StateTransFormulaVariable, StateFormulaVariable {
	
	public Input(String s, BaseType ty){
		super(s, ty);
	}

}
