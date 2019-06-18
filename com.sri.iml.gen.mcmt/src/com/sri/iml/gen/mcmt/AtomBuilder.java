package com.sri.iml.gen.mcmt;

import com.sri.iml.gen.mcmt.model.FormulaAtom;
import com.sri.iml.gen.mcmt.model.FormulaSymb;
import com.sri.iml.gen.mcmt.model.FormulaVar;
import com.sri.iml.gen.mcmt.model.Sexp;
import com.sri.iml.gen.mcmt.model.Sexp_atom;
import com.sri.iml.gen.mcmt.model.StateFormulaVariable;
import com.sri.iml.gen.mcmt.model.StateNext;
import com.sri.iml.gen.mcmt.model.StateVariable;

abstract class AtomBuilder<V> {

	abstract FormulaVar<V> formulaVar(StateFormulaVariable var) throws GeneratorException ;
	abstract FormulaVar<V> formulaVar(StateNext which, StateVariable var) throws GeneratorException ;
	abstract FormulaVar<V> formulaByName(String var) throws GeneratorException ;
	
	Sexp<FormulaAtom<V>> variable(StateFormulaVariable var) throws GeneratorException {
		assert(var != null);
		return new Sexp_atom<FormulaAtom<V>>(formulaVar(var));
	}
	
	Sexp<FormulaAtom<V>> variable(StateNext which, StateVariable var) throws GeneratorException {
		return new Sexp_atom<FormulaAtom<V>>(formulaVar(which,var));
	}

	Sexp<FormulaAtom<V>> variable(String var) throws GeneratorException {
		assert(var != null);
		return new Sexp_atom<FormulaAtom<V>>(formulaByName(var));
	}

	Sexp<FormulaAtom<V>> symbol(String symb) throws GeneratorException {
		FormulaAtom<V> symbol = new FormulaSymb<V>(symb);
		return new Sexp_atom<FormulaAtom<V>>(symbol);		
	}
	
	
	
}
