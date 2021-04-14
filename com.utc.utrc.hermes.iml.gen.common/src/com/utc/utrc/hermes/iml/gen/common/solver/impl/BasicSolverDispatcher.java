package com.utc.utrc.hermes.iml.gen.common.solver.impl;

import java.util.List;
import java.util.Map;

import com.utc.utrc.hermes.iml.gen.common.UnsupportedQueryException;
import com.utc.utrc.hermes.iml.gen.common.solver.ISolver;
import com.utc.utrc.hermes.iml.gen.common.solver.ISolverResult;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.iml.TailedExpression;

/**
 * Basic implementation for dispatcher strategy by selecting the first solver compatible with the giving query.
 * 
 * @author Ayman Elkfrawy
 *
 */
public class BasicSolverDispatcher extends AbstractSolverDispatcher {

	@Override
	public ISolverResult dispatchSolver(FolFormula query, Map<String, String> args) throws UnsupportedQueryException {
		SymbolDeclaration querySymbol = getQueryFromFormula(query);
		List<ISolver> solvers = getRegisteredSolvers(querySymbol);
		if (!solvers.isEmpty()) {
			return solvers.get(0).runSolver(query, args);
		} else {
			throw new UnsupportedQueryException("Couldn't find solver for the given query: " + query);
		}
	}

	private SymbolDeclaration getQueryFromFormula(FolFormula query) {
		if (query instanceof SymbolReferenceTerm) {
			if (((SymbolReferenceTerm) query).getSymbol() instanceof SymbolDeclaration) {
				return (SymbolDeclaration) ((SymbolReferenceTerm) query).getSymbol();
			} else return null;
		} 
		if (query instanceof SignedAtomicFormula) {
			return getQueryFromFormula(query.getLeft());
		}
		if (query instanceof TailedExpression) {
			return getQueryFromFormula(query.getLeft());
		}
		
		return null;
	}

}
