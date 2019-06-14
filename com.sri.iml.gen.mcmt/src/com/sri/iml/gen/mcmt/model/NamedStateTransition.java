package com.sri.iml.gen.mcmt.model;

public class NamedStateTransition extends Named implements Instruction, StateTransFormulaVariable {

	private NamedStateType statetype;
	private Sexp<FormulaAtom<StateTransFormulaVariable>> formula;
	
	public NamedStateTransition(String name, NamedStateType statetype, Sexp<FormulaAtom<StateTransFormulaVariable>> formula) {
		super(name);
		this.statetype = statetype;
		this.formula = formula;
	}
	

	public Sexp<String> toSexp() {
		Sexp_list<String> result = new Sexp_list<String>();
		result.addRight(new Sexp_atom<String>("define-transition"));
		result.addRight(new Sexp_atom<String>(toString()));
		result.addRight(new Sexp_atom<String>(statetype.toString()));
		result.addRight(formula.toSexp());
		return result;
	}

	public static Sexp_list<FormulaAtom<StateTransFormulaVariable>> symbol(String symb){
		FormulaAtom<StateTransFormulaVariable> symbol = new FormulaSymb<StateTransFormulaVariable>(symb);
		Sexp_list<FormulaAtom<StateTransFormulaVariable>> result = new Sexp_list<FormulaAtom<StateTransFormulaVariable>>();
		result.addRight(new Sexp_atom<FormulaAtom<StateTransFormulaVariable>>(symbol));
		return result;		
	}

}
