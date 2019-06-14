package com.sri.iml.gen.mcmt.model;

// One of the two classes of things we find at the leaves of formulas (which are S-expressions),
// here the class of formula symbols, which are simply characterised by a string

public class FormulaSymb<V> extends FormulaAtom<V> {

	private String symbol;
	
	public FormulaSymb(String symb) {
		this.symbol = symb;
	}
	
	public String toString() {
		return symbol.toString();
	}
}
