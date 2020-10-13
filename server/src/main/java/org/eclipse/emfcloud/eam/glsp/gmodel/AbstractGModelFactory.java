/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.eam.glsp.gmodel;

import java.util.UUID;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.glsp.graph.GModelElement;
import org.emfcloud.compare.model_comparison.UUID_Provider;

public abstract class AbstractGModelFactory<T extends EObject, E extends GModelElement> {

	protected EAMModelState modelState;

	public AbstractGModelFactory(EAMModelState modelState) {
		this.modelState = modelState;
	}

	public abstract E create(T semanticElement);

	public E create(T semanticElement, String name, int value) { return null; }

	protected String toId(EObject semanticElement) {
		String id = modelState.getIndex().getSemanticId(semanticElement).orElse(null);

		if (id == null ) {
			id = UUID.randomUUID().toString();
			modelState.getIndex().indexSemantic(id, semanticElement);
		}

		if (modelState.getClientOptions().get("useStaticIds") != null && modelState.getClientOptions().get("useStaticIds").equals("true")) {
			id = UUID_Provider.getUUID(semanticElement);
		}
		
		return id;
	}

}
