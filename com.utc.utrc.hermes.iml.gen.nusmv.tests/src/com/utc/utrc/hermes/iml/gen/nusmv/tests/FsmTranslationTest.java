package com.utc.utrc.hermes.iml.gen.nusmv.tests;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.testing.InjectWith;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Injector;
import com.utc.utrc.hermes.iml.ImlStandaloneSetup;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.nusmv.generator.Configuration;
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGenerator;
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGeneratorServices;
import com.utc.utrc.hermes.iml.gen.nusmv.generator.StandardLibProvider;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import org.eclipse.xtext.testing.XtextRunner ;
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider;


class FsmTranslationTest {

	@Test
	public void test() {
		Injector injector = ImlStandaloneSetup.getInjector();
		
		ImlTypeProvider typeprovider = injector.getInstance(ImlTypeProvider.class);

		ResourceSet resourceSet = new ResourceSetImpl();

		// Standard libraries
	    resourceSet.getResource(URI.createURI("../../iml/com.utc.utrc.hermes.iml.lib/src/iml/lang.iml"), true);
		resourceSet.getResource(URI.createURI("../../iml/com.utc.utrc.hermes.iml.lib/src/iml/software.iml"), true);
		resourceSet.getResource(URI.createURI("../../iml/com.utc.utrc.hermes.iml.lib/src/iml/connectivity.iml"), true);
		resourceSet.getResource(URI.createURI("../../iml/com.utc.utrc.hermes.iml.lib/src/iml/contracts.iml"), true);
		resourceSet.getResource(URI.createURI("../../iml/com.utc.utrc.hermes.iml.lib/src/iml/ports.iml"), true);
		resourceSet.getResource(URI.createURI("../../iml/com.utc.utrc.hermes.iml.lib/src/iml/fsm.iml"), true);		
		
//		resourceSet.getResource(URI.createURI("models/fromaadl/UxASNodeLibEvents.iml"), true);
//		resourceSet.getResource(URI.createURI("models/fromaadl/GenericService.iml"), true);
//		resourceSet.getResource(URI.createURI("models/fromaadl/GenericLastService.iml"), true);
		
		
		Resource translationunit = resourceSet.getResource(URI.createURI("models/fromaadl/UxASRespondsEvents_pkg.iml"), true);

		StandardLibProvider standard_libs = new StandardLibProvider(resourceSet);
		Configuration conf = new Configuration.Builder().BypassDelay(false).build();
		
		NuSmvGenerator gen = injector.getInstance(NuSmvGenerator.class);
		gen.setLibs(standard_libs);
		gen.setConf(conf);

		Model m = (Model) translationunit.getContents().get(0);
		NuSmvModel nm = new NuSmvModel();
		for (Symbol s : m.getSymbols()) {
			if (s instanceof NamedType) {
				if ( s.getName().equals("UxAS_responds_dot_i") )
				gen.generateType(nm, ImlCustomFactory.INST.createSimpleTypeReference(((NamedType) s)));
			}
		}

		NuSmvGeneratorServices generatorServices = injector.getInstance(NuSmvGeneratorServices.class);
		String serialized = generatorServices.serialize(nm);
		System.out.println(serialized);

	}

}
