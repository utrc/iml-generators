package com.utc.utrc.hermes.iml.gen.nusmv.sally.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlUtil;

public class SallyState {

	private Map<String, SallySymbol> variables;
	private Map<String, SallySymbol> inputs;
	private Map<String, Integer> literals;
	private ImlStdLib stdLibs;

	public SallyState(ImlStdLib stdLibs) {
		this.stdLibs = stdLibs;
		variables = new HashMap<String, SallySymbol>();
		literals = new HashMap<>();
		inputs = new HashMap<>();
	}

	public void addVariable(SymbolDeclaration state) {
		addVariable("", state);
	}

	public void addVariable(String prefix, SymbolDeclaration state) {
		if (state.getType() instanceof SimpleTypeReference) {
			SimpleTypeReference tr = (SimpleTypeReference) state.getType();
			String name = prefix + "." + state.getName();
			if (ImlUtil.isEnum(tr.getType())) {
				variables.put(name, new SallySymbol(name, SallyType.Int));
				int index = literals.keySet().size() ;
				for(String l: ImlUtil.getLiterals(tr.getType())) {
					literals.put(tr.getType().getName() + "." + l, index++);
				}
			} else {
				if (tr.getType() == stdLibs.getBoolType()) {
					variables.put(name, new SallySymbol(name, SallyType.Bool));
				} else if (tr.getType() == stdLibs.getIntType()) {
					variables.put(name, new SallySymbol(name, SallyType.Int));
				} else if (tr.getType() == stdLibs.getRealType()) {
					variables.put(name, new SallySymbol(name, SallyType.Real));
				} else {
					TypingEnvironment typing = new TypingEnvironment(tr);
					for (Symbol s : tr.getType().getSymbols()) {
						if (s instanceof SymbolDeclaration) {
							SymbolDeclaration sbound = ImlCustomFactory.INST.createSymbolDeclaration(s.getName(),
									typing.bind(((SymbolDeclaration) s).getType()));
							addVariable(prefix + "." + state.getName(), sbound);
						}
					}
				}
			}
		}
	}

	public Map<String, SallySymbol> getVariables() {
		return variables;
	}

	public Map<String, Integer> getLiterals(){
		return literals;
	}
	
	@Override
	public String toString() {
		String retval = "Constants \n";
		for(String c : literals.keySet()) {
			retval += "\t" + c + " = " + literals.get(c).toString() + "\n" ;
		}
		retval += "State \n";
		for (SallySymbol s : variables.values()) {
			retval += "\t " + s.getName() + " : " + s.getType().toString() + "\n";
		}
		return retval;
	}

}
