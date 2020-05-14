package com.utc.utrc.hermes.iml.gen.common.systems;

import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;

public class ComponentInstance {
	
	private SymbolDeclaration sd ;
	private ComponentType ctype ;
	
	public static ComponentInstance nil = new ComponentInstance(null,null);
	public static ComponentInstance self = new ComponentInstance(null,null);
	
	public ComponentInstance(SymbolDeclaration sd, ComponentType t) {
		this.sd = sd ;
		ctype = t ;
	}
	public String getName() {
		return sd.getName();
	}
	
	public ComponentType getComponentType() {
		return ctype;
	}
	
	public void setComponentType(ComponentType ctype) {
		this.ctype = ctype;
	}
	
	@Override
	public String toString() {
		String retval = getName() + " " + ctype.getName();
		return retval ;
	}
	public SymbolDeclaration getSymbolDeclaration() {
		return sd ;
	}
	
	
	
	
}
