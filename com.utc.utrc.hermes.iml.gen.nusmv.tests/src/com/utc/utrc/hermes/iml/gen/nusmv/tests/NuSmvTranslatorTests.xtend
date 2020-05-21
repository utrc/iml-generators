package com.utc.utrc.hermes.iml.gen.nusmv.tests

import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.InjectWith
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.ImlParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import com.utc.utrc.hermes.iml.tests.TestHelper
import org.junit.Test
import com.utc.utrc.hermes.iml.util.FileUtil
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.gen.common.systems.Systems
import com.utc.utrc.hermes.iml.gen.nusmv.generator.Configuration
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGenerator
import com.utc.utrc.hermes.iml.gen.nusmv.sms.Sms
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.util.ImlUtil
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGeneratorServices
import eu.fbk.tools.editor.nusmv.smv.util.SmvSwitch
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvModelInterface
import org.eclipse.xtext.serializer.ISerializer
import eu.fbk.tools.editor.nusmv.serializer.NuSMVSemanticSequencer
import eu.fbk.tools.editor.nusmv.serializer.NuSMVSyntacticSequencer
import com.google.inject.Injector
import org.junit.Before
import eu.fbk.tools.editor.nusmv.NuSMVStandaloneSetup
import org.eclipse.xtext.serializer.impl.Serializer

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class NuSmvTranslatorTests {
	
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject
	NuSmvGenerator gen ;
	
	@Inject
	NuSmvGeneratorServices generatorServices;
		
	
	@Test
	def void testTranslation() {
		
		var Model m = parse(FileUtil.readFileContent("models/fromaadl/UxASRespondsEvents_pkg.iml"),true) ;
		var NamedType smtype = m.findSymbol("UxAS_responds_dot_i") as NamedType;
		var NamedType spectype = m.findSymbol("UxAS_responds") as NamedType;		
		var NuSmvModel smv = gen.generateNuSmvModel(null,smtype) ;
		
		
		var output = generatorServices.serialize(smv);
		System.out.println(output);
		
		//var main = gen.getMainModel(smv,ImlCustomFactory.INST.createSimpleTypeReference(spectype),ImlCustomFactory.INST.createSimpleTypeReference(smtype))
		
		//System.out.println(generatorServices.serialize(main));
		
		
	}
	
	
}

