package com.sri.iml.gen.mcmt;


import com.sri.iml.gen.mcmt.model.FormulaVar;
import com.sri.iml.gen.mcmt.model.MCMT;
import com.sri.iml.gen.mcmt.model.NamedStateFormula;
import com.sri.iml.gen.mcmt.model.StateFormulaVariable;
import com.sri.iml.gen.mcmt.model.StateNext;

class StateVarBuilder extends AtomBuilder<StateFormulaVariable>{

	MCMT mcmt;
	
	StateVarBuilder(MCMT mcmt){
		this.mcmt = mcmt;
	}
	
	FormulaVar<StateFormulaVariable> formulaVar(StateFormulaVariable var) {
		assert(var != null);
		return new FormulaVar<StateFormulaVariable>(var);
	}
	
	FormulaVar<StateFormulaVariable> formulaVar(StateNext which, StateFormulaVariable var) throws GeneratorException {
		assert(var != null);
		if (which == StateNext.Next) throw new GeneratorException("This mode should be State, but got Next");
		return formulaVar(var);
	}
	
	FormulaVar<StateFormulaVariable> formulaByName(String var) throws GeneratorException {
		NamedStateFormula form = mcmt.getStateFormula(var);
		if (form == null) throw new GeneratorException("Cannot find "+var+" among the state formulae.");
		return formulaVar(form);
	}
	
	FormulaVar<StateFormulaVariable> formulaByName(StateNext which, String var) throws GeneratorException {
		throw new GeneratorException("This mode should be State, but got Next");
	}

}
