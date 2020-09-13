/*******************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *  
 *   This program and the accompanying materials are made available under the
 *   terms of the Eclipse Public License v. 2.0 which is available at
 *   http://www.eclipse.org/legal/epl-2.0.
 *  
 *   This Source Code may also be made available under the following Secondary
 *   Licenses when the conditions for such availability set forth in the Eclipse
 *   Public License v. 2.0 are satisfied: GNU General Public License, version 2
 *   with the GNU Classpath Exception which is available at
 *   https://www.gnu.org/software/classpath/license.html.
 *  
 *   SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ******************************************************************************/
package org.eclipse.emfcloud.eam.glsp.operationhandler;

import static org.eclipse.glsp.api.jsonrpc.GLSPServerException.getOrThrow;

import java.util.List;

import org.eclipse.emfcloud.eam.enotation.NotationElement;
import org.eclipse.emfcloud.eam.glsp.EAMEditorContext;
import org.eclipse.emfcloud.eam.glsp.EAMFacade;
import org.eclipse.emfcloud.eam.glsp.EAMModelIndex;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.Types;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.operation.Operation;
import org.eclipse.glsp.api.operation.kind.CreateEdgeOperation;
import org.eclipse.glsp.server.operationhandler.BasicOperationHandler;

import EAM_Metamodel.EAM_Application;
import EAM_Metamodel.EAM_DataCenter;
import EAM_Metamodel.EAM_Host;
import EAM_Metamodel.EAM_Landscape;
import EAM_Metamodel.EAM_Node;
import EAM_Metamodel.EAM_Product;

import com.google.common.collect.Lists;

public class CreateEdgeOperationHandler extends BasicOperationHandler<CreateEdgeOperation> {

	private List<String> handledElementTypeIds = Lists.newArrayList(Types.EDGE_ASSOCIATION, Types.EDGE_COMMUNICATION);

	public CreateEdgeOperationHandler() {
	}

	@Override
	public boolean handles(Operation execAction) {
		if (execAction instanceof CreateEdgeOperation) {
			CreateEdgeOperation action = (CreateEdgeOperation) execAction;
			return handledElementTypeIds.contains(action.getElementTypeId());
		}
		return false;
	}

	@Override
	protected void executeOperation(CreateEdgeOperation operation, GraphicalModelState modelState) {
		String elementTypeId = operation.getElementTypeId();

		EAMEditorContext context = EAMModelState.getEditorContext(modelState);
		EAMModelIndex modelIndex = context.getModelState().getIndex();
		EAMFacade facade = context.getEAMFacade();
		EAM_Node sourceNode = getOrThrow(modelIndex.getSemantic(operation.getSourceElementId(), EAM_Node.class),
				"No semantic Node found for source element with id " + operation.getSourceElementId());
		EAM_Node targetNode = getOrThrow(modelIndex.getSemantic(operation.getTargetElementId(), EAM_Node.class),
				"No semantic Node found for target element with id" + operation.getTargetElementId());

		//Diagram diagram = facade.getDiagram();

		if (elementTypeId.equals(Types.EDGE_COMMUNICATION)) {
			// Application --- Application
			if (sourceNode instanceof EAM_Application && targetNode instanceof EAM_Application) {
				EAM_Application source = (EAM_Application) sourceNode;
				EAM_Application target= (EAM_Application) targetNode;
				source.getApplication().add(target);
				target.getApplicationOpposite().add(source);
			}

		} else if (elementTypeId.equals(Types.EDGE_ASSOCIATION)) {

			// Product --- Application
			if (sourceNode instanceof EAM_Product && targetNode instanceof EAM_Application) {
				EAM_Product source = (EAM_Product) sourceNode;
				EAM_Application target = (EAM_Application) targetNode;
				source.getApplication().add(target);
				target.getProduct().add(source);
			} else if (targetNode instanceof EAM_Product && sourceNode instanceof EAM_Application) {
				EAM_Application source = (EAM_Application) sourceNode;
				EAM_Product target = (EAM_Product) targetNode;
				source.getProduct().add(target);
				target.getApplication().add(source);
			}

			// Application --- Host
			else if (sourceNode instanceof EAM_Application && targetNode instanceof EAM_Host) {
				EAM_Application source = (EAM_Application) sourceNode;
				EAM_Host target = (EAM_Host) targetNode;
				source.getHost().add(target);
				target.getApplication().add(source);
			} else if (targetNode instanceof EAM_Application && sourceNode instanceof EAM_Host) {
				EAM_Host source = (EAM_Host) sourceNode;
				EAM_Application target = (EAM_Application) targetNode;
				source.getApplication().add(target);
				target.getHost().add(source);
			}

			// Host --- DataCenter
			else if (sourceNode instanceof EAM_Host && targetNode instanceof EAM_DataCenter) {
				EAM_Host source = (EAM_Host) sourceNode;
				EAM_DataCenter target = (EAM_DataCenter) targetNode;
				target.getHost().add(source);
			
			} else if (targetNode instanceof EAM_Host && sourceNode instanceof EAM_DataCenter) {
				EAM_DataCenter source = (EAM_DataCenter) sourceNode;
				EAM_Host target = (EAM_Host) targetNode;
				source.getHost().add(target);
			}
		}
	}

	/*
	private EReference createReference(EClass source, EClass target, String elementTypeId) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setEType(target);
		reference.setName(target.getName().toLowerCase() + "s");
		if (elementTypeId.equals(Types.COMPOSITION)) {
			reference.setContainment(true);
		}
		source.getEStructuralFeatures().add(reference);
		return reference;

	}*/

	@Override
	public String getLabel() {
		return "Create EAM edge";
	}

}
