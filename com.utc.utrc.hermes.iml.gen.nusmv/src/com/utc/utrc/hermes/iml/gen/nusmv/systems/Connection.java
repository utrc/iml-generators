package com.utc.utrc.hermes.iml.gen.nusmv.systems;

public class Connection {
	private ComponentInstance sourceComponent;
	private ComponentInstance targetComponent;
	private Port sourcePort;
	private Port targetPort;


	public Connection(ComponentInstance s, Port sp, ComponentInstance t, Port tp) {
		setConnection(s,sp,t,tp);
	}
	
	public Connection(Port sp, ComponentInstance t, Port tp) {
		setConnection(ComponentInstance.self,sp,t,tp);
	}

	public Connection(ComponentInstance s, Port sp, Port tp) {
		setConnection(s,sp,ComponentInstance.self,tp) ;
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
	
}
