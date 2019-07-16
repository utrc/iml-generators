package com.sri.iml.gen.mcmt.model;

public class StateVariable extends Variable implements StateFormulaVariable {

	public StateVariable(String name, BaseType ty) {
		super(name, ty);
	}
	
	public StateTransFormulaVariable convert(StateNext which) {
		return new StateTransVariable(which, this);
	}
	
}
