package com.sri.iml.gen.mcmt.model;

public class NamedStateTransition extends Named implements Instruction, StateTransFormulaVariable {

	private NamedStateType statetype;
	private Sexp<FormulaAtom<StateTransFormulaVariable>> formula;
	
	public NamedStateTransition(String name, NamedStateType statetype, Sexp<FormulaAtom<StateTransFormulaVariable>> formula) {
		super(name);
		this.statetype = statetype;
		this.formula = formula;
	}
	
	public Sexp<FormulaAtom<StateTransFormulaVariable>> getFormula() {
		return formula;
	}

	public Sexp<String> toSexp() {
		Sexp_list<String> result = new Sexp_list<String>();
		result.addRight(new Sexp_atom<String>("define-transition"));
		result.addRight(new Sexp_atom<String>(toString()));
		result.addRight(new Sexp_atom<String>(statetype.toString()));
		result.addRight(formula.toSexp());
		return result;
	}
	
}
