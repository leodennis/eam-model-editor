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
import { GLSPClientContribution } from "@eclipse-glsp/theia-integration/lib/browser";
import {
    FrontendApplicationContribution,
    OpenHandler,
    WebSocketConnectionProvider,
    WidgetFactory
} from "@theia/core/lib/browser";
import { ContainerModule, interfaces } from "inversify";
import { DiagramConfiguration, DiagramManager, DiagramManagerProvider } from "sprotty-theia/lib";

import { FILEGEN_SERVICE_PATH, FileGenServer } from "../common/generate-protocol";
import { EAMDiagramConfiguration } from "./diagram/eam-diagram-configuration";
import { EAMDiagramManager } from "./diagram/eam-diagram-manager";
import { EAMGLSPDiagramClient } from "./diagram/eam-glsp-diagram-client";
import { EAMGLSPClientContribution } from "./eam-glsp-contribution";
import {EAMCommandContribution } from "./EAMCommandContribution";
import { CommandContribution } from "@theia/core";
//import { EAMGraphicalComparisonOpener } from "./graphical-comparison-opener";

import { EAMGraphicalComparisonOpener } from "./graphical-comparison-opener";
import { GraphicalComparisonOpener } from 'comparison-extension/lib/browser/graphical/graphical-comparison-opener';


export default new ContainerModule((bind: interfaces.Bind, unbind: interfaces.Unbind, isBound: interfaces.IsBound, rebind: interfaces.Rebind) => {
    bind(EAMGLSPClientContribution).toSelf().inSingletonScope();
    bind(GLSPClientContribution).toService(EAMGLSPClientContribution);
    bind(EAMGLSPDiagramClient).toSelf().inSingletonScope();
    bind(DiagramConfiguration).to(EAMDiagramConfiguration).inSingletonScope();
    bind(EAMDiagramManager).toSelf().inSingletonScope();
    bind(FrontendApplicationContribution).toService(EAMDiagramManager);
    bind(OpenHandler).toService(EAMDiagramManager);
    bind(WidgetFactory).toService(EAMDiagramManager);
    bind(CommandContribution).to(EAMCommandContribution);

    rebind(GraphicalComparisonOpener).to(EAMGraphicalComparisonOpener);

    bind(DiagramManagerProvider).toProvider<DiagramManager>((context) => {
        return () => {
            return new Promise<DiagramManager>((resolve) => {
                const diagramManager = context.container.get<EAMDiagramManager>(EAMDiagramManager);
                resolve(diagramManager);
            });
        };
    });
    bind(FileGenServer).toDynamicValue(ctx => {
        const connection = ctx.container.get(WebSocketConnectionProvider);
        return connection.createProxy<FileGenServer>(FILEGEN_SERVICE_PATH);
    }).inSingletonScope();
});
