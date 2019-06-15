package com.sri.iml.gen.mcmt.tests;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.google.inject.Injector;
import com.utc.utrc.hermes.iml.ImlStandaloneSetup;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.sri.iml.gen.mcmt.GeneratorException;
import com.sri.iml.gen.mcmt.MCMTGenerator;

import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.Symbol;

public class SmTranslationTest {

	public static void main(String[] args) {
		Injector injector = ImlStandaloneSetup.getInjector();
		
		ResourceSet resourceSet = new ResourceSetImpl();

		// Standard libraries
	    resourceSet.getResource(URI.createURI("../../HERMES-IML/com.utc.utrc.hermes.iml.lib/src/iml/lang.iml"), true);
		resourceSet.getResource(URI.createURI("../../HERMES-IML/com.utc.utrc.hermes.iml.lib/src/iml/software.iml"), true);
		resourceSet.getResource(URI.createURI("../../HERMES-IML/com.utc.utrc.hermes.iml.lib/src/iml/connectivity.iml"), true);
		resourceSet.getResource(URI.createURI("../../HERMES-IML/com.utc.utrc.hermes.iml.lib/src/iml/contracts.iml"), true);
		resourceSet.getResource(URI.createURI("../../HERMES-IML/com.utc.utrc.hermes.iml.lib/src/iml/ports.iml"), true);
		resourceSet.getResource(URI.createURI("../../HERMES-IML/com.utc.utrc.hermes.iml.lib/src/iml/fsm.iml"), true);		
		
		Resource translationunit = resourceSet.getResource(URI.createURI("models/Sm1.iml"), true);

		MCMTGenerator gen = injector.getInstance(MCMTGenerator.class);
		
		Model m = (Model) translationunit.getContents().get(0);
		for (Symbol s : m.getSymbols()) {
			if (s instanceof NamedType) {
				try {
					System.out.println(gen.generate(ImlCustomFactory.INST.createSimpleTypeReference(((NamedType) s))));
				} catch (GeneratorException e) {
					System.out.println("Could not convert to MCMT the following:\n" + m + "\n because: "+ e);
				}
			}
		}
	}

}
