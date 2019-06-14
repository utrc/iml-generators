package com.sri.iml.gen.mcmt.model;

public class Query implements Instruction {

	private NamedTransitionSystem system;
	private Sexp<FormulaAtom<StateFormulaVariable>> formula;
	
	public Query(NamedTransitionSystem system, Sexp<FormulaAtom<StateFormulaVariable>> formula) {
		this.system = system;
		this.formula = formula;
	}
	
	public Sexp<String> toSexp() {
		Sexp_list<String> result = new Sexp_list<String>();
		result.addRight(new Sexp_atom<String>("query"));
		result.addRight(new Sexp_atom<String>(system.toString()));
		result.addRight(formula.toSexp());
		return result;
	}

}
