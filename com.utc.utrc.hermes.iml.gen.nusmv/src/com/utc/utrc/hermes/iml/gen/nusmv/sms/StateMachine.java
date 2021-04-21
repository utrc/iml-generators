/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.nusmv.sms;

import java.util.HashMap;
import java.util.Map;

import com.utc.utrc.hermes.iml.gen.common.systems.ComponentType;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;

public class StateMachine {
	
	private SimpleTypeReference smType ;
	private State stateType ;
	private FolFormula invariant ;
	private FolFormula transition ;
	private FolFormula init ;
	private Map<String, FolFormula> defines ;
	boolean component ;
	private ComponentType ctype ;
	
	public StateMachine() {
		defines = new HashMap<String, FolFormula>() ;
	}

	public SimpleTypeReference getSmType() {
		return smType;
	}

	public void setSmType(SimpleTypeReference smType) {
		this.smType = smType;
	}

	public FolFormula getInvariant() {
		return invariant;
	}

	public void setInvariant(FolFormula invariant) {
		this.invariant = invariant;
	}

	public FolFormula getTransition() {
		return transition;
	}

	public void setTransition(FolFormula transition) {
		this.transition = transition;
	}

	public FolFormula getInit() {
		return init;
	}

	public void setInit(FolFormula init) {
		this.init = init;
	}

	public Map<String, FolFormula> getDefines() {
		return defines;
	}

	public State getStateType() {
		return stateType;
	}

	public void setStateType(State stateType) {
		this.stateType = stateType;
	}

	public boolean isComponent() {
		return component;
	}

	public void setComponent(boolean component) {
		this.component = component;
	}
	
	public ComponentType getComponentType() {
		return ctype ;
	}
	public void setComponentType(ComponentType ct) {
		ctype = ct ;
	}
	
	
}
