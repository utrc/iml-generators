package com.sri.iml.gen.mcmt;

import com.sri.iml.gen.mcmt.model.FormulaVar;
import com.sri.iml.gen.mcmt.model.Input;
import com.sri.iml.gen.mcmt.model.StateFormulaVariable;
import com.sri.iml.gen.mcmt.model.StateNext;
import com.sri.iml.gen.mcmt.model.StateTransFormulaVariable;
import com.sri.iml.gen.mcmt.model.StateTransVariable;
import com.sri.iml.gen.mcmt.model.StateVariable;

class TransVarBuilder extends AtomBuilder<StateTransFormulaVariable>{

	FormulaVar<StateTransFormulaVariable> formulaVar(StateFormulaVariable var){
		assert(var instanceof Input);
		return new FormulaVar<StateTransFormulaVariable>((Input)var);
	}
	
	FormulaVar<StateTransFormulaVariable> formulaVar(StateNext which, StateVariable var){
		return new FormulaVar<StateTransFormulaVariable>(new StateTransVariable(which, var));
	}
	
}
