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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.Types;
import org.eclipse.glsp.graph.GLabel;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.emfcloud.compare.model_comparison.UUID_Provider;

import EAM_Metamodel.EAM_Application;
import EAM_Metamodel.EAM_Node;

public class LabelFactory extends AbstractGModelFactory<EObject, GLabel> {

	public LabelFactory(EAMModelState modelState) {
		super(modelState);
	}

	public GLabel create(EObject semanticElement) {
		return null;
	}

	public GLabel create(EAM_Node node, String name, String value, EAttribute attribute) {
		String type = Types.ATTRIBUTE;
		if (node instanceof EAM_Application) {
			if (name.equals("type")) {
				type = Types.ATTRIBUTE_PRODUCT_TYPE;
			}
		}
		if (value.trim().isEmpty()) {
			value = "\u00A0\u00A0\u00A0"; // otherwise label width would be 0
		}

		String id = UUID.randomUUID().toString() + "_" + name;
		if (modelState.getClientOptions().get("useStaticIds") != null && modelState.getClientOptions().get("useStaticIds").equals("true")) {
			id = UUID_Provider.getUUID(node, attribute);
		}
		
		GLabelBuilder labelBuilder = new GLabelBuilder(type) //
				.id(id)//
				.text(value);

		return labelBuilder.build();
	}

	public GLabel createAttributeName(String name) {
		String label = String.format("%s:\u00A0", name);
		return new GLabelBuilder(Types.ATTRIBUTE_NAME) //
				.id(UUID.randomUUID().toString())//
				.text(label) //
				.build();
	}

}
