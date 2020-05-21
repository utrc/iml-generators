package com.utc.utrc.hermes.iml.gen.nusmv.generator;

import org.eclipse.emf.ecore.EObject;

public class NuSmvGeneratorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EObject element;
	private String message ;
	
	public EObject getElement() {
		return element;
	}

	public String getMessage() {
		return message;
	}

	public NuSmvGeneratorException(EObject element, String message) {
		this.element = element ;
		this.message = message ;
	}
	
}
