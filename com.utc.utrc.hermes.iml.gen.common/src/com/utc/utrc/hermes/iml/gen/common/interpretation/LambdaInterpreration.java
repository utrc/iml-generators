/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.common.interpretation;

import com.utc.utrc.hermes.iml.iml.LambdaExpression;

public class LambdaInterpreration extends FunctionInterpreration {

	private LambdaExpression lambda;
	
	public LambdaInterpreration(LambdaExpression lambda) {
		this.lambda = lambda;
	}
	
	public LambdaExpression getLambda() {
		return lambda;
	}
	
	public void setLambda(LambdaExpression lambda) {
		this.lambda = lambda;
	}
	
}
