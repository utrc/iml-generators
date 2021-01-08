package com.utc.utrc.hermes.iml.gen.common;

import com.utc.utrc.hermes.iml.iml.Symbol;

/**
 * An interface for IML Generators extension point
 * 
 * @author Ayman Elkfrawy
 *
 */
public interface IImlGenerator {
	
	public boolean canGenerate(Symbol query);
	
	public ImlGeneratorResult generate(Symbol query);
	
	public boolean canRunSolver();
	
	public String runSolver(String generatedModel);
}
