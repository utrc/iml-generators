/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGenerator;
import com.utc.utrc.hermes.iml.gen.zinc.model.MiniZincModel;
import com.utc.utrc.hermes.iml.gen.zinc.model.SolutionTable;
import com.utc.utrc.hermes.iml.util.FileUtil;

public class SolverRunner {

	public static SolutionTable run(String model, String prefix, String typename) throws IOException {

		// save to file
		FileUtil.writeFileContent("d:\\Temp\\model.mzn", model);

		String cmd = "d:\\tools\\minizinc\\minizinc.exe --solver Chuffed d:\\Temp\\model.mzn";
		Process p = Runtime.getRuntime().exec(cmd);

		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuffer output = new StringBuffer();
		String line = null;

		while ((line = stdError.readLine()) != null) {
			output.append(line);
			output.append('\n');
		}

		if (output.length() == 0) {
			while ((line = stdInput.readLine()) != null) {
				output.append(line);
				output.append('\n');
			}
			return new SolutionTable(output.toString(),prefix,typename) ;
		} else {
			throw new IOException("The solver run into a problem : " + output.toString());
		}
	}

	public static SolutionTable run(MiniZincGenerator gen, String prefix, String typename) throws IOException {

		// save to file
		FileUtil.writeFileContent("d:\\Temp\\model.mzn", gen.getModelAsString());

		String cmd = "d:\\tools\\minizinc\\minizinc.exe --solver chuffed d:\\Temp\\model.mzn";
		Process p = Runtime.getRuntime().exec(cmd);

		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuffer output = new StringBuffer();
		String line = null;

		while ((line = stdError.readLine()) != null) {
			output.append(line);
			output.append('\n');
		}

		if (output.length() == 0) {
			while ((line = stdInput.readLine()) != null) {
				output.append(line);
				output.append('\n');
			}
			return new SolutionTable(gen,output.toString(),prefix,typename) ;
		} else {
			throw new IOException("The solver run into a problem : " + output.toString());
		}
	}
	
}
