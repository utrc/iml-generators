/*******************************************************************************
*Copyright (c) 2021 Raytheon Technologies. All rights reserved.
*See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.tests

import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.InjectWith
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.google.inject.Inject
import org.junit.Test


import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGeneratorBasedImlSwitch
import com.utc.utrc.hermes.iml.gen.zinc.utils.MiniZincQuerySolver

import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class MiniZincGeneratorBasedImlSwitchTests  {	
	@Inject MiniZincGeneratorBasedImlSwitch mzModelVisitor;
	@Inject MiniZincQuerySolver mzQuerySolver;
	
	@Test
	def void testModelTranslation1() {
		mzModelVisitor.translateIMLModel2CPModel("models/happiness/", "optimization.happiness.optimalWeek"); 	
		System.out.println("====================CP SubModels======================");	
		for(String subModelName : mzModelVisitor.getSubModelList()){	
			System.out.println();
			System.out.println("**** sub-model: " + subModelName + "****" );	
			System.out.println(mzModelVisitor.printSubModel(subModelName));
		}
	}
	
	@Test
	def void testModelCloning() {
		mzModelVisitor.translateIMLModel2CPModel("models/happiness/", "optimization.happiness.optimalWeek"); 
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
	
	@Test
	def void testFlatzincModelExport() {
		//Convert the IML models to CP models
		val pathIML = "models/happiness/";	
		val modelName = "optimization.happiness.optimalWeek";
		mzModelVisitor.translateIMLModel2CPModel(pathIML, modelName); 
		//Export and compile the CP model of a given query
		val queryName = "optimization.happiness.happiness";
		mzModelVisitor.ExportModel(queryName);
		
//		val directoryName = pathIML + "\\mzn" 
//		val directory = new File(directoryName);
//      val fileName = queryName.replace(".", "_");
//      val mznFileName = directory+ "\\" + fileName +".mzn"	
		
		//assert whether the file exists

	}
	
	@Test
	def void testRunMinizincModel() {
		val pathIML = "models/happiness/";	
		val modelName = "optimization.happiness.optimalWeek";
		mzQuerySolver.initilize(pathIML, modelName);
		 	
		val queryName = "optimization.happiness.happiness";
		mzQuerySolver.SolveQuery("optimization.happiness.happiness");
	}
		
}


