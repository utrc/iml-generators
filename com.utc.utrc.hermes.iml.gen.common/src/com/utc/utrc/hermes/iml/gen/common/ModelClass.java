/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
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
