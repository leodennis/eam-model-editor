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

import org.eclipse.emfcloud.eam.glsp.actions.TriggerCompareAction;
import org.eclipse.emfcloud.eam.glsp.actions.TriggerCompareReturnAction;
import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.handler.ActionHandler;
import org.eclipse.glsp.api.model.GraphicalModelState;

public class EAMTriggerCompareActionHandler implements ActionHandler {

	@Override
	public boolean handles(Action action) {
		return action instanceof TriggerCompareAction;
	}

	@Override
	public List<Class<? extends Action>> getHandledActionTypes() {
		return List.of(TriggerCompareAction.class);
	}

	@Override
	public List<Action> execute(Action action, GraphicalModelState modelState) {
		//TriggerCompareAction compareAction = (TriggerCompareAction) action;

		String[] args = new String[2];
		args[0] = "C:\\Users\\ldkpr\\Desktop\\ws\\diagram_new.eam";
		args[1] = "C:\\Users\\ldkpr\\Desktop\\ws\\diagram.eam";
		//Model_Compare.compare(args);

		return List.of(new TriggerCompareReturnAction());
	}

}