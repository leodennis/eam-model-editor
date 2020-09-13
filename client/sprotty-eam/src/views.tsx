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
import { injectable } from "inversify";
import * as snabbdom from "snabbdom-jsx";
import { VNode } from "snabbdom/vnode";
import {
  Point,
  PolylineEdgeView,
  RectangularNodeView,
  RenderingContext,
  SEdge,
  toDegrees,
} from "sprotty/lib";

import { LabeledNode} from "./model";
import { SCompartmentView, SCompartment } from "@eclipse-glsp/client";

/** @jsx svg */
const JSX = { createElement: snabbdom.svg };
@injectable()
export class ClassNodeView extends RectangularNodeView {
  render(node: LabeledNode, context: RenderingContext): VNode {

    const rhombStr = "M 0,38  L " + node.bounds.width + ",38";

    return <g class-node={true}>
      <defs>
        <filter id="dropShadow">
          <feDropShadow dx="0.5" dy="0.5" stdDeviation="0.4" />
        </filter>
      </defs>

      <rect class-sprotty-node={true} class-selected={node.selected} class-mouseover={node.hoverFeedback}
        x={0} y={0} rx={6} ry={6}
        width={Math.max(0, node.bounds.width)} height={Math.max(0, node.bounds.height)} />
      {context.renderChildren(node)}
      {(node.children[1] && node.children[1].children.length > 0) ?
        <path class-sprotty-edge={true} d={rhombStr}></path> : ''}
      </g>;
    }
}

@injectable()
export class SCompartmentAttributeView implements SCompartmentView {
    render(model: Readonly<SCompartment>, context: RenderingContext): VNode {
        const translate = `translate(${model.bounds.x}, ${model.bounds.y})`;
        const vnode = <g transform={translate} class-eam-comp-attribute="{true}">
            {context.renderChildren(model)}
        </g>;
        return vnode;
    }
}

@injectable()
export class ArrowEdgeView extends PolylineEdgeView {
  protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
    const p1 = segments[segments.length - 2];
    const p2 = segments[segments.length - 1];
    return [
      <path class-sprotty-edge={true} d="M 10,-4 L 0,0 L 10,4"
        transform={`rotate(${angle(p2, p1)} ${p2.x} ${p2.y}) translate(${p2.x} ${p2.y})`} />,
    ];
  }
}

@injectable()
export class AssociationEdgeView extends ArrowEdgeView {
    protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
        const source1 = segments[0];
        const source2 = segments[1];
        const target1 = segments[segments.length - 2];
        const target2 = segments[segments.length - 1];
        return [
            <path class-sprotty-edge={true} class-triangle={true} class-association={true} d="M 12,-4 L 0,0 L 12,4"
                  transform={`rotate(${angle(target2, target1)} ${target2.x} ${target2.y}) translate(${target2.x} ${target2.y})`} />,
            <path class-sprotty-edge={true} class-triangle={true} class-association={true} d="M 12,-4 L 0,0 L 12,4"
                  transform={`rotate(${angle(source1, source2)} ${source1.x} ${source1.y}) translate(${source1.x} ${source1.y})`} />,
        ];
    }
}

export function angle(x0: Point, x1: Point): number {
  return toDegrees(Math.atan2(x1.y - x0.y, x1.x - x0.x));
}
