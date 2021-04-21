/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.nusmv.sms;

import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.NamedType;

public class State {
	
	private ImlType type ;
	
	public static State stateless = new State();
	
	public State() {
		
	}
	
	public State(ImlType t) {
		type = t ;
	}

	public ImlType getType() {
		return type;
	}

	public void setType(ImlType type) {
		this.type = type;
	}
	
	
}
