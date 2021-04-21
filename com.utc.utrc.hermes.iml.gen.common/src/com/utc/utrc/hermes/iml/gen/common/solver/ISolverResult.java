/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.common.solver;

import com.utc.utrc.hermes.iml.gen.common.interpretation.Interpretation;

public interface ISolverResult {
	public SolverResultStatus getResultStatus();
	
	public String getExplanation();
	
	public Interpretation getInterpretation();
}
