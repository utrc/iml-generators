/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.utc.utrc.hermes.iml.AbstractImlRuntimeModule;
import com.utc.utrc.hermes.iml.gen.smt.model.SmtModelProvider;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleFunDeclaration;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSmtFormula;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSmtModelProvider;
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSort;
import com.google.inject.TypeLiteral;

/**
 * @author SCHULZCH
 *
 */
public class SmtEncoderModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(new TypeLiteral<SmtModelProvider<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula>>(){}).
		to(SimpleSmtModelProvider.class);
		
	}
	
}
