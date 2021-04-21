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

import at.siemens.ct.jmz.elements.BasicTypeInst;
import at.siemens.ct.jmz.expressions.Expression;
import at.siemens.ct.jmz.expressions.set.SetExpression;

/**
 * @author Copyright Siemens AG, 2016-2017
 */
public class BasicFloat extends BasicTypeInst<Float> implements FloatExpression {

  public BasicFloat(BasicTypeInst<Float> innerTypeInst) {
    super(innerTypeInst);
  }

  public BasicFloat(String name, SetExpression<Float> type) {
    super(name, type);
  }

  public BasicFloat(String name, SetExpression<Float> type, Expression<Float> value) {
    super(name, type, value);
  }

	@Override
	public Boolean isGreaterThanOrEqualTo(float value) {
		Expression<Float> expression = getValue();
		if (expression instanceof FloatExpression) {
			return ((FloatExpression) expression).isGreaterThanOrEqualTo(value);
		}
		return null;
	}

	@Override
	public Boolean isLessThanOrEqualTo(float value) {
		Expression<Float> expression = getValue();
		if (expression instanceof FloatExpression) {
			return ((FloatExpression) expression).isLessThanOrEqualTo(value);
		}
		return null;
	}

  @Override
  public BasicFloat substitute(String name, Object value) {
    return new BasicFloat(this.name, this.type.substitute(name, value), this.value.substitute(name, value));
  }

}
