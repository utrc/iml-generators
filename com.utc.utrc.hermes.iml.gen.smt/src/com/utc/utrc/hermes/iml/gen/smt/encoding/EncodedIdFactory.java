package com.utc.utrc.hermes.iml.gen.smt.encoding;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

import com.google.inject.Inject;


/**
 * A factory responsible for creating EncodedId for any IML object
 * 
 * @author Ayman Elkfrawy (elkfraaf@utrc.utc.com)
 * @author Gerald Wang (wangg@utrc.utc.com)
 */
public class EncodedIdFactory {

	@Inject IQualifiedNameProvider qnp ;
	int lastId = 0;
	
	// For objects that don't have standard unique name
	Map<EObject, EncodedId> specialIdList = new HashMap<>();
	
	public EncodedId createEncodedId(EObject imlObject, EObject container) {
		EncodedId id = new EncodedId(imlObject, container, qnp);
		if (id.getName() == null || id.getName().isEmpty()) {
			if (specialIdList.containsKey(imlObject)) {
				return specialIdList.get(imlObject);
			} else {
				id.setName("__id_" + lastId++);
				specialIdList.put(imlObject, id);
			}
		}
		return id;
	}
	
	public String getStringId(EObject imlObject, EObject container) {
		return createEncodedId(imlObject, container).stringId();
	}
}
