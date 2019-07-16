package com.sri.iml.gen.mcmt.model;

/* In this implementation, MCMT formulas are S-expressions, as in the SMTlib language.
	The atoms (i.e. leaves) of such S-expressions are in that case:
	either a formula variable or a formula symbol.
	The abstract class below, with concrete subclasses FormulaSymb and FormulaVar, implements this concept. 
*/

public abstract class FormulaAtom<V> implements Sexpable {

	public Sexp<String> toSexp(){
		return new Sexp_atom<String>(toString());
	}
}
