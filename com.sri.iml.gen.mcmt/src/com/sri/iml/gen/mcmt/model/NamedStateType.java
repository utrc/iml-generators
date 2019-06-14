package com.sri.iml.gen.mcmt.model;

public class NamedStateType extends Named implements Instruction {

	private Record<StateVariable> variables;
	private Record<Input> inputs;
	
	public NamedStateType(String name, Record<StateVariable> variables, Record<Input> inputs) {
		super(name);
		this.variables = variables;
		this.inputs = inputs;
	}

	public NamedStateType(String name, Record<StateVariable> variables) {
		this(name, variables, new Record<Input>());
	}

	public Sexp<String> toSexp() {
		Sexp_list<String> result = new Sexp_list<String>();
		result.addRight(new Sexp_atom<String>("define-state-type"));
		result.addRight(new Sexp_atom<String>(toString()));
		result.addRight(variables.toSexp());
		result.addRight(inputs.toSexp());
		return result;
	}

}
