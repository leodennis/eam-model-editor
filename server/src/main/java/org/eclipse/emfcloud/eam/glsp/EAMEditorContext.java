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
package org.eclipse.emfcloud.eam.glsp;

import org.eclipse.emfcloud.eam.glsp.gmodel.GModelFactory;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;

public class EAMEditorContext {
	private final ResourceManager resourceManager;
	private final GModelFactory gModelFactory;
	private final EAMModelState modelState;

	public EAMEditorContext(EAMModelState modelState) {
		this.modelState = modelState;
		gModelFactory = new GModelFactory(modelState);
		resourceManager = new ResourceManager(modelState);
	}

	public EAMFacade getEAMFacade() {
		return resourceManager.getEAMFacade();
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public GModelFactory getGModelFactory() {
		return gModelFactory;
	}

	public EAMModelState getModelState() {
		return modelState;
	}

}
