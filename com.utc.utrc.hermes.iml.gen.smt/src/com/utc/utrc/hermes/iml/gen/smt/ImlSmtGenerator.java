package com.utc.utrc.hermes.iml.gen.smt;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.gen.common.IImlGenerator;
import com.utc.utrc.hermes.iml.gen.common.impl.AbstractImlGeneratorResult;
import com.utc.utrc.hermes.iml.gen.smt.encoding.ImlSmtEncoder;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleFunDeclaration;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSmtFormula;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSort;
import com.utc.utrc.hermes.iml.iml.Symbol;
  
public class ImlSmtGenerator implements IImlGenerator {
	
	@Inject ImlSmtEncoder<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula> encoder;

	@Override
	public boolean canGenerate(Symbol query) {
		return true;
	}

	@Override
	public AbstractImlGeneratorResult generate(Symbol query) {
		AbstractImlGeneratorResult result = new AbstractImlGeneratorResult();
		encoder.encode(query);
		result.setGeneratedModel(encoder.toString());
		return result;
	}

	@Override
	public boolean canRunSolver() {
		return false;
	}

	@Override
	public String runSolver(String generatedModel) {
		throw new UnsupportedOperationException();
	}

}
