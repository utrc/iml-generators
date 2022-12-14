/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.model;

import com.utc.utrc.hermes.iml.gen.smt.encoding.SmtStandardLib;
import com.utc.utrc.hermes.iml.util.ImlUtil;

public class AbstractSort implements NamedEntity {

	protected String name;
	
	@Override
	public String getName() {
		if (SmtStandardLib.isNative(name)) {
			return ImlUtil.getUnqualifiedName(name);
		}
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
