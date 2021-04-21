package com.utc.utrc.hermes.iml.gen.smt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.xtext.EcoreUtil2;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.gen.common.solver.ISolver;
import com.utc.utrc.hermes.iml.gen.common.solver.ISolverResult;
import com.utc.utrc.hermes.iml.gen.common.solver.SolverResultStatus;
import com.utc.utrc.hermes.iml.gen.common.solver.impl.AbstractSolverResult;
import com.utc.utrc.hermes.iml.gen.smt.encoding.ImlSmtEncoder;
import com.utc.utrc.hermes.iml.gen.smt.encoding.SMTEncodingException;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleFunDeclaration;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSmtFormula;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSort;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.LambdaExpression;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.util.FileUtil;

public class ImlSmtSolver implements ISolver {

	@Inject ImlSmtEncoder<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula> encoder;
	
	@Override
	public ISolverResult runSolver(FolFormula query, Map<String, String> args) {
		encoder.reset();
		AbstractSolverResult solverResult = new AbstractSolverResult();
		if (query instanceof LambdaExpression) {
			LambdaExpression qeryCopy = EcoreUtil2.copy((LambdaExpression) query);
			SymbolDeclaration queryInst = qeryCopy.getParameters().remove(0); // This is our instance
			TermExpression lamDefin = ((LambdaExpression) qeryCopy).getDefinition();
			encoder.encode(queryInst);
			try {
				SimpleSmtFormula propertyForm = encoder.encodeFormula(lamDefin, queryInst);
				StringBuilder sb = new StringBuilder(encoder.toString());
				sb.append(String.format("(assert %s)", propertyForm.toString()));
				sb.append("(check-sat)");
				System.out.println(sb.toString());
				
				String solverInput = sb.toString();
				List<String> result = runZ3(solverInput);
				if (!result.isEmpty()) {
					if (result.get(0).equals("sat")) {
						solverResult.setStatus(SolverResultStatus.VALID);
					} else if (result.get(0).equals("unsat")) {
						solverResult.setStatus(SolverResultStatus.INVALID);
					} else {
						solverResult.setStatus(SolverResultStatus.UNKNOWN);
					}
				}
				
			} catch (SMTEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return solverResult;
	}

	private List<String> runZ3(String solverInput) {
		String fileName = "solver_input.smt";
		FileUtil.writeFileContent(fileName, solverInput);
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"z3", fileName};
		Process proc;
		try {
			proc = rt.exec(commands);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			
			return stdInput.lines().collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
