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

import java.util.Arrays;
import java.util.regex.Pattern;

import at.siemens.ct.jmz.elements.Variable;
import at.siemens.ct.jmz.expressions.Expression;
import at.siemens.ct.jmz.expressions.array.ArrayExpression;
import at.siemens.ct.jmz.expressions.set.SetExpression;

/**
 * @author Copyright Siemens AG, 2016-2017
 */
public class FloatVariable extends Variable<Float, Float> implements FloatExpression {

  private SetExpression<Float> type;

	public FloatVariable(String name) {
    this(name, FloatSetExpression.FLOAT_UNIVERSE);
	}

	public FloatVariable(String name, SetExpression<Float> type) {
		this(name, type, null);
	}

	public FloatVariable(String name, SetExpression<Float> type, Expression<Float> value) {
    super(name, type);
    this.type = type;
		this.value = value;
	}

	/**
	 * Creates an integer variable named {@code name} and assigns the sum of {@code summands} to it.
	 *
	 * @param name
	 * @param summands
	 * @return a reference to the created variable.
	 */
	public static FloatVariable createSum(String name, ArrayExpression<Float> summands) {
		return new FloatVariable(name, FloatSetExpression.FLOAT_UNIVERSE, new Sum(summands)); // TODO: tighter domain bounds?
	}

	/**
	 * Creates an integer variable named {@code name} and assigns the sum of {@code summands} to it.
	 *
	 * @param name
	 * @param summands
	 * @return a reference to the created variable.
	 */
	@SafeVarargs
	public static FloatVariable createSum(String name, ArrayExpression<Float>... summands) {
		return new FloatVariable(name, FloatSetExpression.FLOAT_UNIVERSE, multipleSummands(summands)); // TODO: tighter domain bounds?
	}

	private static Expression<Float> multipleSummands(ArrayExpression<Float>[] summands) {
		return ArithmeticOperation.plus(sums(summands));
	}

	private static Sum[] sums(ArrayExpression<Float>[] summands) {
		return Arrays.stream(summands).map(s -> new Sum(s)).toArray(size -> new Sum[size]);
	}

	@Override
	public String use() {
		return getName();
	}

	@Override
	public Pattern getPattern() {
		return getFloatPattern();
	}

	public static Pattern getFloatPattern() {
		return Pattern.compile("-?\\d+");
	}

	@Override
	public Float parseValue(String string) {
		float i = Float.parseFloat(string);
    Boolean valueInDomain = type.contains(i);
		if (valueInDomain == Boolean.FALSE) {
			throw new IllegalArgumentException("Value not in domain: " + string);
		}
		return i;
	}

  @Override
  public FloatVariable substitute(String name, Object value) {
    return new FloatVariable(this.getName(), this.type.substitute(name, value), this.value.substitute(name, value));
  }

}
