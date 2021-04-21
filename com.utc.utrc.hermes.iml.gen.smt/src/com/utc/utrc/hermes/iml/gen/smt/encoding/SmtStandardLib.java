/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.encoding;

import java.util.Arrays;
import java.util.List;

public class SmtStandardLib {
	
	private static List<String> nativeTypes = Arrays.asList("Int", "Real", "Bool", "iml.lang.Int", "iml.lang.Real", "iml.lang.Bool");
	
	public static boolean isNative(String name) {
		return nativeTypes.contains(name);
	}

}
