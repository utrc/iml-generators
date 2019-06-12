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

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class NuSmvTranslatorTests {
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject 
	Systems sys ;
	
	
	@Test
	def void testTranslation() {
		
		var Model m = parse(FileUtil.readFileContent("models/fromaadl/UxASRespondsEvents_pkg.iml"),true) ;
		sys.process(m) ;
		System.out.println(sys.toString)
		
	}
}

