/********************************************************************************
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.eam.glsp;

import org.eclipse.emfcloud.eam.glsp.handler.EAMOperationActionHandler;
import org.eclipse.emfcloud.eam.glsp.handler.EAMSaveModelActionHandler;
import org.eclipse.emfcloud.eam.glsp.handler.EAMTriggerCompareActionHandler;
import org.eclipse.emfcloud.eam.glsp.handler.EAMGetApplicationTypesActionHandler;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelFactory;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelStateProvider;
import org.eclipse.emfcloud.eam.glsp.operationhandler.CreateEdgeOperationHandler;
import org.eclipse.emfcloud.eam.glsp.operationhandler.CreateNodeOperationHandler;
import org.eclipse.emfcloud.eam.glsp.operationhandler.EAMChangeBoundsOperationHandler;
import org.eclipse.emfcloud.eam.glsp.operationhandler.EAMLabelEditOperationHandler;
import org.eclipse.emfcloud.eam.glsp.operationhandler.EAMDeleteOperationHandler;
import org.eclipse.emfcloud.eam.glsp.palette.EAMToolPaletteItemProvider;
import org.eclipse.emfcloud.eam.glsp.registry.EAMDIOperationHandlerRegistry;
import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.configuration.ServerConfiguration;
import org.eclipse.glsp.api.diagram.DiagramConfiguration;
import org.eclipse.glsp.api.factory.ModelFactory;
import org.eclipse.glsp.api.handler.ActionHandler;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.layout.ILayoutEngine;
import org.eclipse.glsp.api.model.ModelStateProvider;
import org.eclipse.glsp.api.provider.ToolPaletteItemProvider;
import org.eclipse.glsp.api.registry.OperationHandlerRegistry;
import org.eclipse.glsp.server.actionhandler.OperationActionHandler;
import org.eclipse.glsp.server.actionhandler.SaveModelActionHandler;
import org.eclipse.glsp.server.di.DefaultGLSPModule;
import org.eclipse.glsp.server.di.MultiBindConfig;
import org.eclipse.glsp.server.operationhandler.LayoutOperationHandler;

public class EAMGLSPModule extends DefaultGLSPModule {

	@Override
	protected void configureActionHandlers(MultiBindConfig<ActionHandler> bindings) {
		super.configureActionHandlers(bindings);
		bindings.rebind(OperationActionHandler.class, EAMOperationActionHandler.class);
		bindings.rebind(SaveModelActionHandler.class, EAMSaveModelActionHandler.class);
		bindings.add(EAMTriggerCompareActionHandler.class);
		bindings.add(EAMGetApplicationTypesActionHandler.class);
	}
	
	@Override
	protected void configureActions(MultiBindConfig<Action> bindings) {
		super.configureActions(bindings);
	}
	
	@Override
	protected Class<? extends ToolPaletteItemProvider> bindToolPaletteItemProvider() {
		return EAMToolPaletteItemProvider.class;
	}
	
	@Override
	protected Class<? extends OperationHandlerRegistry> bindOperationHandlerRegistry() {
		return EAMDIOperationHandlerRegistry.class;
	}

	@Override
	protected void configureOperationHandlers(MultiBindConfig<OperationHandler> bindings) {
		bindings.add(LayoutOperationHandler.class);
		bindings.add(CreateNodeOperationHandler.class);
		bindings.add(CreateEdgeOperationHandler.class);
		bindings.add(EAMChangeBoundsOperationHandler.class);
		bindings.add(EAMLabelEditOperationHandler.class);
		bindings.add(EAMDeleteOperationHandler.class);
	}

	@Override
	protected Class<? extends ServerConfiguration> bindServerConfiguration() {
		return EAMServerConfiguration.class;
	}

	@Override
	protected void configureDiagramConfigurations(MultiBindConfig<DiagramConfiguration> bindings) {
		bindings.add(EAMDiagramConfiguration.class);
	}


	@Override
	protected Class<? extends ModelStateProvider> bindModelStateProvider() {
		return EAMModelStateProvider.class;
	}

	@Override
	protected Class<? extends ILayoutEngine> bindLayoutEngine() {
		return EAMLayoutEngine.class;
	}

	@Override
	protected Class<? extends ModelFactory> bindModelFactory() {
		return EAMModelFactory.class;
	}

}
