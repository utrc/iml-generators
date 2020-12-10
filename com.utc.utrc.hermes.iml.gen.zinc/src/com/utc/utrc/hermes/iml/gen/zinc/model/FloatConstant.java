/**
 * Copyright Siemens AG, 2016-2017
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.utc.utrc.hermes.iml.gen.zinc.model;

import at.siemens.ct.jmz.expressions.Constant;
import at.siemens.ct.jmz.expressions.set.IntegerSetExpression;
import at.siemens.ct.jmz.expressions.set.SetExpression;

/**
 * @author Copyright Siemens AG, 2016-2017
 */
public class FloatConstant extends Constant<Float, Float>
    implements FloatExpression, Comparable<FloatConstant> {

	public FloatConstant(Float value) {
		super(FloatSetExpression.FLOAT_UNIVERSE, value);
	}

	public FloatConstant(SetExpression<Float> type, Float value) {
		super(type, value);
	}

	/**
	 * Constructs either a new constant whose value is {@code this + delta} (if this constant has no name), or calls
	 * {@link FloatExpression#add(int)} (if this constant has a name).
	 */
	@Override
	public FloatExpression add(float delta) {
		return new FloatConstant(getValue() + delta);
		// TODO: what if has name?
	}

	@Override
	public Boolean isGreaterThanOrEqualTo(float otherValue) {
		return getValue().intValue() >= otherValue;
	}

	@Override
	public Boolean isLessThanOrEqualTo(float otherValue) {
		return getValue().intValue() <= otherValue;
	}

  public BasicFloat toNamedConstant(String name) {
    return new BasicFloat(name, FloatSetExpression.FLOAT_UNIVERSE, this);
	}

  @Override
  public int compareTo(FloatConstant o) {
    return this.getValue().compareTo(o.getValue());
  }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Constant) {
			Constant<?, ?> other = (Constant<?, ?>) obj;
			if (getValue() == null) {
				return other.getValue() == null;
			} else {
				return getValue().equals(other.getValue());
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getValue() == null) {
			return 0;
		} else {
			return getValue().hashCode();
		}
	}

  @Override
  public FloatConstant substitute(String name, Object value) {
    return new FloatConstant((SetExpression<Float>) type, this.getValue());
  }

}