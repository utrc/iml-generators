package com.sri.iml.gen.mcmt;

import com.sri.iml.gen.mcmt.model.FormulaAtom;
import com.sri.iml.gen.mcmt.model.FormulaSymb;
import com.sri.iml.gen.mcmt.model.Sexp;
import com.sri.iml.gen.mcmt.model.Sexp_atom;
import com.sri.iml.gen.mcmt.model.Sexp_list;

class AppBuilder<V> extends Sexp_list<FormulaAtom<V>> {

	AppBuilder(String symbol) {
		Sexp<FormulaAtom<V>> head = new Sexp_atom<FormulaAtom<V>>(new FormulaSymb<V>(symbol));
		addRight(head);	
	}
	
	AppBuilder<V> app(Sexp<FormulaAtom<V>> arg){
		addRight(arg);
		return this;
	}
		
}
