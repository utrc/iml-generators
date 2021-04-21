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

/**
 * @author Copyright Siemens AG, 2016-2017
 */
public interface FloatExpression extends Expression<Float> {

  /**
   * Adds a delta to this expression and returns the result.
   * 
   * @param delta
   * @return a new expression whose value is {@code this+delta}
   */
	default FloatExpression add(float delta) {
    if (delta > 0) {
			return new FloatOperation(ArithmeticOperation.plus(this, delta));
    } else if (delta < 0) {
			return new FloatOperation(ArithmeticOperation.minus(this, -delta));
    } else {
      return this;
    }
  }

	default FloatExpression addTo(Expression<Float> otherSummand) {
		return new FloatOperation(ArithmeticOperation.plus(otherSummand, this));
	}

  /**
   * Checks if the given integer expression string is negative.
   * 
   * @param expression
   * @return {@code true} iff the given expression starts with a minus sign
   */
  static boolean isNegative(String expression) {
    return expression.startsWith("-");
  }

  /**
   * Puts the given integer expression string into braces.
   * 
   * @param expression
   * @return a parenthesised version of the given expression
   */
  static String parenthesise(String expression) {
    return "(" + expression + ")";
  }

  /**
   * Checks if the given value is greater than or equal to this expression. If this cannot be determined (e.g. if the
   * value of the expression is not known), {@code null} is returned.
   * 
   * @param value
   * @return
   */
  default Boolean isGreaterThanOrEqualTo(float value) {
    return null;
  }

  /**
   * Checks if the given value is less than or equal to this expression. If this cannot be determined (e.g. if the value
   * of the expression is not known), {@code null} is returned.
   * 
   * @param value
   * @return
   */
  default Boolean isLessThanOrEqualTo(float value) {
    return null;
  }

  @Override
  FloatExpression substitute(String name, Object value);

}
