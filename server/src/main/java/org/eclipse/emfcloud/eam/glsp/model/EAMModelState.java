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

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emfcloud.eam.glsp.EAMEditorContext;
import org.eclipse.emfcloud.eam.glsp.EAMFacade;
import org.eclipse.emfcloud.eam.glsp.EAMModelIndex;
import org.eclipse.emfcloud.eam.glsp.ResourceManager;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.server.model.ModelStateImpl;

public class EAMModelState extends ModelStateImpl implements GraphicalModelState {

	private EAMEditorContext editorContext;

	public static EAMModelState getModelState(GraphicalModelState state) {
		if (!(state instanceof EAMModelState)) {
			throw new IllegalArgumentException("Argument must be a EAMModelState");
		}
		return ((EAMModelState) state);
	}

	public static EAMEditorContext getEditorContext(GraphicalModelState state) {
		return getModelState(state).getEditorContext();
	}

	public static ResourceManager getResourceManager(GraphicalModelState modelState) {
		return getEditorContext(modelState).getResourceManager();
	}

	public static EAMFacade getEcoreFacade(GraphicalModelState modelState) {
		return getEditorContext(modelState).getEAMFacade();
	}

	public EAMEditorContext getEditorContext() {
		return editorContext;
	}

	public void setEditorContext(EAMEditorContext editorContext) {
		this.editorContext = editorContext;
		setCommandStack((BasicCommandStack) editorContext.getResourceManager().getEditingDomain().getCommandStack());
	}

	@Override
	public EAMModelIndex getIndex() {
		return EAMModelIndex.get(getRoot());
	}

}
