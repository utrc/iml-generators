package com.sri.iml.gen.mcmt.model;

// One of the two classes of things we find at the leaves of formulas (which are S-expressions),
// here the class of formula variables

public class FormulaVar<V> extends FormulaAtom<V> {

	private V var;
	
	public FormulaVar(V v) {
		assert(v != null);
		this.var = v;
	}
	
	public String toString() {
		return var.toString();
	}
	
	V getVar() {
		return var;
	}

}
