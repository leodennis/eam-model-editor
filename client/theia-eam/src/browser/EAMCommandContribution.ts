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
import {
    FrontendApplication,
    OpenerService,
    QuickOpenItem,
    QuickOpenMode,
    QuickOpenModel,
    QuickOpenOptions,
    QuickOpenService
} from "@theia/core/lib/browser";
import { Command, CommandContribution, CommandRegistry } from "@theia/core/lib/common/command";
import { MessageService } from "@theia/core/lib/common/message-service";
import { ProgressService } from "@theia/core/lib/common/progress-service";
import { SelectionService } from "@theia/core/lib/common/selection-service";
import URI from "@theia/core/lib/common/uri";
import { UriAwareCommandHandler, UriCommandHandler } from "@theia/core/lib/common/uri-command-handler";
import { FileDialogService } from "@theia/filesystem/lib/browser";
import { FileStat, FileSystem } from "@theia/filesystem/lib/common/filesystem";
import { WorkspaceService } from "@theia/workspace/lib/browser";
import { inject, injectable } from "inversify";
import { RequestAction, ResponseAction, generateRequestId } from "@eclipse-glsp/client";
import { EAMDiagramManager } from "./diagram/eam-diagram-manager";
import { DiagramWidget, DiagramManagerProvider } from "sprotty-theia/lib";

export const TRIGGER_COMPARE_COMMAND: Command = {
    id: 'compare.eamFiles',
    category: 'Compare',
    label: 'Compare EAM files',
};


export class TriggerCompareAction  implements RequestAction<TriggerCompareActionReturnAction> {
    static readonly KIND = 'triggerCompare';
    kind = TriggerCompareAction.KIND;
    constructor(public readonly requestId: string = generateRequestId()) { }
}
export class TriggerCompareActionReturnAction implements ResponseAction {
    static readonly KIND = 'triggerCompareReturn';
    kind = TriggerCompareActionReturnAction.KIND;
    constructor(public readonly responseId: string = '') { }
}


@injectable()
export class EAMCommandContribution implements CommandContribution {

    @inject(FileSystem) protected readonly fileSystem: FileSystem;
    @inject(SelectionService) protected readonly selectionService: SelectionService;
    @inject(OpenerService) protected readonly openerService: OpenerService;
    @inject(FrontendApplication) protected readonly app: FrontendApplication;
    @inject(MessageService) protected readonly messageService: MessageService;
    @inject(FileDialogService) protected readonly fileDialogService: FileDialogService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(ProgressService) protected readonly progressService: ProgressService;
    @inject(QuickOpenService) protected readonly quickOpenService: QuickOpenService;
    //@inject(TYPES.IActionDispatcher) protected readonly actionDispatcher: GLSPActionDispatcher;
    @inject(DiagramManagerProvider) protected readonly diagramManagerProvider: DiagramManagerProvider;


    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(TRIGGER_COMPARE_COMMAND, this.newWorkspaceRootUriAwareCommandHandler({
            execute: uri => this.getDirectory(uri).then(parent => {
                if (parent) {
                    // const parentUri = new URI(parent.uri);

                    const triggerCompare = (origin: string, left: string, right: string) => {
                        console.log("Ready to compare !!! :D");

                        this.diagramManagerProvider().then(provider => {
                            let myprovider = <EAMDiagramManager> provider;
                            let widget = <DiagramWidget> myprovider.diagramConnector.widgetManager.getWidgets(provider.id)[0];

                            widget.actionDispatcher.request(new TriggerCompareAction()).then(response => {
                                if (response) {
                                    //const action: TriggerCompareActionReturnAction = <TriggerCompareActionReturnAction> response;
                                    console.log("Received trigger compare Response!");
                                }
                            });
                        });
                        
                    };

                    const showInput = (hint: string, prefix: string, onEnter: (result: string) => void) => {
                        const quickOpenModel: QuickOpenModel = {
                            onType(lookFor: string, acceptor: (items: QuickOpenItem[]) => void): void {
                                const dynamicItems: QuickOpenItem[] = [];
                                const suffix = "Press 'Enter' to confirm or 'Escape' to cancel.";

                                dynamicItems.push(new SingleStringInputOpenItem(
                                    `${prefix}: ${lookFor}. ${suffix}`,
                                    () => onEnter(lookFor),
                                    (mode: QuickOpenMode) => mode === QuickOpenMode.OPEN,
                                    () => false
                                ));

                                acceptor(dynamicItems);
                            }
                        };
                        this.quickOpenService.open(quickOpenModel, this.getOptions(hint, false));
                    };

                    showInput("Origin", "A prevous (common) version", (origin) => {
                        showInput("Left", "A new verson", (left) => {
                            showInput("Right", "A new verson", (right) => {
                                triggerCompare(origin, left, right);
                            });
                        });
                    });
                }
            })
        }));
    }

    protected async getDirectory(candidate: URI): Promise<FileStat | undefined> {
        const stat = await this.fileSystem.getFileStat(candidate.toString());
        if (stat && stat.isDirectory) {
            return stat;
        }
        return this.getParent(candidate);
    }

    protected getParent(candidate: URI): Promise<FileStat | undefined> {
        return this.fileSystem.getFileStat(candidate.parent.toString());
    }

    protected newWorkspaceRootUriAwareCommandHandler(handler: UriCommandHandler<URI>): WorkspaceRootUriAwareCommandHandler {
        return new WorkspaceRootUriAwareCommandHandler(this.workspaceService, this.selectionService, handler);
    }

    private getOptions(placeholder: string, fuzzyMatchLabel: boolean = true, onClose: (canceled: boolean) => void = () => { }): QuickOpenOptions {
        return QuickOpenOptions.resolve({
            placeholder,
            fuzzyMatchLabel,
            fuzzySort: false,
            onClose
        });
    }
}


export class WorkspaceRootUriAwareCommandHandler extends UriAwareCommandHandler<URI> {

    constructor(
        protected readonly workspaceService: WorkspaceService,
        protected readonly selectionService: SelectionService,
        protected readonly handler: UriCommandHandler<URI>
    ) {
        super(selectionService, handler);
    }

    public isEnabled(...args: any[]): boolean {
        return super.isEnabled(...args) && !!this.workspaceService.tryGetRoots().length;
    }

    public isVisible(...args: any[]): boolean {
        return super.isVisible(...args) && !!this.workspaceService.tryGetRoots().length;
    }

    protected getUri(...args: any[]): URI | undefined {
        const uri = super.getUri(...args);
        // If the URI is available, return it immediately.
        if (uri) {
            return uri;
        }
        // Return the first root if available.
        if (!!this.workspaceService.tryGetRoots().length) {
            return new URI(this.workspaceService.tryGetRoots()[0].uri);
        }
        return undefined;
    }
}

class SingleStringInputOpenItem extends QuickOpenItem {

    constructor(
        private readonly label: string,
        private readonly execute: (item: QuickOpenItem) => void = () => { },
        private readonly canRun: (mode: QuickOpenMode) => boolean = mode => mode === QuickOpenMode.OPEN,
        private readonly canClose: (mode: QuickOpenMode) => boolean = mode => true) {

        super();
    }

    getLabel(): string {
        return this.label;
    }

    run(mode: QuickOpenMode): boolean {
        if (!this.canRun(mode)) {
            return false;
        }
        this.execute(this);
        return this.canClose(mode);
    }

}
