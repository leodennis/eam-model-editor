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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.graph.GModelIndex;
import org.eclipse.glsp.server.operationhandler.DeleteOperationHandler;

import EAM_Metamodel.EAM_Application;
import EAM_Metamodel.EAM_DataCenter;
import EAM_Metamodel.EAM_Host;
import EAM_Metamodel.EAM_Product;

public class EAMDeleteOperationHandler extends DeleteOperationHandler {
	@Override
	protected boolean delete(String elementId, GModelIndex index, GraphicalModelState graphicalModelState) {
		super.delete(elementId, index, graphicalModelState);
		
		EAMModelState modelState = EAMModelState.getModelState(graphicalModelState);
		
		// edge
		if (elementId.contains("_")) {
			String[] ids = elementId.split("_");
			EObject sourceNode = getOrThrow(modelState.getIndex().getSemantic(ids[0]), "Node with id " + ids[0] + " not found");
			EObject targetNode = getOrThrow(modelState.getIndex().getSemantic(ids[1]), "Node with id " + ids[1] + " not found");
			
			// Application --- Application
			if (sourceNode instanceof EAM_Application && targetNode instanceof EAM_Application) {
				EAM_Application source = (EAM_Application) sourceNode;
				EAM_Application target= (EAM_Application) targetNode;
				source.getApplication().remove(target);
				target.getApplicationOpposite().remove(source);
			}

			// Product --- Application
			else if (sourceNode instanceof EAM_Product && targetNode instanceof EAM_Application) {
				EAM_Product source = (EAM_Product) sourceNode;
				EAM_Application target = (EAM_Application) targetNode;
				source.getApplication().remove(target);
				target.getProduct().remove(source);
			} else if (targetNode instanceof EAM_Product && sourceNode instanceof EAM_Application) {
				EAM_Application source = (EAM_Application) sourceNode;
				EAM_Product target = (EAM_Product) targetNode;
				source.getProduct().remove(target);
				target.getApplication().remove(source);
			}

			// Application --- Host
			else if (sourceNode instanceof EAM_Application && targetNode instanceof EAM_Host) {
				EAM_Application source = (EAM_Application) sourceNode;
				EAM_Host target = (EAM_Host) targetNode;
				source.getHost().remove(target);
				target.getApplication().remove(source);
			} else if (targetNode instanceof EAM_Application && sourceNode instanceof EAM_Host) {
				EAM_Host source = (EAM_Host) sourceNode;
				EAM_Application target = (EAM_Application) targetNode;
				source.getApplication().remove(target);
				target.getHost().remove(source);
			}

			// Host --- DataCenter
			else if (sourceNode instanceof EAM_Host && targetNode instanceof EAM_DataCenter) {
				EAM_Host source = (EAM_Host) sourceNode;
				EAM_DataCenter target = (EAM_DataCenter) targetNode;
				target.getHost().remove(source);
			
			} else if (targetNode instanceof EAM_Host && sourceNode instanceof EAM_DataCenter) {
				EAM_DataCenter source = (EAM_DataCenter) sourceNode;
				EAM_Host target = (EAM_Host) targetNode;
				source.getHost().remove(target);
			}

		}

		// node
		else {
			modelState.getIndex().getSemantic(elementId).ifPresent(EcoreUtil::remove);
			modelState.getIndex().getNotation(elementId).ifPresent(EcoreUtil::remove);
		}
		
		return true;
	}
	
}
