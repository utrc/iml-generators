package com.sri.iml.gen.mcmt.model;

public abstract class Variable extends Named implements Sexpable {

	private BaseType type; // Variables are always typed
	
	public Variable(String name, BaseType ty) {
		super(name);
		this.type = ty;
	}
	
	public BaseType getType() {
		return type;
	}
	
	// The S-expression for x of type t is (x t)
	public Sexp<String> toSexp() {
		Sexp_list<String> l = new Sexp_list<String>();
		l.addRight(new Sexp_atom<String>(toString()));
		l.addRight(type.toSexp());
		return l;
	}

}
