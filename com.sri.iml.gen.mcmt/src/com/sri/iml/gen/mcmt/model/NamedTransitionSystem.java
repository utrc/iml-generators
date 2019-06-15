package com.sri.iml.gen.mcmt.model;

import java.util.Collection;
import java.util.HashSet;

public class NamedTransitionSystem extends Named implements Instruction, StateTransFormulaVariable {

	private NamedStateType statetype;
	private Sexp<FormulaAtom<StateFormulaVariable>> init;
	private Collection<Sexp<FormulaAtom<StateTransFormulaVariable>>> transitions;
	
	public NamedTransitionSystem(String name, NamedStateType statetype, Sexp<FormulaAtom<StateFormulaVariable>> init) {
		super(name);
		this.init = init;
		this.statetype = statetype;
		this.transitions = new HashSet<Sexp<FormulaAtom<StateTransFormulaVariable>>>();
	}

	public void add_transition(Sexp<FormulaAtom<StateTransFormulaVariable>> transition) {
		this.transitions.add(transition);
	}
	
	public NamedStateType getStateType() {
		return this.statetype;
	}

	public Sexp<String> toSexp() {
		Sexp_list<String> result = new Sexp_list<String>();
		result.addRight(new Sexp_atom<String>("define-transition-system"));
		result.addRight(new Sexp_atom<String>(toString()));
		result.addRight(new Sexp_atom<String>(statetype.toString()));
		result.addRight(init.toSexp());
		for(Sexp<FormulaAtom<StateTransFormulaVariable>> transition : transitions){
			result.addRight(transition.toSexp());
		}
		return result;
	}

}
