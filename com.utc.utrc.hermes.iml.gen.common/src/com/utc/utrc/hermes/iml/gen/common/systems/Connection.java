/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.common.systems;

import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;

public class Connection {
	private SymbolDeclaration sd ;
	private ComponentInstance sourceComponent;
	private ComponentInstance targetComponent;
	private Port sourcePort;
	private Port targetPort;


	public Connection(SymbolDeclaration sd, ComponentInstance s, Port sp, ComponentInstance t, Port tp) {
		setConnection(s,sp,t,tp);
		this.sd = sd ;
	}
	
	public Connection(SymbolDeclaration sd,Port sp, ComponentInstance t, Port tp) {
		setConnection(ComponentInstance.self,sp,t,tp);
		this.sd = sd ;
	}

	public Connection(SymbolDeclaration sd,ComponentInstance s, Port sp, Port tp) {
		setConnection(s,sp,ComponentInstance.self,tp) ;
		this.sd = sd ;
	}

	private void setConnection(ComponentInstance s, Port sp, ComponentInstance t, Port tp) {
		sourceComponent = s;
		targetComponent = t;
		sourcePort = sp;
		targetPort = tp;
	}

	public ComponentInstance getSourceComponent() {
		return sourceComponent;
	}

	public ComponentInstance getTargetComponent() {
		return targetComponent;
	}

	public Port getSourcePort() {
		return sourcePort;
	}

	public Port getTargetPort() {
		return targetPort;
	}
	
	public SymbolDeclaration getSymbolDeclaration() {
		return sd ;
	}
	
}
