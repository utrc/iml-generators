package com.utc.utrc.hermes.iml.gen.common.solver;

import java.util.Map;

import com.utc.utrc.hermes.iml.iml.FolFormula;

public interface ISolver {
	
	/**
	 * Run a solver over the given query formula and provide the result of the solver
	 * @param query is a formula capturing the query to solve.
	 * @param args an optional argument map to use with the solver
	 * @return the solver result
	 */
	public ISolverResult runSolver(FolFormula query, Map<String, String> args);
	
}
