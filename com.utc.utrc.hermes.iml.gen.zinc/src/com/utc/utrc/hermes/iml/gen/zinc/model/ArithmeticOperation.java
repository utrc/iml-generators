/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
/**
 * Copyright Siemens AG, 2016
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.utc.utrc.hermes.iml.gen.zinc.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import at.siemens.ct.jmz.expressions.Expression;
import at.siemens.ct.jmz.expressions.Operation;

/**
 * @author Copyright Siemens AG, 2016
 *
 * @param <T> The data type of this expression
 */
public class ArithmeticOperation<T extends Number> extends Operation<T, T>
		implements Expression<T> {

  protected ArithmeticOperation(Expression<T> left, ArithmeticOperator operator, Expression<T> right) {
    super(left, operator, right);
  }

  public static <T extends Number> ArithmeticOperation<T> plus(Expression<T> left,
      Expression<T> right) {
    return new ArithmeticOperation<T>(left, ArithmeticOperator.PLUS, right);
  }

	@SafeVarargs
	public static <T extends Number> ArithmeticOperation<T> plus(Expression<T>... summands) {
		return plus(Arrays.asList(summands));
	}

	public static <T extends Number> ArithmeticOperation<T> plus(Collection<Expression<T>> summands) {
		return plusOfList(new ArrayList<>(summands));
	}

	private static <T extends Number> ArithmeticOperation<T> plusOfList(List<Expression<T>> summands) {
		if (summands.size() < 2) {
			throw new IllegalArgumentException("At least two summands are needed to creat a sum");
		} else if (summands.size() == 2) {
			return plus(summands.get(0), summands.get(1));
		} else {
			Expression<T> head = summands.remove(0);
			return plus(head, plusOfList(summands));
		}
	}

	public static ArithmeticOperation<Float> plus(Float left, Expression<Float> right) {
    return plus(new FloatConstant(left), right);
  }

  public static ArithmeticOperation<Float> plus(Expression<Float> left, Float right) {
    return plus(left, new FloatConstant(right));
  }

  public static <T extends Number> ArithmeticOperation<T> minus(Expression<T> left,
      Expression<T> right) {
    return new ArithmeticOperation<T>(left, ArithmeticOperator.MINUS, right);
  }

  public static ArithmeticOperation<Float> minus(Float left, Expression<Float> right) {
    return minus(new FloatConstant(left), right);
  }

  public static ArithmeticOperation<Float> minus(Expression<Float> left, Float right) {
    return minus(left, new FloatConstant(right));
  }

  public static <T extends Number> ArithmeticOperation<T> times(Expression<T> left,
      Expression<T> right) {
    return new ArithmeticOperation<T>(left, ArithmeticOperator.TIMES, right);
  }

  public static ArithmeticOperation<Float> times(Float left, Expression<Float> right) {
    return times(new FloatConstant(left), right);
  }

  public static ArithmeticOperation<Float> times(Expression<Float> left, Float right) {
    return times(left, new FloatConstant(right));
  }

  public static <T extends Number> ArithmeticOperation<T> div(Expression<T> left,
      Expression<T> right) {
    return new ArithmeticOperation<T>(left, ArithmeticOperator.DIV_INT, right);
  }

  public static ArithmeticOperation<Float> div(Float left, Expression<Float> right) {
    return div(new FloatConstant(left), right);
  }

  public static ArithmeticOperation<Float> div(Expression<Float> left, Float right) {
    return div(left, new FloatConstant(right));
  }

  public static <T extends Number> ArithmeticOperation<T> modulo(Expression<T> left,
      Expression<T> right) {
    return new ArithmeticOperation<T>(left, ArithmeticOperator.MODULO, right);
  }

  public static ArithmeticOperation<Float> modulo(Float left, Expression<Float> right) {
    return modulo(new FloatConstant(left), right);
  }

  public static ArithmeticOperation<Float> modulo(Expression<Float> left, Float right) {
    return modulo(left, new FloatConstant(right));
  }

	@Override
	public ArithmeticOperator getOperator() {
		return (ArithmeticOperator) super.getOperator();
	}

}
