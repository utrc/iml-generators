/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.model.simplesmt;

import java.util.Arrays;
import java.util.List;

import com.utc.utrc.hermes.iml.gen.smt.encoding.OperatorType;
import com.utc.utrc.hermes.iml.gen.smt.model.SmtModelProvider;

/**
 * This is a simple implementation of SmtModelProvider using simple SMT model
 *
 * @author Ayman Elkfrawy (elkfraaf@utrc.utc.com)
 * @author Gerald Wang (wangg@utrc.utc.com)
 */
public class SimpleSmtModelProvider implements SmtModelProvider<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula> {

	@Override
	public SimpleSort createSort(String sortName) {
		return new SimpleSort(sortName);
	}

	@Override
	public SimpleSort createHotSort(String sortName, SimpleSort domainSort, SimpleSort rangeSort) {
		return new SimpleSort(sortName, domainSort, rangeSort);
	}

	@Override
	public SimpleSort createTupleSort(String sortName, List<SimpleSort> sorts) {
		return new SimpleSort(sortName, sorts);
	}

	@Override
	public SimpleFunDeclaration createFuncDecl(String funName, List<SimpleSort> inputSorts, SimpleSort outputSort) {
		return new SimpleFunDeclaration(funName, inputSorts, outputSort);
	}

	@Override
	public SimpleFunDeclaration createConst(String funName, SimpleSort outputSort) {
		return new SimpleFunDeclaration(funName, null, outputSort);
	}

	@Override
	public SimpleSmtFormula createFormula(OperatorType op, List<SimpleSmtFormula> params) {
		return new SimpleSmtFormula(op, params);
	}

	@Override
	public SimpleSmtFormula createFormula(Object value) {
		return new SimpleSmtFormula(value);
	}

	@Override
	public SimpleSmtFormula createFormula(SimpleFunDeclaration funcDeclar, List<SimpleSmtFormula> params) {
		return new SimpleSmtFormula(funcDeclar, null, params, null);
	}

	@Override
	public SimpleSmtFormula createFormula(List<SimpleSmtFormula> params) {
		return new SimpleSmtFormula(null, params);
	}

	@Override
	public SimpleSmtFormula createFormula(String name, SimpleSort sort) {
		return new SimpleSmtFormula(null, Arrays.asList(createFormula(name), createFormula(sort.getQuotedName())));
	}

	@Override
	public SimpleSort createEnum(String sortName, List<String> enumList) {
		return new SimpleSort(sortName, enumList, null);
	}

	@Override
	public SimpleFunDeclaration createFuncDef(String funName, List<SimpleSmtFormula> inputParams, SimpleSort outputSort,
			SimpleSmtFormula funcDef) {
		return new SimpleFunDeclaration(funName, inputParams, outputSort, funcDef);
	}


}
