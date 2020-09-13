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
import { GLSPActionDispatcher, TYPES, getZoom } from "@eclipse-glsp/client/lib";
import { inject, injectable } from "inversify";
import { EditLabelUI, generateRequestId, RequestAction, ResponseAction, SModelRoot } from "sprotty/lib";
import { matchesKeystroke } from "sprotty/lib/utils/keyboard";

export class ApplicationTypesAction implements RequestAction<ReturnApplicationTypesAction> {
    static readonly KIND = 'getApplicationTypes';
    kind = ApplicationTypesAction.KIND;
    constructor(public readonly requestId: string = generateRequestId()) { }
}

export class ReturnApplicationTypesAction implements ResponseAction {
    static readonly KIND = 'returnApplicationTypes';
    kind = ReturnApplicationTypesAction.KIND;
    types: string[];
    constructor(public readonly actions: string[], public readonly responseId: string = '') {
        this.types = actions;
    }
}

@injectable()
export class EditLabelUIAutocomplete extends EditLabelUI {

    protected showAutocomplete: boolean = false;
    protected outerDiv: HTMLElement;
    protected listContainer: HTMLElement;
    protected currentFocus: number;
    protected types: string[] = [];

    @inject(TYPES.IActionDispatcher) protected actionDispatcher: GLSPActionDispatcher;

    constructor() {
        super();
    }

    protected initializeContents(containerElement: HTMLElement) {
        this.outerDiv = containerElement;
        super.initializeContents(containerElement);
    }

    protected hideIfEscapeEvent(event: KeyboardEvent) {
        super.hideIfEscapeEvent(event);

        if (matchesKeystroke(event, 'Space', 'ctrl')) {
            this.showAutocomplete = true;
            if (this.isAutoCompleteEnabled()) {
                this.createAutocomplete();
            }
        }

        this.updateAutocomplete(event);
    }

    protected validateLabelIfContentChange(event: KeyboardEvent, value: string) {
        if (this.isAutoCompleteEnabled() && this.previousLabelContent !== value) {
            // recreate autocomplete list if value changed
            this.createAutocomplete();
        }
        super.validateLabelIfContentChange(event, value);
    }

    protected updateAutocomplete(event: KeyboardEvent) {
        if (matchesKeystroke(event, 'ArrowDown')) {
            this.currentFocus++;
            this.addActive();
        } else if (matchesKeystroke(event, 'ArrowUp')) {
            this.currentFocus--;
            this.addActive();
        } else if (matchesKeystroke(event, 'Enter')) {
            event.preventDefault();
            if (this.currentFocus > -1) {
                if (this.listContainer) {
                    const children = this.listContainer.children;
                    (<HTMLElement>children[this.currentFocus]).click();
                }
            }
        }
    }

    protected createAutocomplete() {
        const input: String = this.inputElement.value;

        this.closeAllLists();
        this.currentFocus = -1;

        this.listContainer = document.createElement("div");
        this.listContainer.setAttribute("id", this.inputElement.id + "autocomplete-list");
        this.listContainer.setAttribute("class", "autocomplete-items");
        this.outerDiv.appendChild(this.listContainer);

        // create autocomlete items starting with input
        for (let i = 0; i < this.types.length; i++) {
            if (this.types[i].substr(0, input.length).toLowerCase() === input.toLowerCase()) {
                const element = document.createElement("div");
                element.innerHTML = "<strong>" + this.types[i].substr(0, input.length) + "</strong>";
                element.innerHTML += this.types[i].substr(input.length);
                element.innerHTML += "<input type='hidden' value='" + this.types[i] + "'>";
                element.addEventListener("click", e => {
                    // change the type of the label
                    this.inputElement.value = element.getElementsByTagName("input")[0].value;
                    this.closeAllLists();
                });
                this.listContainer.appendChild(element);
            }
        }

        // set max height for scrolling
        const parent = this.outerDiv.parentElement;
        if (parent) {
            const parentHeight = parent.offsetHeight;
            const parentPosY = parent.offsetTop;
            const posY = this.outerDiv.offsetTop + this.inputElement.offsetHeight;
            const maxHeight = parentHeight - (posY - parentPosY);
            this.listContainer.style.maxHeight = `${maxHeight}px`;
        }
    }

    protected addActive() {
        if (!this.listContainer) return;
        this.removeActive();
        const children = this.listContainer.children;
        if (children.length > 0) {
            if (this.currentFocus >= children.length) this.currentFocus = 0;
            if (this.currentFocus < 0) this.currentFocus = (children.length - 1);
            children[this.currentFocus].classList.add("autocomplete-active");
        }
    }

    protected removeActive() {
        const children = this.listContainer.children;
        for (let i = 0; i < children.length; i++) {
            children[i].classList.remove("autocomplete-active");
        }
    }

    protected closeAllLists() {
        const x = this.outerDiv.getElementsByClassName("autocomplete-items");
        for (let i = 0; i < x.length; i++) {
            this.outerDiv.removeChild(x[i]);
        }
    }

    protected onBeforeShow(containerElement: HTMLElement, root: Readonly<SModelRoot>, ...contextElementIds: string[]) {
        super.onBeforeShow(containerElement, root, ...contextElementIds);
        
        // request possible element types
        if (this.label && this.label.type === "label:attribute-product-type") {
            this.actionDispatcher.requestUntil(new ApplicationTypesAction()).then(response => {
                if (response) {
                    const action: ReturnApplicationTypesAction = <ReturnApplicationTypesAction>response;
                    this.types = action.types;
                }
            });
        }   
    }

    protected isAutoCompleteEnabled() {
        if (this.label) {
            return this.label.type === "label:attribute-product-type" && this.showAutocomplete;
        }
        
        return false;
    }

    public hide() {
        super.hide();
        this.showAutocomplete = false;
        this.currentFocus = -1;
        this.closeAllLists();
    }

    // increase edit label width
    protected setPosition(containerElement: HTMLElement) {
        super.setPosition(containerElement);
        let width = 200;

        if (this.label) {
            const zoom = getZoom(this.label);
            width = (this.label.editControlDimension ? this.label.editControlDimension.width : width) * zoom;
            if (width/zoom < width) {
                width = width * zoom;
            }
        }

        containerElement.style.width = `${width}px`;
        this.editControl.style.width = `${width}px`;
    }
}
