package com.sri.iml.gen.mcmt.model;

public class NamedStateFormula extends Named implements Instruction, StateFormulaVariable {

	private NamedStateType statetype;
	private Sexp<FormulaAtom<StateFormulaVariable>> formula;
	
	public NamedStateFormula(String name, NamedStateType statetype, Sexp<FormulaAtom<StateFormulaVariable>> formula) {
		super(name);
		this.statetype = statetype;
		this.formula = formula;		
	}
	

	public Sexp<String> toSexp() {
		Sexp_list<String> result = new Sexp_list<String>();
		result.addRight(new Sexp_atom<String>("define-states"));
		result.addRight(new Sexp_atom<String>(toString()));
		result.addRight(new Sexp_atom<String>(statetype.toString()));
		result.addRight(formula.toSexp());
		return result;
	}
	
}
