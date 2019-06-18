package com.sri.iml.gen.mcmt;

import com.sri.iml.gen.mcmt.model.FormulaVar;
import com.sri.iml.gen.mcmt.model.Input;
import com.sri.iml.gen.mcmt.model.MCMT;
import com.sri.iml.gen.mcmt.model.NamedStateFormula;
import com.sri.iml.gen.mcmt.model.NamedStateTransition;
import com.sri.iml.gen.mcmt.model.StateFormulaVariable;
import com.sri.iml.gen.mcmt.model.StateNext;
import com.sri.iml.gen.mcmt.model.StateTransFormulaVariable;
import com.sri.iml.gen.mcmt.model.StateTransVariable;
import com.sri.iml.gen.mcmt.model.StateVariable;

class TransVarBuilder extends AtomBuilder<StateTransFormulaVariable>{

	MCMT mcmt;
	
	TransVarBuilder(MCMT mcmt){
		this.mcmt = mcmt;
	}

	FormulaVar<StateTransFormulaVariable> formulaVar(StateFormulaVariable var) throws GeneratorException {
		if (!(var instanceof Input)) throw new GeneratorException(var.toString() + " should be an input");
		return new FormulaVar<StateTransFormulaVariable>((Input)var);
	}
	
	FormulaVar<StateTransFormulaVariable> formulaVar(StateNext which, StateVariable var){
		return new FormulaVar<StateTransFormulaVariable>(new StateTransVariable(which, var));
	}
	
	FormulaVar<StateTransFormulaVariable> formulaByName(String var) throws GeneratorException {
		NamedStateTransition form = mcmt.getStateTransition(var);
		return new FormulaVar<StateTransFormulaVariable>(form);
	}

}
