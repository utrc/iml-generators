/*******************************************************************************
*Copyright (c) 2021 Raytheon Technologies. All rights reserved.
*See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.tests.encoding

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.gen.smt.encoding.ImlSmtEncoder
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.tests.TestHelper
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.runner.RunWith
import org.junit.Test
import static extension org.junit.Assert.*
import java.util.stream.Collectors
import com.utc.utrc.hermes.iml.util.FileUtil
import org.eclipse.emf.ecore.resource.ResourceSet
import com.utc.utrc.hermes.iml.gen.smt.tests.SmtTestInjectorProvider
import com.utc.utrc.hermes.iml.ImlParseHelper
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSort
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleFunDeclaration
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSmtFormula

@RunWith(XtextRunner)
@InjectWith(SmtTestInjectorProvider)
class ModelSmtEncodingTest {
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject ImlSmtEncoder<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula> encoder
	
	@Test
	def void testTest1GeneratedModelEncoder() {
		
		val files = FileUtil.readAllFilesUnderDir("./res/assessment2/test1");
		var ResourceSet rs;
		for (file : files) {
			if (rs !== null) {
				rs = file.parse(rs).eResource.resourceSet			 
			} else {
				rs = file.parse.eResource.resourceSet
			}
		}
		var Model swModel = null;
		for (resource : rs.resources) {
			if (resource.contents !== null && resource.contents.size > 0) {
				val model = resource.contents.get(0);
				if (model instanceof Model) {
					model.assertNoErrors
					if ((model as Model).name.equals("SWIMLAnnex")) {
						swModel = model;
					}
				}
			}
		}
		if (swModel !== null) {
			encode(swModel as Model, "MC_SW_dot_Impl")
			println(encoder.toString)
		}
		
	}
	
	@Test
	def void testAssessment3SW() {
		
		val files = FileUtil.readAllFilesUnderDir("./res/assessment3/SW_static.iml");
		var ResourceSet rs;
		for (file : files) {
			if (rs !== null) {
				rs = file.parse(rs).eResource.resourceSet			 
			} else {
				rs = file.parse.eResource.resourceSet
			}
		}
		var Model swModel = null;
		for (resource : rs.resources) {
			if (resource.contents !== null && resource.contents.size > 0) {
				val model = resource.contents.get(0);
				if (model instanceof Model) {
					model.assertNoErrors
					if ((model as Model).name.equals("examples.SW.static")) {
						swModel = model;
					}
				}
			}
		}
		if (swModel !== null) {
			encode(swModel as Model, "MC_SW_dot_Impl")
			println(encoder.toString)
		}
		
	}

	
	@Test
	def void testDocumentationExample() {
		
		val files = FileUtil.readAllFilesUnderDir("./res/docExample/UTRC_Explain_yes.iml");
		var ResourceSet rs;
		for (file : files) {
			if (rs !== null) {
				rs = file.parse(rs).eResource.resourceSet			 
			} else {
				rs = file.parse.eResource.resourceSet
			}
		}
		var Model swModel = null;
		for (resource : rs.resources) {
			if (resource.contents !== null && resource.contents.size > 0) {
				val model = resource.contents.get(0);
				if (model instanceof Model) {
					model.assertNoErrors
					if ((model as Model).name.equals("smt.example")) {
						swModel = model;
					}
				}
			}
		}
		if (swModel !== null) {
			encode(swModel as Model, "top_level_dot_Impl")
			println(encoder.toString)
		}
		
	}	
	@Test
	def void testAssessment3FlattenIml() {
		
		val files = FileUtil.readAllFilesUnderDir("./res/assessment3/flatten_iml.iml");
		var ResourceSet rs;
		for (file : files) {
			if (rs !== null) {
				rs = file.parse(rs).eResource.resourceSet			 
			} else {
				rs = file.parse.eResource.resourceSet
			}
		}
		var Model swModel = null;
		for (resource : rs.resources) {
			if (resource.contents !== null && resource.contents.size > 0) {
				val model = resource.contents.get(0);
				if (model instanceof Model) {
					model.assertNoErrors
					if ((model as Model).name.equals("p")) {
						swModel = model;
					}
				}
			}
		}
		if (swModel !== null) {
			encode(swModel as Model, null)
			println(encoder.toString)
		}
		
	}
	
	def encode(Model model, String ctName) {
		model.assertNoErrors
		if (ctName !== null) {
			encoder.encode(model.findSymbol(ctName))
		} else {
			encoder.encode(model)
		}
		// TODO make sure names are unique
		val distinctSorts = encoder.allSorts.map[it.name].stream.distinct.collect(Collectors.toList)
		assertEquals(distinctSorts.size, encoder.allSorts.size)
		val distinctFuncDecls = encoder.allFuncDeclarations.map[it.name].stream.distinct.collect(Collectors.toList)
		assertEquals(distinctFuncDecls.size, encoder.allFuncDeclarations.size)
		return model
	}
}
