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
package org.eclipse.emfcloud.eam.glsp.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.model.GraphicalModelState;

import EAM_Metamodel.ApplicationType;

import org.eclipse.glsp.api.handler.ActionHandler;
import org.eclipse.emfcloud.eam.glsp.actions.ApplicationTypesAction;
import org.eclipse.emfcloud.eam.glsp.actions.ReturnApplicationTypesAction;

public class EAMGetApplicationTypesActionHandler implements ActionHandler {

	@Override
	public boolean handles(Action action) {
		return action instanceof ApplicationTypesAction;
	}

	@Override
	public List<Class<? extends Action>> getHandledActionTypes() {
		return List.of(ApplicationTypesAction.class);
	}

	@Override
	public List<Action> execute(Action action, GraphicalModelState modelState) {
		List<String> types = Arrays.stream(ApplicationType.values()).map(e -> e.getLiteral()).collect(Collectors.toList());
		Collections.sort(types);

		System.out.println("sdifjnb");
		System.out.println("sdifjnb");
		System.out.println("sdifjnb");

		return List.of(new ReturnApplicationTypesAction(types));
	}
}