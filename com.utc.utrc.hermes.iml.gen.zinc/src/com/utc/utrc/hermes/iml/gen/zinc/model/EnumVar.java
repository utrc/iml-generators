/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.model;

import at.siemens.ct.jmz.elements.Element;
import at.siemens.ct.jmz.expressions.integer.IntegerVariable;

public class EnumVar implements Element {

	EnumType type ;
	IntegerVariable var ;
	
	public EnumVar(IntegerVariable var, EnumType type) {
		this.var = var ;
		this.type = type;
	}
	
	public String getName() {
		return var.getName() ;
	}
	
	public IntegerVariable getVar() {
		return var;
	}
	
	public EnumType getType() {
		return type;
	}
	
	@Override
	public String declare() {
		StringBuilder retval = new StringBuilder() ;
		retval.append("constraint " + getName() + " in " + type.getName() + " ;\n") ;
		return retval.toString() ;
	}

	
	
}
