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

import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.eam.glsp.EAMModelIndex;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.operation.Operation;
import org.eclipse.glsp.api.operation.kind.ApplyLabelEditOperation;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.server.operationhandler.BasicOperationHandler;

import EAM_Metamodel.EAM_Application;
import EAM_Metamodel.ApplicationType;
import EAM_Metamodel.EAM_DataCenter;
import EAM_Metamodel.EAM_Host;
import EAM_Metamodel.EAM_Node;
import EAM_Metamodel.EAM_Product;

public class EAMLabelEditOperationHandler extends BasicOperationHandler<ApplyLabelEditOperation>  {

	@Override
	public boolean handles(Operation execAction) {
		return execAction instanceof ApplyLabelEditOperation;
	}
	

	@Override
	protected void executeOperation(ApplyLabelEditOperation operation, GraphicalModelState modelState) {
		//EAMFacade facade = EAMModelState.getEcoreFacade(modelState);
		EAMModelIndex index = EAMModelState.getModelState(modelState).getIndex();

		// attribute modification
		GNode gnode = getOrThrow(index.findElementByClass(operation.getLabelId(), GNode.class), "ERROR"); 
		Optional<EObject> optional = index.getSemantic(gnode);
		if (optional.isPresent()) {
			if (optional.get() instanceof EAM_Node) {
				EAM_Node node = (EAM_Node) optional.get();
				String attributeName = operation.getLabelId().split("_")[1];

				switch (attributeName) {
					case "id":
						node.setId(operation.getText());
						break;
					case "name":
						node.setName(operation.getText());
						break;
					case "owner":
						((EAM_Product) node).setOwner(operation.getText());
						break;
					case "department":
						((EAM_Product) node).setDepartment(operation.getText());
							break;
					case "type":
						((EAM_Application) node).setType(ApplicationType.getByName(operation.getText()));
						break;
					case "version":
						((EAM_Application) node).setVersion(operation.getText());
						break;
					case "vendor":
						((EAM_Host) node).setVendor(operation.getText());
						break;
					case "location":
						((EAM_DataCenter) node).setLocation(operation.getText());
						break;
					default:
						throw new RuntimeException("Edit label: Unknown attribute: " + attributeName);
				}
				return;
			}
		}
	}

	@Override
	public String getLabel() {
		return "Edit label";
	}
}
