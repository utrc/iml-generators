package com.sri.iml.gen.mcmt.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class NamedTransitionSystem extends Named implements Instruction {

	private NamedStateType statetype;
	private Collection<Sexp<FormulaAtom<StateFormulaVariable>>> inits;
	private Collection<Sexp<FormulaAtom<StateTransFormulaVariable>>> transitions;
	
	public NamedTransitionSystem(String name, NamedStateType statetype) {
		super(name);
		this.inits = new HashSet<Sexp<FormulaAtom<StateFormulaVariable>>>();
		this.statetype = statetype;
		this.transitions = new HashSet<Sexp<FormulaAtom<StateTransFormulaVariable>>>();
	}

	public void add_init(Sexp<FormulaAtom<StateFormulaVariable>> init) {
		this.inits.add(init);
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
		if (inits.isEmpty()) {
			result.addRight(new Sexp_atom<String>("true"));
		}
		else {
			Iterator<Sexp<FormulaAtom<StateFormulaVariable>>> i = inits.iterator();
			Sexp<FormulaAtom<StateFormulaVariable>> current = i.next();
			if (!i.hasNext()) {
				result.addRight(current.toSexp());
			} else {
				Sexp_list<String> tmp = new Sexp_list<String>();
				tmp.addRight(new Sexp_atom<String>("and"));
				tmp.addRight(current.toSexp());
				while (i.hasNext()) {
					current = i.next();
					tmp.addRight(current.toSexp());
				}
				result.addRight(tmp);
			}
		}
		if (transitions.isEmpty()) {
			result.addRight(new Sexp_atom<String>("true"));
		}
		else {
			Iterator<Sexp<FormulaAtom<StateTransFormulaVariable>>> i = transitions.iterator();
			Sexp<FormulaAtom<StateTransFormulaVariable>> current = i.next();
			if (!i.hasNext()) {
				result.addRight(current.toSexp());
			} else {
				Sexp_list<String> tmp = new Sexp_list<String>();
				tmp.addRight(new Sexp_atom<String>("and"));
				tmp.addRight(current.toSexp());
				while (i.hasNext()) {
					current = i.next();
					tmp.addRight(current.toSexp());
				}
				result.addRight(tmp);
			}
		}
		return result;
	}

}
