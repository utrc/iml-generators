package com.utc.utrc.hermes.iml.gen.smt.encoding;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

import com.utc.utrc.hermes.iml.iml.Alias;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Inclusion;
import com.utc.utrc.hermes.iml.iml.InstanceConstructor;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.util.ImlUtil;
/**
 * Encodes IML types in a way that guarantee that each unique type has a unique ID
 * This should hide the way it generates the unique id for each IML object
 *
 * @author Ayman Elkfrawy (elkfraaf@utrc.utc.com)
 * @author Gerald Wang (wangg@utrc.utc.com)
 */
public class EncodedId {
	
	private String containerFqn;
	private String name;
	
	EObject imlObject;
	EObject _imlContainer;

	public static String DEFAULT_CONTAINER = "__unnamed__";
	public static String ASSERTION_DEFAULT_NAME="__assertion_";
	
	/**
	 * Create a unique EncoderId for each unique IML Object. The same IML type should return same EncoderID
	 * for example: {@code Int ~> Real}type declared in different symbols should return the same EncoderId (eId1.equals(eId2) is true)
	 * Current implementation uses the string of the actual type to generate unique id, for example the type {@code Int ~> Real}
	 * will generate string id with "Int~>Real"
	 * The method uses the imlContainer if not null, otherwise, it uses the eContainer of the given imlEObject
	 * @param imlEObject
	 * @param imlContainer
	 * @param qnp
	 */
	public EncodedId(EObject imlEObject, EObject imlContainer, IQualifiedNameProvider qnp) {
		this.imlObject = imlEObject;
		
		if (imlContainer != null) {
			this.containerFqn = getContainerFqn(imlContainer, qnp);
			this._imlContainer = imlContainer;
		}
		
		if (imlEObject instanceof Model) {
			name = ((Model) imlEObject).getName();
		}
		if (imlEObject instanceof NamedType) {
			if (imlContainer == null) {
				this.containerFqn = getContainerFqn(imlEObject.eContainer(), qnp);
			}
			name = ((Symbol) imlEObject).getName();
		} else if (imlEObject instanceof ImlType) {
			// use the serialization as name 
			if (imlEObject instanceof SimpleTypeReference && ((SimpleTypeReference) imlEObject).getTypeBinding().size() == 0) {
				NamedType type = ((SimpleTypeReference) imlEObject).getType();
				if (imlContainer == null) {
					this._imlContainer = type.eContainer();
					this.containerFqn = getContainerFqn(this._imlContainer, qnp);
				}
				name = type.getName();
			} else {
				this.containerFqn = DEFAULT_CONTAINER;
				// Use the name exactly as declared 					
				name = ImlUtil.getTypeNameManually((ImlType) imlEObject, qnp);
			}
		} else if (imlEObject instanceof AtomicRelation) {
			if (imlContainer == null) {
				this._imlContainer = ((AtomicRelation) imlEObject).getRelation().eContainer();
				this.containerFqn = getContainerFqn(this._imlContainer, qnp);
			}
			if (((AtomicRelation) imlEObject).getRelation() instanceof Alias) {
				name = "alias_" + ImlUtil.getTypeNameManually(((AtomicRelation) imlEObject).getRelatedType(), qnp);
			} else if (((AtomicRelation) imlEObject).getRelation() instanceof Inclusion) {
				name = "extends_" + ImlUtil.getTypeNameManually(((AtomicRelation) imlEObject).getRelatedType(), qnp);
			}
		} else if (imlEObject instanceof SymbolDeclaration) {
			if (imlContainer == null) {
				this._imlContainer = imlEObject.eContainer();
				this.containerFqn = getContainerFqn(this._imlContainer, qnp);
			}
			if (imlEObject instanceof Assertion) {
				if (((SymbolDeclaration) imlEObject).getName() != null && !((SymbolDeclaration) imlEObject).getName().isEmpty()) {
					name = ((SymbolDeclaration) imlEObject).getName();
				} else {
					EObject eContainer = imlEObject.eContainer();
					int index = 0;
					if (eContainer instanceof Model) {
						index = ((Model) eContainer).getSymbols().indexOf(imlEObject);
					} else { // Should be NamedType
						index = ((NamedType) eContainer).getSymbols().indexOf(imlEObject);
					}
					name = ASSERTION_DEFAULT_NAME + index;
				}
			} else {
				name = ((SymbolDeclaration) imlEObject).getName();
			}
		} else if (imlEObject instanceof SymbolReferenceTerm) {
			if (imlContainer == null) {
				this._imlContainer = ((SymbolReferenceTerm) imlEObject).getSymbol().eContainer();
				this.containerFqn = getContainerFqn(this._imlContainer, qnp);
			}
			name = ((SymbolReferenceTerm) imlEObject).getSymbol().getName();
			if (!((SymbolReferenceTerm) imlEObject).getTypeBinding().isEmpty()) { // Add the symbolref type binding to the name
				name = name + "<";
				name = name + ((SymbolReferenceTerm) imlEObject).getTypeBinding().stream()
					.map(type -> ImlUtil.getTypeName(type, qnp))
					.reduce((acc, curr) ->  acc + ", " +  curr).get();
				name = name + ">";
			}
		} else if (imlEObject instanceof InstanceConstructor) {
			this.containerFqn = DEFAULT_CONTAINER;
			name = "__some_" + ImlUtil.getTypeName((ImlType) imlContainer, qnp) 
						+ "_" + System.identityHashCode(imlEObject); // Use hashcode to identify each some
		}
	}
	
	private String getContainerFqn(EObject container, IQualifiedNameProvider qnp) {
		if (container instanceof ImlType) {
			return ImlUtil.getTypeName((ImlType) container, qnp);
		} else if (container instanceof NamedType || container instanceof Model) {
			return qnp.getFullyQualifiedName(container).toString();
		} else {
			return "";
		}
	}

	public EObject getImlObject() {
		return imlObject;
	}

	public void setImlObject(EObject imlObject) {
		this.imlObject = imlObject;
	}
	
	public EObject getImlContainer() {
		return _imlContainer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containerFqn == null) ? 0 : containerFqn.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EncodedId))
			return false;
		if ((containerFqn == null && ((EncodedId) obj).getContainer() != null) ||
			(containerFqn != null && ((EncodedId) obj).getContainer() == null)) {
			return false;
		}
		if (containerFqn != null && !containerFqn.equals(((EncodedId) obj).getContainer()))
			return false;
		if (!name.equals(((EncodedId) obj).getName()))
			return false;
		
		return true;
	}

	public String getContainer() {
		return containerFqn;
	}

	public void setContainer(String container) {
		this.containerFqn = container;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String stringId() {
		if (containerFqn == null || containerFqn.isEmpty()) {
			return name;
		} else {
			return (containerFqn.toString() + "." + name);
		}
	}
	
}
