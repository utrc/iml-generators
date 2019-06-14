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
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Systems
import com.utc.utrc.hermes.iml.gen.nusmv.generator.Configuration
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGenerator
import com.utc.utrc.hermes.iml.gen.nusmv.sms.Sms
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.util.ImlUtil
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class NuSmvTranslatorTests {
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject 
	Systems sys ;
	
	@Inject 
	Sms sms ;
	
	@Inject
	NuSmvGenerator gen ;
	
		
	@Test
	def void testTranslation() {
		
		var Model m = parse(FileUtil.readFileContent("models/fromaadl/UxASRespondsEvents_pkg.iml"),true) ;
		sys.process(m) ;
		System.out.println(sys.toString)
		sms.systems = sys;
		sms.process(m);
		gen.sms = sms;
		var NamedType smtype = m.findSymbol("UxAS_responds_dot_i") as NamedType;
		var NuSmvModel smv = new NuSmvModel() ;
		gen.generateStateMachine(smv,sms.getStateMachine(ImlCustomFactory.INST.createSimpleTypeReference(smtype))) ;
		
	}
}

