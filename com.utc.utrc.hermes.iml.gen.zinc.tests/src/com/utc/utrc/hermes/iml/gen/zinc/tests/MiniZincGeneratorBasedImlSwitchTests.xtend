package com.utc.utrc.hermes.iml.gen.zinc.tests

import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.InjectWith
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.ImlParseHelper
import org.junit.Test
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.util.ImlUtil
import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGenerator
import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGeneratorServices

import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGeneratorBasedImlSwitch
import java.util.Map


@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class MiniZincGeneratorBasedImlSwitchTests  {
	
	@Inject
	ImlParseHelper parser;
	
//	@Inject extension ImlParseHelper
//	
//	@Inject extension ValidationTestHelper
//	
//	@Inject extension TestHelper
	
	@Inject
	MiniZincGenerator gen ;
	
	@Inject
	MiniZincGeneratorServices generatorServices;
	
	@Inject MiniZincGeneratorBasedImlSwitch mzModelVisitor;
	//@Inject AbstractModelAcceptor modelAcceptor;
	
	@Test
	def void testModelTranslation1() {
		val rs = parser.parseDir("models/happiness/", true);
		parser.assertNoErrors(rs);
		var query = ImlUtil.findModel(rs, "optimization.happiness.optimalWeek")
		var model = mzModelVisitor.caseModel(query as Model);
//		System.out.println("====================IML Model=====================");
//		System.out.println(model.toString);		
		System.out.println("====================CP SubModels======================");	
		for(String subModelName : mzModelVisitor.getSubModelList()){	
			System.out.println();
			System.out.println("**** sub-model: " + subModelName + "****" );	
			System.out.println(mzModelVisitor.printSubModel(subModelName));
		}
	}
	
		@Test
	def void testModelCloning() {
		val rs = parser.parseDir("models/happiness/", true);
		parser.assertNoErrors(rs);
		var query = ImlUtil.findModel(rs, "optimization.happiness.optimalWeek")
		var model = mzModelVisitor.caseModel(query as Model);
//		System.out.println("====================IML Model=====================");
//		System.out.println(model.toString);		
		System.out.println("====================CP SubModels======================");	
		for(String subModelName : mzModelVisitor.getSubModelList()){	
			System.out.println();
			System.out.println("**** sub-model: " + subModelName + "****" );	
			System.out.println(mzModelVisitor.printSubModel(subModelName));
			System.out.println();
			System.out.println("**** cloned sub-model of " + subModelName + "****" );	
			System.out.println(mzModelVisitor.cloneSubModel(subModelName));
		}
	}
		
}


