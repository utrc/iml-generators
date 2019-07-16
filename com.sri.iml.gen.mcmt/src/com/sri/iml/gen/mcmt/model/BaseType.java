package com.sri.iml.gen.mcmt.model;

// The base types that MCMT can talk about.

public enum BaseType implements Sexpable { 
    Bool, Real, Int;
	
	public Sexp<String> toSexp(){
		return new Sexp_atom<String>(this.name());
	}
} 