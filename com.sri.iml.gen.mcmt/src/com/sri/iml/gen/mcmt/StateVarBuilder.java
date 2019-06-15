package com.sri.iml.gen.mcmt;

import com.sri.iml.gen.mcmt.model.FormulaVar;
import com.sri.iml.gen.mcmt.model.StateFormulaVariable;
import com.sri.iml.gen.mcmt.model.StateNext;
import com.sri.iml.gen.mcmt.model.StateVariable;

public class StateVarBuilder extends AtomBuilder<StateFormulaVariable>{

	FormulaVar<StateFormulaVariable> formulaVar(StateFormulaVariable var){
		return new FormulaVar<StateFormulaVariable>(var);
	}
	
	FormulaVar<StateFormulaVariable> formulaVar(StateNext which, StateVariable var){
		assert(which == StateNext.State);
		return formulaVar(var);
	}
	
}
