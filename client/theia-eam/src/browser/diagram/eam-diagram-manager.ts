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
import { RequestTypeHintsAction, EnableToolPaletteAction } from "@eclipse-glsp/client";
import {
    GLSPDiagramManager,
    GLSPDiagramWidget,
    GLSPTheiaSprottyConnector,
    GLSPTheiaDiagramServer,
    GLSPNotificationManager
} from "@eclipse-glsp/theia-integration/lib/browser";
import { MessageService } from "@theia/core";
import { WidgetManager, WidgetOpenerOptions } from "@theia/core/lib/browser";
import URI from "@theia/core/lib/common/uri";
import { EditorManager } from "@theia/editor/lib/browser";
import { inject, injectable } from "inversify";
import { DiagramServer, ModelSource, RequestModelAction, TYPES } from "sprotty";
import { DiagramWidget, DiagramWidgetOptions, TheiaFileSaver } from "sprotty-theia";

import { EAMLanguage } from "../../common/eam-language";
import { EAMGLSPDiagramClient } from "./eam-glsp-diagram-client";


@injectable()
export class EAMDiagramManager extends GLSPDiagramManager {
    readonly diagramType = EAMLanguage.DiagramType;
    readonly iconClass = "fa fa-project-diagram";
    readonly label = EAMLanguage.Label + " Editor";

    private _diagramConnector: GLSPTheiaSprottyConnector;

    async createWidget(options?: any): Promise<DiagramWidget> {
        if (DiagramWidgetOptions.is(options)) {
            const clientId = this.createClientId();
            const config = this.diagramConfigurationRegistry.get(options.diagramType);
            const diContainer = config.createContainer(clientId);
            const diagramWidget = new EAMDiagramWidget(options, clientId + '_widget', diContainer, this.editorPreferences, this.diagramConnector);
            return diagramWidget;
        }
        throw Error('DiagramWidgetFactory needs DiagramWidgetOptions but got ' + JSON.stringify(options));
    }
    constructor(
        @inject(EAMGLSPDiagramClient) diagramClient: EAMGLSPDiagramClient,
        @inject(TheiaFileSaver) fileSaver: TheiaFileSaver,
        @inject(WidgetManager) widgetManager: WidgetManager,
        @inject(EditorManager) editorManager: EditorManager,
        @inject(MessageService) messageService: MessageService,
        @inject(GLSPNotificationManager) notificationManager: GLSPNotificationManager) {
        super();
        this._diagramConnector = new GLSPTheiaSprottyConnector({ diagramClient,
            fileSaver, editorManager, widgetManager, diagramManager: this, messageService, notificationManager });
    }

    get fileExtensions() {
        return [EAMLanguage.FileExtension];
    }
    get diagramConnector() {
        return this._diagramConnector;
    }

    createWidgetFromURI(uri: URI, options?: WidgetOpenerOptions): Promise<GLSPDiagramWidget> {
        return this.getOrCreateWidget(uri, options) as Promise<GLSPDiagramWidget>;
    }
}

export class EAMDiagramWidget extends GLSPDiagramWidget {
    protected initializeSprotty() {
        const modelSource = this.diContainer.get<ModelSource>(TYPES.ModelSource);
        if (modelSource instanceof DiagramServer)
            modelSource.clientId = this.id;
        if (modelSource instanceof GLSPTheiaDiagramServer && this.connector)
            this.connector.connect(modelSource);
        this.disposed.connect(() => {
            if (modelSource instanceof GLSPTheiaDiagramServer && this.connector)
                this.connector.disconnect(modelSource);
        });

        this.actionDispatcher.dispatch(new RequestModelAction({
            sourceUri: this.options.uri.replace("file://", ""),
            needsClientLayout: `${this.viewerOptions.needsClientLayout}`,
            ...this.options
        }));
        this.actionDispatcher.dispatch(new RequestTypeHintsAction(this.options.diagramType));
        if (! (<any>this.options).highliteDifferences) {
            this.actionDispatcher.dispatch(new EnableToolPaletteAction());
        }
    }
}
