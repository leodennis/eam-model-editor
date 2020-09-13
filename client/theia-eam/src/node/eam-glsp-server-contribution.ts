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
import { BaseGLSPServerContribution } from "@eclipse-glsp/theia-integration/lib/node";
import { IConnection } from "@theia/languages/lib/node";
import { inject, injectable, optional } from "inversify";
import * as net from "net";
import { createSocketConnection } from "vscode-ws-jsonrpc/lib/server";

import { EAMLanguage } from "../common/eam-language";
import { GLSPLaunchOptions, GLSPLaunchOptionsSymb } from "./glsp-server-launcher";

@injectable()
export class EAMGLSPServerContribution extends BaseGLSPServerContribution {

    readonly id = EAMLanguage.Id;
    readonly name = EAMLanguage.Name;
    serverStarted = false;
    readonly description = {
        id: 'eam',
        name: 'EAM',
        documentSelector: ['eam'],
        fileEvents: [
            '**/*.eamdiagram'
        ]
    };
    @inject(GLSPLaunchOptionsSymb) @optional() protected readonly launchOptions: GLSPLaunchOptions;

    start(clientConnection: IConnection): void {
        const socketPort = this.launchOptions.serverPort;
        if (socketPort) {
            const socket = new net.Socket();
            const serverConnection = createSocketConnection(socket, socket, () => {
                socket.destroy();
            });
            this.forward(clientConnection, serverConnection);
            socket.connect(socketPort);
        } else {
            console.error("Error when trying to connect to EAM GLSP server");
        }
    }
}
