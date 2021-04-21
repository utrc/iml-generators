/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.model.simplesmt;

public class SimpleSmtUtil {
	
	public static String getQuotedName(String name) {
//		return "|" + name + "|";
		return name.replace('<', '_').replace('>', '_').replace(" ", "").replace(',', '_');
	}

}
