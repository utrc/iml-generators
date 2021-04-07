package com.utc.utrc.hermes.iml.gen.common.interpretation;

import java.util.ArrayList;
import java.util.List;

public class TupleInterpretation extends Interpretation {

	private List<Interpretation> tuple_i ;
	
	public TupleInterpretation(int size) {
		tuple_i = new ArrayList<>() ;
		for(int i =0 ; i < size ; i++) {
			tuple_i.add(new Interpretation()) ;
		}
		setValueType(InterpretationType.TUPLE);
	}
	public void assign(int index, Interpretation i) {
		if ( index < tuple_i.size()) {
			tuple_i.set(index, i);
		}
	}
	public Interpretation get(int index) {
		if ( index < tuple_i.size()) {
			return tuple_i.get(index);
		}
		return new Interpretation();
	}
	
	
	
	
}

