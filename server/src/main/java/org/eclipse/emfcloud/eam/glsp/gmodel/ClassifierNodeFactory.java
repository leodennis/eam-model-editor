/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
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
package org.eclipse.emfcloud.eam.glsp.gmodel;

import java.util.List;
import java.util.UUID;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.eam.enotation.Shape;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.CSS;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.Types;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.emfcloud.compare.model_comparison.UUID_Provider;

import EAM_Metamodel.EAM_Application;
import EAM_Metamodel.EAM_DataCenter;
import EAM_Metamodel.EAM_Host;
import EAM_Metamodel.EAM_Node;
import EAM_Metamodel.EAM_Product;

public class ClassifierNodeFactory extends AbstractGModelFactory<EAM_Node, GNode> {

	private GModelFactory parentFactory;

	public ClassifierNodeFactory(EAMModelState modelState, GModelFactory parentFactory) {
		super(modelState);
		this.parentFactory = parentFactory;
	}

	public GNode create(EAM_Node node) {
		if (node instanceof EAM_Application) {
			return create(node, Types.NODE_APPLICATION);
		} else if (node instanceof EAM_Product) {
			return create(node, Types.NODE_PRODUCT);
		} else if (node instanceof EAM_Host) {
			return create(node, Types.NODE_HOST);
		} else if (node instanceof EAM_DataCenter) {
			return create(node, Types.NODE_DATACENTER);
		}
		return null;
	}

	public GNode create(EAM_Node node, String type) {
		List<GModelElement> children = parentFactory.createAttributeLabels(node);
		
		String id = toId(node);
		GNodeBuilder nodeBuilder = new GNodeBuilder(type) //
				.id(id) //
				.layout(GConstants.Layout.VBOX) //
				.addCssClass(CSS.NODE) //
				.add(buildHeader(getNodeName(node)))//
				.add(createLabeledChildrenCompartment(children));
		applyShapeData(node, nodeBuilder);
		
		String change = this.modelState.getHighlight().get(id);
		if (change != null) {
			nodeBuilder.addCssClass(changeToNodeHighlightClass(change));
		}

		return nodeBuilder.build();
		/*
		System.out.println("created element with id: " + node2.getId());
		for (GModelElement gModelElement : children) {
			System.out.println("  child id: " + gModelElement.getId());
		}
		
		return node2;
		*/
	}

	private String changeToNodeHighlightClass(String color) {
		switch (color) {
			case "red":
				return "fillRed";
			case "green":
				return "fillGreen";
			case "yellow":
				return "fillBlue";
		}
		return "";
	}

	public String getNodeName(EAM_Node node) {
		if (node instanceof EAM_Application) {
			return "Application";
		} else if (node instanceof EAM_Product) {
			return "Product";
		} else if (node instanceof EAM_Host) {
			return "Host";
		} else if (node instanceof EAM_DataCenter) {
			return "Datacenter";
		}
		return "not found";
	}

	private void applyShapeData(EAM_Node node, GNodeBuilder builder) {
		modelState.getIndex().getNotation(node, Shape.class).ifPresent(shape -> {
			if (shape.getPosition() != null) {
				builder.position(GraphUtil.copy(shape.getPosition()));
			} else if (shape.getSize() != null) {
				builder.size(GraphUtil.copy(shape.getSize()));
			}
		});
	}

	private GCompartment buildHeader(String name) {
		return new GCompartmentBuilder(Types.COMP_HEADER) //
				.layout("hbox") //
				.add(new GLabelBuilder(Types.LABEL_NAME) //
						.text(name) //
						.build()) //
				.build();
	}


	private GCompartment createLabeledChildrenCompartment(List<GModelElement> lables) {
		return new GCompartmentBuilder(Types.COMP) //
				.layout(GConstants.Layout.VBOX) //
				.layoutOptions(new GLayoutOptions() //
						.hAlign(GConstants.HAlign.LEFT) //
						.resizeContainer(true)) //
				.addAll(lables)
				.build();
	}
}
