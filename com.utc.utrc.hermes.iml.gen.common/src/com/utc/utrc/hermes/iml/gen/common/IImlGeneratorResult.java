/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.common;


import com.utc.utrc.hermes.iml.gen.common.solver.ISolverDispatcher;

public interface IImlGeneratorResult {
	
	public String getGeneratedModel();

	/**
	 * Get the original variable name of the input model given the generated name for it
	 * @param generatedName the name of the variable in the generated model
	 * @return the name of the variable in the original model, null if it doesn't exist
	 */
	public String getOriginalNameOf(String generatedName);
	
	/**
	 * Get the generated variable name in the generated model given the original variable name for it
	 * @param originalName the name of the variable in the original input model
	 * @return the name of the variable in the generated model, null if it doesn't exist
	 */
	public String getGeneratedNameOf(String originalName);
	
	/**
	 * Get the current model class. This class would be used by the {@link ISolverDispatcher}. See {@link ModelClass}.
	 */
	public ModelClass getModelClass();
	
}
