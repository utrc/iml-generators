/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.encoding;

public class EncodeConfig {
	private boolean allowFunDef = true;
	private boolean allowQuantifiers = true;
	private boolean allowFuncDecl = true;
	private boolean z3EnumStyle = true;
	
	private EncodeConfig(Builder builder) {
		this.allowFuncDecl = builder.allowFuncDecl;
		this.allowFunDef = builder.allowFunDef;
		this.allowQuantifiers = builder.allowQuantifiers;
		this.z3EnumStyle = builder.z3EnumStyle;
	}
	
	public boolean allowQuantifiers() {
		return allowQuantifiers;
	}

	public boolean allowFuncDecl() {
		return allowFuncDecl;
	}

	public boolean allowFunDef() {
		return allowFunDef;
	}

	public static Builder Builder() {
		return new Builder();
	}
	
	public boolean z3EnumStyle() {
		return z3EnumStyle;
	}
	
	public static class Builder {
		private boolean allowQuantifiers = true;
		private boolean allowFuncDecl = true;
		private boolean allowFunDef = true;
		private boolean z3EnumStyle = true;
		
		public EncodeConfig build() {
			return new EncodeConfig(this);
		}
		
		public Builder allowQuantifiers(boolean allowQuantifiers) {
			this.allowQuantifiers = allowQuantifiers;
			return this;
		}
		
		public Builder allowFuncDecl(boolean allowFuncDecl) {
			this.allowFuncDecl = allowFuncDecl;
			return this;
		}
		
		public Builder allowFunDef(boolean allowFunDef) {
			this.allowFunDef = allowFunDef;
			return this;
		}
		
		public Builder z3EnumStyle(boolean z3EnumStyle) {
			this.z3EnumStyle = z3EnumStyle;
			return this;
		}
	}
	
}
