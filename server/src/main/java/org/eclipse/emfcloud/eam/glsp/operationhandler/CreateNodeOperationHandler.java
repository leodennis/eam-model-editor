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

import java.util.List;
import java.util.function.Function;

import org.eclipse.emfcloud.eam.enotation.Diagram;
import org.eclipse.emfcloud.eam.enotation.Shape;
import org.eclipse.emfcloud.eam.glsp.EAMEditorContext;
import org.eclipse.emfcloud.eam.glsp.EAMFacade;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.Types;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.operation.Operation;
import org.eclipse.glsp.api.operation.kind.CreateNodeOperation;

import org.eclipse.glsp.graph.GraphPackage;
import org.eclipse.glsp.server.operationhandler.BasicOperationHandler;

import EAM_Metamodel.EAM_Landscape;
import EAM_Metamodel.EAM_MetamodelFactory;
import EAM_Metamodel.EAM_Node;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class CreateNodeOperationHandler extends BasicOperationHandler<CreateNodeOperation> {

	private List<String> handledElementTypeIds = Lists.newArrayList(Types.NODE_APPLICATION, Types.NODE_PRODUCT,
			Types.NODE_HOST, Types.NODE_DATACENTER);

	public CreateNodeOperationHandler() {
	}

	@Override
	public boolean handles(Operation execAction) {
		if (execAction instanceof CreateNodeOperation) {
			CreateNodeOperation action = (CreateNodeOperation) execAction;
			return handledElementTypeIds.contains(action.getElementTypeId());
		}
		return false;
	}

	@Override
	protected void executeOperation(CreateNodeOperation operation, GraphicalModelState modelState) {
		Preconditions.checkArgument(operation instanceof CreateNodeOperation);
		CreateNodeOperation action = (CreateNodeOperation) operation;
		String elementTypeId = action.getElementTypeId();
		EAMEditorContext context = EAMModelState.getEditorContext(modelState);
		EAMFacade facade = context.getEAMFacade();
		EAM_Landscape ePackage = facade.getEPackage();
		EAM_Node node = createNode(elementTypeId);

		setName(node, modelState);
		ePackage.getNode().add(node);
		Diagram diagram = facade.getDiagram();
		Shape shape = facade.initializeShape(node);
		if (action.getLocation() != null && action.getLocation().isPresent()) {
			shape.setPosition(action.getLocation().get());
		}
		diagram.getElements().add(shape);
	}

	protected void setName(EAM_Node node, GraphicalModelState modelState) {
		Function<Integer, String> nameProvider = i -> "New" + node.eClass().getName() + i;
		int nodeCounter = modelState.getIndex().getCounter(GraphPackage.Literals.GNODE, nameProvider);
		node.setName(nameProvider.apply(nodeCounter));
	}

	private EAM_Node createNode(String elementTypeId) {
		if (elementTypeId.equals((Types.NODE_APPLICATION))) {
			return EAM_MetamodelFactory.eINSTANCE.createApplication();
		} else if (elementTypeId.equals((Types.NODE_PRODUCT))) {
			return EAM_MetamodelFactory.eINSTANCE.createProduct();
		} else if (elementTypeId.equals((Types.NODE_HOST))) {
			return EAM_MetamodelFactory.eINSTANCE.createHost();
		} else if (elementTypeId.equals((Types.NODE_DATACENTER))) {
			return EAM_MetamodelFactory.eINSTANCE.createDataCenter();
		}
		return null;
	}

	@Override
	public String getLabel() {
		return "Create EAM node";
	}

}
