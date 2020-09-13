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
import { TYPES } from "@eclipse-glsp/client/lib";
import { SelectionService } from "@theia/core";
import { Container, inject, injectable } from "inversify";
import { createEAMDiagramContainer} from "sprotty-eam/lib";
import { DiagramConfiguration, TheiaDiagramServer, TheiaSprottySelectionForwarder } from "sprotty-theia/lib";

import { EAMLanguage } from "../../common/eam-language";
import { EAMGLSPTheiaDiagramServer } from "./eam-glsp-theia-diagram-server";

@injectable()
export class EAMDiagramConfiguration implements DiagramConfiguration {
    @inject(SelectionService) protected selectionService: SelectionService;
    diagramType: string = EAMLanguage.DiagramType;

    createContainer(widgetId: string): Container {
        const container = createEAMDiagramContainer(widgetId);
        container.bind(TYPES.ModelSource).to(EAMGLSPTheiaDiagramServer).inSingletonScope();
        container.bind(TheiaDiagramServer).toService(EAMGLSPTheiaDiagramServer);
        // container.rebind(KeyTool).to(TheiaKeyTool).inSingletonScope()
        container.bind(TYPES.IActionHandlerInitializer).to(TheiaSprottySelectionForwarder);
        container.bind(SelectionService).toConstantValue(this.selectionService);

        return container;
    }
}
