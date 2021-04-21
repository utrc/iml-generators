/*******************************************************************************
*Copyright (c) 2021 Raytheon Technologies. All rights reserved.
*See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.utils


import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGeneratorBasedImlSwitch

import com.google.inject.Inject
import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader

public class MiniZincQuerySolver{
	
	
	@Inject MiniZincGeneratorBasedImlSwitch mzModelVisitor;
	private String pathIML;
	def public void initilize(String pathIML, String modelName) {
		this.pathIML=pathIML
		mzModelVisitor.translateIMLModel2CPModel(pathIML, modelName); 
	}
		
	def public void SolveQuery(String queryName){
		
		val directoryName = pathIML + "\\mzn\\tmp" 
		mzModelVisitor.ExportModel(queryName, directoryName);
		
		val directory = new File(directoryName);
        val fileName = queryName.replace(".", "_");
        val mznFileName = directory+ "\\" + fileName +".mzn"	
        		
		//run minizinc to solve/optimize the query model
		val cmd = "minizinc --solver Gecode "+mznFileName;
		val p = Runtime.getRuntime().exec(cmd);
				
		val stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		val stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		//print solver outputs
		System.out.println("Outputs:");
		var line = stdInput.readLine();
		while (line != null) {
				System.out.println(line);
				line = stdInput.readLine();
		}
		
		System.out.println("Errors:");	
		line = stdError.readLine();
		while (line != null) {
				System.out.println(line);
				line = stdError.readLine();
		}
		
		//remove all the files in the folder
		for (File file : directory.listFiles())
			file.delete();
    	directory.delete();
	}
}
