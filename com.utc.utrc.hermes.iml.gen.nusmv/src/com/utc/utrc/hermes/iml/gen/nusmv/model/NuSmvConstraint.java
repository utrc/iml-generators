/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.nusmv.model;

import com.utc.utrc.hermes.iml.iml.FolFormula;

public class NuSmvConstraint {
	private FolFormula f;
	public NuSmvConstraint(FolFormula f) {
		this.f = f ;
	}
	public FolFormula getF() {
		return f;
	}
	public void setF(FolFormula f) {
		this.f = f;
	}
}
