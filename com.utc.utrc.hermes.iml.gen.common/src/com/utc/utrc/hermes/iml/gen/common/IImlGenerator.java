package com.utc.utrc.hermes.iml.gen.common;

import java.util.Map;

import com.utc.utrc.hermes.iml.gen.common.impl.AbstractImlGeneratorResult;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.Symbol;

/**
 * An interface for IML Generators extension point
 * 
 * @author Ayman Elkfrawy
 *
 */
public interface IImlGenerator {
	
	/**
	 * Return whether this generator can generate given query or not
	 * @param query the formula representing the query
	 * @return
	 */
	public boolean canGenerate(FolFormula query);
	
	/**
	 * Generates the given query and return the result of this generation
	 * @param query the formula representing the query to generate
	 * @param params any generator specific parameters needed
	 * @return the generation result as an object of {@link IImlGeneratorResult}. See {@link AbstractImlGeneratorResult}.
	 * @throws UnsupportedQueryException if the given symbol is not supported by this generator. See {@link #canGenerate(Symbol)}.
	 */
	public IImlGeneratorResult generate(FolFormula query, Map<String, String> params) throws UnsupportedQueryException;
	
	/**
	 * Generates a formula fragment that doesn't require generate a whole encoded model, but rather just string representation 
	 * of the given formula in the target model
	 * @param fragment
	 * @return the encoding of the given formula as String of the target model
	 */
	public String generateFragment(FolFormula fragment);
	
	public ModelClass getGneratedModelClass();
}
