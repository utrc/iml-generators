package com.utc.utrc.hermes.iml.gen.nusmv.systems;

import com.utc.utrc.hermes.iml.util.ImlUtil;

public class ComponentInstance {
	private String name ;
	private ComponentType ctype ;
	
	public static ComponentInstance nil = new ComponentInstance("nil", null);
	public static ComponentInstance self = new ComponentInstance("self", null);
	
	public ComponentInstance(String name, ComponentType t) {
		this.name = name;
		ctype = t ;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ComponentType getComponentType() {
		return ctype;
	}
	public void setComponentType(ComponentType ctype) {
		this.ctype = ctype;
	}
	@Override
	public String toString() {
		String retval = name + " " + ctype.getName();
		return retval ;
	}
	
	
	
	
	
}
