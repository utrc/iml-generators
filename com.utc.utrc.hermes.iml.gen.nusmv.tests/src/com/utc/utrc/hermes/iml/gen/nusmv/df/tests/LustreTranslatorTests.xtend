package com.utc.utrc.hermes.iml.gen.nusmv.df.tests

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
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGeneratorServices
import com.utc.utrc.hermes.iml.gen.nusmv.df.SynchDf
import com.utc.utrc.hermes.iml.gen.nusmv.df.generator.LustreGenerator
import com.utc.utrc.hermes.iml.gen.nusmv.df.generator.LustreGeneratorServices
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreModel

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class LustreTranslatorTests {
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject 
	Systems sys ;
	
	@Inject 
	SynchDf sdf ;
	
	@Inject
	LustreGenerator gen ;
	
	
		
	@Test
	def void testTranslation() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/filter.iml"),true) ;
		sys.process(m) ;
		System.out.println(sys.toString)
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Filter") as NamedType;
		//var NamedType spectype = m.findSymbol("UxAS_responds") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
	
	
		var output = gen.serialize(lus);
		System.out.println(output);
		
		//var main = gen.getMainModel(smv,ImlCustomFactory.INST.createSimpleTypeReference(spectype),ImlCustomFactory.INST.createSimpleTypeReference(smtype))
		
		//System.out.println(generatorServices.serialize(main));
		
		
	}
}

