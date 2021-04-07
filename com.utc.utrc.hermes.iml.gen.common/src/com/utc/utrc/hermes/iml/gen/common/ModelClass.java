package com.utc.utrc.hermes.iml.gen.common;

public enum ModelClass {
	
	SMT("Satisfiability Modulo Theories"),
	CSP("Constraint Satiscation Problen"),
	MC("Model Checker"),
	OPT("Optimization");
	
	private String name;
	
	private ModelClass(String name) {
		this.name = name;
	}
	
	public String getFullName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
