/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.common.solver.impl;

import com.utc.utrc.hermes.iml.gen.common.interpretation.Interpretation;
import com.utc.utrc.hermes.iml.gen.common.solver.ISolverResult;
import com.utc.utrc.hermes.iml.gen.common.solver.SolverResultStatus;

public class AbstractSolverResult implements ISolverResult {

	private SolverResultStatus status;
	private String explanation;
	private Interpretation interpretation;
	
	public AbstractSolverResult() {
	}
	
	public AbstractSolverResult(SolverResultStatus status, String explanation, Interpretation interpretation) {
		super();
		this.status = status;
		this.explanation = explanation;
		this.interpretation = interpretation;
	}

	public SolverResultStatus getStatus() {
		return status;
	}

	public void setStatus(SolverResultStatus status) {
		this.status = status;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public void setInterpretation(Interpretation interpretation) {
		this.interpretation = interpretation;
	}

	@Override
	public SolverResultStatus getResultStatus() {
		return status;
	}

	@Override
	public String getExplanation() {
		return explanation;
	}

	@Override
	public Interpretation getInterpretation() {
		return interpretation;
	}

}
