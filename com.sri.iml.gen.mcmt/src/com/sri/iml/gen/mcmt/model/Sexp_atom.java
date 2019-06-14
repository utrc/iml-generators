package com.sri.iml.gen.mcmt.model;

public class Sexp_atom<Atom> extends Sexp<Atom> {

	Atom atom;
	
	public Sexp_atom(Atom atom){
		this.atom = atom;
	}
	
	public String to_string() {
		return this.atom.toString();
	}
	
	public Sexp<String> toSexp(){
		return new Sexp_atom<String>(this.atom.toString());
	}

}
