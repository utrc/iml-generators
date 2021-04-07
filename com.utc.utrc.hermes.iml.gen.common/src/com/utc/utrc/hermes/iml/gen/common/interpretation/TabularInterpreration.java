package com.utc.utrc.hermes.iml.gen.common.interpretation;

import java.util.Map;

public class TabularInterpreration extends FunctionInterpreration {

	
	private Map<TupleInterpretation,Interpretation> value_map ;
	
	public TabularInterpreration() {
		setValueType(InterpretationType.TABULAR);
	}
	
	public void assign(TupleInterpretation d, Interpretation r) {
		value_map.put(d,r) ;
	}
	public Interpretation get(TupleInterpretation d) {
		if (value_map.containsKey(d)) {
			return value_map.get(d);
		}
		return new Interpretation();
	}
	
	
	
}
