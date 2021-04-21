/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
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
