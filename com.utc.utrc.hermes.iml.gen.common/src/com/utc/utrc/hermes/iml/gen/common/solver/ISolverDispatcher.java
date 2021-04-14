package com.utc.utrc.hermes.iml.gen.common.solver;

import java.util.Map;

import com.utc.utrc.hermes.iml.gen.common.UnsupportedQueryException;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;

/**
 * This class is responsible of mapping different solvers to queries. All queries should be symbols with Query annotation 
 * from "iml.queries" standard IML library
 * 
 * @author Ayman Elkfrawy
 *
 */
public interface ISolverDispatcher {
	
	/**
	 * Register a solver for a given query. The query should have annotation of "iml.queries.Query". 
	 * For example: we can register "iml.queries.sat" symbol to some SMT solver.
	 * @param querySymbol
	 * @param solver
	 */
	public void registerSolver(SymbolDeclaration querySymbol, ISolver solver);
	
	/**
	 * Given a query formula, dispatch the correct solver to solve that query
	 * @param query
	 * @param args list of arguments to send to the solver
	 * @return result of solving the given query.
	 */
	public ISolverResult dispatchSolver(FolFormula query, Map<String, String> args) throws UnsupportedQueryException;
}
