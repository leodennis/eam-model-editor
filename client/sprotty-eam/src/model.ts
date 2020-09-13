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
    deletableFeature,
    EditableLabel,
    editLabelFeature,
    hoverFeedbackFeature,
    isEditableLabel,
    Nameable,
    nameFeature,
    popupFeature,
    RectangularNode,
    selectFeature,
    SLabel,
    WithEditableLabel,
    withEditLabelFeature,
    SEdge
} from "sprotty/lib";


export class LabeledNode extends RectangularNode implements WithEditableLabel, Nameable {

    get editableLabel() {
        const headerComp = this.children.find(element => element.type === 'comp:header');
        if (headerComp) {
            const label = headerComp.children.find(element => element.type === 'label:heading');
            if (label && isEditableLabel(label)) {
                return label;
            }
        }
        return undefined;
    }

    get name() {
        if (this.editableLabel) {
            return this.editableLabel.text;
        }
        return this.id;
    }
    hasFeature(feature: symbol) {
        return super.hasFeature(feature) || feature === nameFeature || feature === withEditLabelFeature;
    }
}

export class SEditableLabel extends SLabel implements EditableLabel {
    hasFeature(feature: symbol) {
        return feature === editLabelFeature || super.hasFeature(feature);
    }
}

export class SLabelNode extends SLabel implements EditableLabel {
    hoverFeedback: boolean = false;
    imageName: string;

    hasFeature(feature: symbol): boolean {
        return (feature === selectFeature || feature === editLabelFeature || feature === popupFeature || feature === deletableFeature || 
            feature === hoverFeedbackFeature || super.hasFeature(feature));
    }
}

export class ArrowEdge extends SEdge {
    public readonly targetAnchorCorrection = 3.3;
}
