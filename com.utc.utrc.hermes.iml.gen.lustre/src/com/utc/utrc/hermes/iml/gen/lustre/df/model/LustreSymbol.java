/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.lustre.df.model;

import java.util.ArrayList;
import java.util.List;

public class LustreSymbol {
	private LustreElementType elementType ;
	private String name = null;
	private LustreTypeInstance type ;
	private String definition;
	private List<LustreVariable> parameters ;
	private boolean isAssume;
	private boolean isGuarantee;
	
	
	private LustreNode container ;
	
	public LustreSymbol(String name) {
		this.name = name;
		container = null ;
		parameters = new ArrayList<LustreVariable>() ;
		elementType = LustreElementType.VAR ;
		isAssume = false;
		isGuarantee = false;
	}
	
	public LustreSymbol(LustreSymbol other) {
		elementType = other.elementType;
		name = other.name;
		type = new LustreTypeInstance(other.type);
		definition = other.definition;
		parameters = new ArrayList<LustreVariable>() ;
		for(LustreVariable v : parameters) {
			parameters.add(new LustreVariable(v));
		}
		isAssume = other.isAssume;
		isGuarantee = other.isGuarantee;
	}
	
	/**
	 * @return the isAssume
	 */
	public boolean isAssume() {
		return isAssume;
	}

	/**
	 * @param isAssume the isAssume to set
	 */
	public void setAssume(boolean isAssume) {
		this.isAssume = isAssume;
	}

	/**
	 * @return the isGuarantee
	 */
	public boolean isGuarantee() {
		return isGuarantee;
	}

	/**
	 * @param isGuarantee the isGuarantee to set
	 */
	public void setGuarantee(boolean isGuarantee) {
		this.isGuarantee = isGuarantee;
	}

	public LustreElementType getElementType() {
		return elementType;
	}

	public void setElementType(LustreElementType elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LustreTypeInstance getType() {
		return type;
	}

	public void setType(LustreTypeInstance type) {
		this.type = type;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public LustreNode getContainer() {
		return container;
	}

	public void setContainer(LustreNode container) {
		this.container = container;
	}
	
	public List<LustreVariable> getParameters(){
		return parameters;
	}
	public void addParameter(LustreVariable v) {
		parameters.add(v);
	}
	
	public int indexOf(String pname) {
		for(int index = 0 ; index < parameters.size() ; index++) {
			if (parameters.get(index).getName().equals(pname)) {
				return index ;
			}
		}
		return -1;
	}
	
}
