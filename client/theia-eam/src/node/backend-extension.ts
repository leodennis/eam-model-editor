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
import { BackendApplicationContribution } from "@theia/core/lib/node";
import { LanguageServerContribution } from "@theia/languages/lib/node";
import { ContainerModule, injectable } from "inversify";

import { EAMGLSPServerContribution } from "./eam-glsp-server-contribution";
import {GLSPLaunchOptions, GLSPLaunchOptionsSymb, GLSPServerLauncher} from "./glsp-server-launcher";
import { join, resolve } from 'path';


@injectable()
export class EAMGlspLaunchOptions implements GLSPLaunchOptions {
    isRunning = false;
    hostname = 'localhost';
    jarPath = resolve(join(__dirname, '..', '..', 'build', 'org.eclipse.emfcloud.eam.glsp-0.0.1-glsp.jar'));
    serverPort = 5007;
}

export default new ContainerModule(bind => {
    bind(GLSPLaunchOptionsSymb).to(EAMGlspLaunchOptions).inSingletonScope();
    bind(LanguageServerContribution).to(EAMGLSPServerContribution).inSingletonScope();
    bind(BackendApplicationContribution).to(GLSPServerLauncher);
});
