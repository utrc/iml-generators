package com.sri.iml.gen.mcmt.model;

// Abstract class for S-expressions
// Concrete subclasses are Sexp_list and Sexp_atom
// The class parameter E indicates what atoms contain

public abstract class Sexp<E> {
	abstract public Sexp<String> toSexp();
}