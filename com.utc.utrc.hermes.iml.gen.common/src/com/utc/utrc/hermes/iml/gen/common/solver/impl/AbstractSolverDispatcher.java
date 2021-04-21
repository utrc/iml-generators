/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.common.solver.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utc.utrc.hermes.iml.gen.common.solver.ISolver;
import com.utc.utrc.hermes.iml.gen.common.solver.ISolverDispatcher;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;

/**
 * Abstract solver dispatcher that implements the logic of associating symbol to a solver. This should be 
 * used as base class for different dispatching strategies.
 * 
 * @author Ayman Elkfrawy
 *
 */
public abstract class AbstractSolverDispatcher implements ISolverDispatcher {
	
	protected Map<SymbolDeclaration, List<ISolver>> solversRepository;

	public AbstractSolverDispatcher() {
		solversRepository = new HashMap<>();
	}
	
	@Override
	public void registerSolver(SymbolDeclaration querySymbol, ISolver solver) {
		List<ISolver> solversList;
		if (solversRepository.containsKey(querySymbol)) {
			solversList = solversRepository.get(querySymbol);
		} else {
			solversList = new ArrayList<ISolver>();
			solversRepository.put(querySymbol, solversList);
		}
		
		if (!solversList.contains(solver)) {
			solversList.add(solver);
		}
	}
	
	/**
	 * Get solvers for a registered symbol
	 * @param querySymbol the registered symbols
	 * @return list of solvers that support given symbol, or empty list if no solvers found for that symbols.
	 */
	protected List<ISolver> getRegisteredSolvers(SymbolDeclaration querySymbol) {
		if (solversRepository.containsKey(querySymbol)) {
			return solversRepository.get(querySymbol);
		} else {
			return new ArrayList<>();
		}
	}

}
