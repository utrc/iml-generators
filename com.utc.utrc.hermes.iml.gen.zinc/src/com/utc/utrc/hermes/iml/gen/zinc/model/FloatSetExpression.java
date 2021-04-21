/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.model;


import at.siemens.ct.jmz.elements.BasicTypeInst;
import at.siemens.ct.jmz.elements.Set;
import at.siemens.ct.jmz.expressions.set.SetExpression;


public interface FloatSetExpression extends SetExpression<Float> {

  SetExpression<Float> FLOAT_UNIVERSE = new Set<Float>(
      new BasicTypeInst<>("float", null));

  @Override
  default SetExpression<Float> getType() {
    return FLOAT_UNIVERSE;
  }
}
