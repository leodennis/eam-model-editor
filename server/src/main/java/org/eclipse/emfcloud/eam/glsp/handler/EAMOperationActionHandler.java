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
package org.eclipse.emfcloud.eam.glsp.handler;

import java.util.List;
import java.util.Optional;

import org.eclipse.emfcloud.eam.glsp.EAMEditorContext;
import org.eclipse.emfcloud.eam.glsp.EAMRecordingCommand;
import org.eclipse.emfcloud.eam.glsp.gmodel.GModelFactory;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.operation.Operation;
import org.eclipse.glsp.api.action.kind.RequestBoundsAction;
import org.eclipse.glsp.api.action.kind.SetDirtyStateAction;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.model.GraphicalModelState;

import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.server.actionhandler.OperationActionHandler;

public class EAMOperationActionHandler extends OperationActionHandler {

	
	@Override
	public List<Action> executeAction(Operation operation, GraphicalModelState modelState) {
		Optional<? extends OperationHandler> operationHandler = operationHandlerRegistry.get(operation);
		if (operationHandler.isPresent()) {
			return executeHandler(operation, operationHandler.get(), modelState);
		}
		return none();
	}

	@Override
	protected List<Action> executeHandler(Operation operation, OperationHandler handler,
			GraphicalModelState graphicalModelState) {
		EAMModelState modelState = EAMModelState.getModelState(graphicalModelState);
		EAMEditorContext context = modelState.getEditorContext();
		String label = handler.getLabel();
		EAMRecordingCommand command = new EAMRecordingCommand(context, label,
				() -> handler.execute(operation, modelState));
		modelState.execute(command);
		GModelRoot newRoot = new GModelFactory(modelState).create();

		return List.of(new RequestBoundsAction(newRoot), new SetDirtyStateAction(modelState.isDirty()));
	}
}
