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
package org.eclipse.emfcloud.eam.glsp.operationhandler;

import org.eclipse.emfcloud.eam.enotation.Shape;
import org.eclipse.emfcloud.eam.glsp.EAMModelIndex;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.operation.kind.ChangeBoundsOperation;
import org.eclipse.glsp.api.types.ElementAndBounds;
import org.eclipse.glsp.graph.GDimension;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.server.operationhandler.BasicOperationHandler;

public class EAMChangeBoundsOperationHandler extends BasicOperationHandler<ChangeBoundsOperation> {

	@Override
	public void executeOperation(ChangeBoundsOperation changeBoundsOperation, GraphicalModelState graphicalModelState) {
		EAMModelState modelState = EAMModelState.getModelState(graphicalModelState);
		applyBounds(changeBoundsOperation, modelState.getIndex());
	}

	private void applyBounds(ChangeBoundsOperation action, EAMModelIndex index) {
		for (ElementAndBounds element : action.getNewBounds()) {
			index.getNotation(element.getElementId(), Shape.class)
					.ifPresent(notationElement -> changeElementBounds(notationElement, element.getNewSize(),
							element.getNewPosition()));
		}
	}

	private void changeElementBounds(Shape element, GDimension dimension, GPoint position) {
		if (position != null) {
			element.setPosition(position);
		}
		if (dimension != null) {
			element.setSize(dimension);
		}
	}

	@Override
	public String getLabel() {
		return "Change bounds";
	}
}