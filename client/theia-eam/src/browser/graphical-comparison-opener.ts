/********************************************************************************
 * Copyright (C) 2018 TypeFox and others.
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
//import { injectable, inject } from 'inversify';
import { injectable } from 'inversify';

import { WidgetManager } from '@theia/core/lib/browser';
import URI from '@theia/core/lib/common/uri';
import {GraphicalComparisonOpener} from 'comparison-extension/lib/browser/graphical-comparison-opener';
//import { EAMDiagramManager } from './diagram/eam-diagram-manager';
//import { EAMLanguage } from '../common/eam-language';
//import { DiagramWidgetOptions } from "sprotty-theia";

@injectable()
export class EAMGraphicalComparisonOpener extends GraphicalComparisonOpener {

  /*
  constructor(
    @inject(EAMDiagramManager) private readonly diagramManager: EAMDiagramManager) {
    super();
  }
  */

 // EAMDiagramManager

    showWidgets(widgetManager: WidgetManager, left: URI, right: URI) {
      /*
      const options: DiagramWidgetOptions = {
        uri: String(left),
        diagramType: EAMLanguage.DiagramType,
        iconClass: "fa fa-project-diagram",
        label: EAMLanguage.Label + " Editor"
      };
      this.diagramManager.createWidget(options).then(widget => {
        widget.show();
        //console.log(widget);
      });

       */
      throw Error("Custom error");

      /*
      super({
        widgetId: ComparisonTreeEditorWidget.WIDGET_ID,
        widgetName: ComparisonTreeEditorWidget.WIDGET_LABEL,
        defaultWidgetOptions: {
            area: 'main',
            mode: 'open-to-left' //open-to-right   ? -> split-left
        }
     });
    */
    }
}
