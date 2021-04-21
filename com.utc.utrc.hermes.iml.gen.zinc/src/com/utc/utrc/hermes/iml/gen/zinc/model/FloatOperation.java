/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
/**
 * Copyright Siemens AG, 2016-2017
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.utc.utrc.hermes.iml.gen.zinc.model;

import at.siemens.ct.jmz.expressions.Expression;
import at.siemens.ct.jmz.expressions.UnknownExpressionValueException;

/**
 * @author Copyright Siemens AG, 2016-2017
 */
public class FloatOperation extends ArithmeticOperation<Float> implements FloatExpression {

	public FloatOperation(Expression<Float> left, ArithmeticOperator operator, Expression<Float> right) {
		super(left, operator, right);
	}

	public FloatOperation(ArithmeticOperation<Float> arithmeticOperation) {
		super(arithmeticOperation.getLeft(), arithmeticOperation.getOperator(), arithmeticOperation.getRight());
	}

  @Override
  public FloatOperation substitute(String name, Object value) {
    return new FloatOperation(((FloatOperation) left).substitute(name, value), (ArithmeticOperator) operator,
        ((FloatOperation) right).substitute(name, value));
  }

  @Override
  public Float value() throws UnknownExpressionValueException {
    switch (getOperator()) {
    case PLUS:
      return left.value() + right.value();
    case MINUS:
      return left.value() - right.value();
    case TIMES:
      return left.value() * right.value();
    case DIV_INT:
      return left.value() / right.value();
    case MODULO:
      return left.value() % right.value();
    default:
      throw new UnknownExpressionValueException(this);
    }
  }

}
