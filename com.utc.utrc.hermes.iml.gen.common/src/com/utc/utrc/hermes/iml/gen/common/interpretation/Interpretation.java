package com.utc.utrc.hermes.iml.gen.common.interpretation;


public class Interpretation {
	
	
	private InterpretationType t ;
	
	public Interpretation() {
		t = InterpretationType.EMPTY;
	}
	
	public void setValueType(InterpretationType t) {
		this.t = t;
	}
	public InterpretationType getValueType() {
		return t ;
	}
}
