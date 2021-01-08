package com.utc.utrc.hermes.iml.gen.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Ayman Elkfrawy
 *
 */
public class ImlGeneratorResult {
	
	private String generatedModel;
	private Map<String, String> originalToGeneratedNames;
	private Map<String, String> generatedToOriginalNames;
	
	public ImlGeneratorResult() {
		originalToGeneratedNames = new HashMap<>();
		generatedToOriginalNames = new HashMap<>();
	}
	
	
	public String getGeneratedModel() {
		return generatedModel;
	}


	public void setGeneratedModel(String generatedModel) {
		this.generatedModel = generatedModel;
	}


	/**
	 * Add variable name mapping between original model and generated model
	 * @param originalName the variable name in the original input model
	 * @param generatedName the variable name in the generated output model
	 */
	public void addMapping(String originalName, String generatedName) {
		originalToGeneratedNames.put(originalName, generatedName);
		generatedToOriginalNames.put(generatedName, originalName);
	}
	
	/**
	 * Get the original variable name of the input model given the generated name for it
	 * @param generatedName the name of the variable in the generated model
	 * @return the name of the variable in the original model, null if it doesn't exist
	 */
	public String getOriginalNameOf(String generatedName) {
		return generatedToOriginalNames.get(generatedName);
	}
	
	/**
	 * Get the generated variable name in the generated model given the original variable name for it
	 * @param originalName the name of the variable in the original input model
	 * @return the name of the variable in the generated model, null if it doesn't exist
	 */
	public String getGeneratedNameOf(String originalName) {
		return originalToGeneratedNames.get(originalName);
	}

}
