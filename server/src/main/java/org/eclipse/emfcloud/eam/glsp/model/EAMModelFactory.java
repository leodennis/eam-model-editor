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
package org.eclipse.emfcloud.eam.glsp.model;

import org.eclipse.emfcloud.eam.enotation.Diagram;
import org.eclipse.emfcloud.eam.glsp.EAMEditorContext;
import org.eclipse.emfcloud.eam.glsp.EAMFacade;
import org.eclipse.glsp.api.action.kind.RequestModelAction;
import org.eclipse.glsp.api.factory.ModelFactory;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.builder.impl.GGraphBuilder;

public class EAMModelFactory implements ModelFactory {
	private static final String ROOT_ID = "sprotty";

	@Override
	public GModelRoot loadModel(RequestModelAction action, GraphicalModelState graphicalModelState) {
		EAMModelState modelState = EAMModelState.getModelState(graphicalModelState);
		graphicalModelState.setClientOptions(action.getOptions());

		EAMEditorContext context = new EAMEditorContext(modelState);

		modelState.setEditorContext(context);

		EAMFacade eamFacade = context.getEAMFacade();
		if (eamFacade == null) {
			return createEmptyRoot();
		}
		Diagram diagram = eamFacade.getDiagram();

		GModelRoot gmodelRoot = context.getGModelFactory().create(eamFacade.getEPackage());
		eamFacade.initialize(diagram, gmodelRoot);
		modelState.setRoot(gmodelRoot);
		return gmodelRoot;
	}

	private static GModelRoot createEmptyRoot() {
		return new GGraphBuilder(DefaultTypes.GRAPH)//
				.id(ROOT_ID) //
				.build();
	}

}
