/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.common.interpretation;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PrimitiveInterpretation extends Interpretation {

	private BigInteger ivalue ;
	private BigDecimal dvalue;
	private String svalue ;
	private char cvalue ;
	
	
	
	public PrimitiveInterpretation(BigInteger v) {
		ivalue = v ;
		setValueType(InterpretationType.INTEGER) ;
	}
	public PrimitiveInterpretation(BigDecimal v) {
		dvalue = v ;
		setValueType(InterpretationType.REAL) ;
	}
	public PrimitiveInterpretation(String v) {
		svalue = v ;
		setValueType(InterpretationType.STRING) ;
	}
	public PrimitiveInterpretation(char v) {
		cvalue = v ;
		setValueType(InterpretationType.CHARACTER) ;
	}
	

	public BigInteger getIntValue() {
		return ivalue ;
	}
	public BigDecimal getRealValue() {
		return dvalue ;
	}
	public String getStringValue() {
		return svalue ;
	}
	public char getCharValue() {
		return cvalue ;
	}
	
	
	
	
}
