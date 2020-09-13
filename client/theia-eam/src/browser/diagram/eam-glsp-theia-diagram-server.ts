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
import { ActionHandlerRegistry} from "@eclipse-glsp/client/lib";
import { GLSPTheiaDiagramServer } from "@eclipse-glsp/theia-integration/lib/browser";
import { injectable } from "inversify";
import { TriggerCompareAction, TriggerCompareActionReturnAction } from "../EAMCommandContribution";
import { ApplicationTypesAction, ReturnApplicationTypesAction } from "sprotty-eam/lib/features/edit-label-autocomplete";

@injectable()
export class EAMGLSPTheiaDiagramServer extends GLSPTheiaDiagramServer {
    initialize(registry: ActionHandlerRegistry): void {
        super.initialize(registry);

        // register custom actions here
        registry.register(TriggerCompareAction.KIND, this);
        registry.register(TriggerCompareActionReturnAction.KIND, this);
        registry.register(ApplicationTypesAction.KIND, this);
        registry.register(ReturnApplicationTypesAction.KIND, this);
    }

}
