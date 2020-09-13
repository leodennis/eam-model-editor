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
package org.eclipse.emfcloud.eam.glsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.Types;
import org.eclipse.glsp.api.diagram.DiagramConfiguration;
import org.eclipse.glsp.api.types.EdgeTypeHint;
import org.eclipse.glsp.api.types.ShapeTypeHint;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GraphPackage;



import com.google.common.collect.Lists;

public class EAMDiagramConfiguration implements DiagramConfiguration {

	@Override
	public String getDiagramType() {
		return "eamdiagram";
	}

	@Override
	public List<EdgeTypeHint> getEdgeTypeHints() {
		// add association, communication
		return Lists.newArrayList(createDefaultEdgeTypeHint(Types.EDGE_ASSOCIATION), createDefaultEdgeTypeHint(Types.EDGE_COMMUNICATION));
	}

	@Override
	public EdgeTypeHint createDefaultEdgeTypeHint(String elementId) {
		List<String> allowed = Lists.newArrayList(Types.NODE_PRODUCT, Types.NODE_APPLICATION, Types.NODE_HOST, Types.NODE_DATACENTER);
		return new EdgeTypeHint(elementId, true, true, true, allowed, allowed);
	}

	@Override
	public List<ShapeTypeHint> getNodeTypeHints() {
		List<ShapeTypeHint> hints = new ArrayList<>();
		//hints.add(new ShapeTypeHint(DefaultTypes.GRAPH, false, false, false, false,
		//		List.of(Types.CASE, Types.TASK, Types.STAGE, Types.EVENTLISTENER)));
		hints.add(new ShapeTypeHint(Types.NODE_APPLICATION, true, true, true, true));
		hints.add(new ShapeTypeHint(Types.NODE_PRODUCT, true, true, true, true));
		hints.add(new ShapeTypeHint(Types.NODE_HOST, true, true, true, true));
		hints.add(new ShapeTypeHint(Types.NODE_DATACENTER, true, true, true, true));

		return hints;
	}

	@Override
	public Map<String, EClass> getTypeMappings() {
		Map<String, EClass> mappings = DefaultTypes.getDefaultTypeMappings();

		mappings.put(Types.LABEL_NAME, GraphPackage.Literals.GLABEL);
		mappings.put(Types.LABEL_TEXT, GraphPackage.Literals.GLABEL);
		mappings.put(Types.LABEL_EDGE_NAME, GraphPackage.Literals.GLABEL);
		mappings.put(Types.LABEL_EDGE_MULTIPLICITY, GraphPackage.Literals.GLABEL);
		mappings.put(Types.COMP, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.COMP_HEADER, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.COMP_ATTRIBUTE, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.LABEL_ICON, GraphPackage.Literals.GCOMPARTMENT);

		// eam
		mappings.put(Types.NODE_APPLICATION, GraphPackage.Literals.GNODE);
		mappings.put(Types.NODE_PRODUCT, GraphPackage.Literals.GNODE);
		mappings.put(Types.NODE_HOST, GraphPackage.Literals.GNODE);
		mappings.put(Types.NODE_DATACENTER, GraphPackage.Literals.GNODE);

		return mappings;
	}

}
