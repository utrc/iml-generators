package com.sri.iml.gen.mcmt.model;

import java.util.Iterator;

public class NamedStateFormula extends Named implements Instruction, StateFormulaVariable {

	private NamedStateType statetype;
	private Sexp<FormulaAtom<StateFormulaVariable>> formula;
	
	public NamedStateFormula(String name, NamedStateType statetype, Sexp<FormulaAtom<StateFormulaVariable>> formula) {
		super(name);
		this.statetype = statetype;
		this.formula = formula;		
	}
	
	public Sexp<FormulaAtom<StateFormulaVariable>> getFormula() {
		return formula;
	}

	public Sexp<String> toSexp() {
		Sexp_list<String> result = new Sexp_list<String>();
		result.addRight(new Sexp_atom<String>("define-states"));
		result.addRight(new Sexp_atom<String>(toString()));
		result.addRight(new Sexp_atom<String>(statetype.toString()));
		result.addRight(formula.toSexp());
		return result;
	}

	private static Sexp<FormulaAtom<StateTransFormulaVariable>> convert(StateNext which, Sexp<FormulaAtom<StateFormulaVariable>> form){
		if (form instanceof Sexp_atom) {
			FormulaAtom<StateTransFormulaVariable> newatom = null; 
			FormulaAtom<StateFormulaVariable> atom = ((Sexp_atom<FormulaAtom<StateFormulaVariable>>) form).getAtom();
			if (atom instanceof FormulaSymb) {
				newatom = new FormulaSymb<StateTransFormulaVariable>(((FormulaSymb<StateFormulaVariable>) atom).getSymbol());
			}
			if (atom instanceof FormulaVar) {
				newatom = new FormulaVar<StateTransFormulaVariable>(((FormulaVar<StateFormulaVariable>) atom).getVar().convert(which));
			}
			return new Sexp_atom<FormulaAtom<StateTransFormulaVariable>>(newatom);
		}
		else if (form instanceof Sexp_list) {
			Sexp_list<FormulaAtom<StateTransFormulaVariable>> result = new Sexp_list<FormulaAtom<StateTransFormulaVariable>>();
			Iterator<Sexp<FormulaAtom<StateFormulaVariable>>> i = ((Sexp_list<FormulaAtom<StateFormulaVariable>>) form).getList().listIterator();
			Sexp<FormulaAtom<StateFormulaVariable>> current;		
			while (i.hasNext()) {
				current = i.next();
				result.addRight(convert(which,current));
			}
			return result;
		}
		assert(false);
		return null;
	}
	
	public NamedStateTransition convert(StateNext which) {
		return new NamedStateTransition(which.toString()+"_"+toString(),statetype,convert(which,formula));
	}
	
	
}
