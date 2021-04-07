package com.utc.utrc.hermes.iml.gen.common.interpretation;

public enum InterpretationType {
		EMPTY,
		INTEGER,
		REAL,
		STRING,
		CHARACTER,
		TUPLE,
		CONSTANT,
		TABULAR,
		LAMBDA ;
	
	public boolean isEmpty() {
		return this == EMPTY;
	}
	public boolean isInteger() {
		return this == INTEGER ;
	}
	public boolean isReal() {
		return this == REAL ;
	}
	public boolean isString() {
		return this == STRING ;
	}
	public boolean isChar() {
		return this == CHARACTER ;
	}
	public boolean isTuple() {
		return this == TUPLE ;
	}
	public boolean isConstant() {
		return this == CONSTANT ;
	}
	public boolean isFunction() {
		return (this == TABULAR) || (this == LAMBDA) ;
	}
	public boolean isTabular() {
		return this == TABULAR;
	}
	public boolean isLambda() {
		return this == LAMBDA;
	}
		
		
}
