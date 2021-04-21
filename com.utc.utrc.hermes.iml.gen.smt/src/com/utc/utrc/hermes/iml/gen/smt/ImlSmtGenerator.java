/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt;

import java.util.Map;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.gen.common.IImlGenerator;
import com.utc.utrc.hermes.iml.gen.common.IImlGeneratorResult;
import com.utc.utrc.hermes.iml.gen.common.ModelClass;
import com.utc.utrc.hermes.iml.gen.common.UnsupportedQueryException;
import com.utc.utrc.hermes.iml.gen.common.impl.AbstractImlGeneratorResult;
import com.utc.utrc.hermes.iml.gen.smt.encoding.ImlSmtEncoder;
import com.utc.utrc.hermes.iml.gen.smt.encoding.SMTEncodingException;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleFunDeclaration;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSmtFormula;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSort;
import com.utc.utrc.hermes.iml.iml.FolFormula;
  
public class ImlSmtGenerator implements IImlGenerator {
	
	@Inject ImlSmtEncoder<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula> encoder;

	@Override
	public boolean canGenerate(FolFormula query) {
		return true;
	}

	@Override
	public IImlGeneratorResult generate(FolFormula query, Map<String, String> params)
			throws UnsupportedQueryException {
		encoder.reset();
		AbstractImlGeneratorResult result = new AbstractImlGeneratorResult(getGneratedModelClass());
		try {
			encoder.encodeFormula(query, null);
			result.setGeneratedModel(encoder.toString());
		} catch (SMTEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String generateFragment(FolFormula fragment) {
		try {
			SimpleSmtFormula encodedFormula = encoder.encodeFormula(fragment, null);
			return encodedFormula.toString();
		} catch (SMTEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public ModelClass getGneratedModelClass() {
		return ModelClass.SMT;
	}

}
