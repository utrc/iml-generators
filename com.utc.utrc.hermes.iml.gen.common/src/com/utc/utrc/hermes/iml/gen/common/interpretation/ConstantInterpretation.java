package com.utc.utrc.hermes.iml.gen.common.interpretation;

import java.util.HashMap;
import java.util.Map;

import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;

/*

type A {
	x : Int ;
}

type B {
	a : A ;
	y : Int ;
}

ac : A ;

Interpretation is a table:
- It is associated with the simple type reference A

| x | v : PrimitiveInterpretation |


v.type = INTEGER
v.ivalue = 129


| a | va : ConstantInterpretation |
| y | vy : PrimitiveInterpretation|

va.t = CONSTANT
va.value_map
| x | PrimitiveInterpretation |



 * 
 * */

public class ConstantInterpretation extends Interpretation {
	
	private SimpleTypeReference tref ;
	private Map<SymbolDeclaration,Interpretation> value_map;
	
	public ConstantInterpretation(SimpleTypeReference tref) {
		setValueType(InterpretationType.CONSTANT);
		this.tref = tref ;
		value_map = new HashMap<>();
	}
	
	public void assign(SymbolDeclaration s, Interpretation i) {
		value_map.put(s,i) ;
	}
	public Interpretation get(SymbolDeclaration s) {
		if (value_map.containsKey(s)) {
			return value_map.get(s);
		}
		return new Interpretation();
	}
	
	public SimpleTypeReference getType() {
		return tref ;
	}
	
}
