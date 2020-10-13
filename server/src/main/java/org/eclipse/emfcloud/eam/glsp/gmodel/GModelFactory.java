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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.transform.Source;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.eam.glsp.model.EAMModelState;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.CSS;
import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.Types;
import org.eclipse.glsp.api.jsonrpc.GLSPServerException;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.builder.impl.GGraphBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GEdgeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.emfcloud.compare.model_comparison.UUID_Provider;

import EAM_Metamodel.EAM_Application;
import EAM_Metamodel.EAM_DataCenter;
import EAM_Metamodel.EAM_Landscape;
import EAM_Metamodel.EAM_MetamodelPackage;
import EAM_Metamodel.EAM_Host;
import EAM_Metamodel.EAM_Node;
import EAM_Metamodel.EAM_Product;

public class GModelFactory extends AbstractGModelFactory<EObject, GModelElement> {

	private ClassifierNodeFactory classifierNodeFactory;
	private LabelFactory labelFactory;

	public GModelFactory(EAMModelState modelState) {
		super(modelState);
		classifierNodeFactory = new ClassifierNodeFactory(modelState, this);
		labelFactory = new LabelFactory(modelState);
		getOrCreateRoot();
	}

	@Override
	public GModelElement create(EObject semanticElement) {
		GModelElement result = null;

		if (semanticElement instanceof EAM_Node) {
			result = classifierNodeFactory.create((EAM_Node) semanticElement);
		} else if (semanticElement instanceof EAM_Landscape) {
			result = create((EAM_Landscape) semanticElement);
		}

		if (result == null) {
			throw createFailed(semanticElement);
		}
		return result;
	}

	public GGraph create() {
		return create(modelState.getEditorContext().getEAMFacade().getEPackage());
	}

	public GGraph create(EAM_Landscape ePackage) {
		GGraph graph = getOrCreateRoot();
		graph.setId(toId(ePackage));

		// add all nodes
		graph.getChildren().addAll(ePackage.getNode().stream()//
				.map(this::create)//
				.collect(Collectors.toList()));

		// add hosts
		graph.getChildren().addAll(ePackage.getNode().stream()//
				.filter(e -> e instanceof EAM_DataCenter)
				.map(EAM_DataCenter.class::cast) 
				.flatMap(node -> node.getHost().stream()) //
				.map(this::create)
				.collect(Collectors.toList()));
		
		// create edges
		graph.getChildren().addAll(ePackage.getNode().stream() //
				//.filter(EAM_Node.class::isInstance) //
				//.map(EAM_Node.class::cast) //
				.flatMap(node -> createEdges(node).stream()) //
				.collect(Collectors.toList()));
				
		return graph;
	}
	
	private List<GModelElement> createEdges(EAM_Node node) {
		List<GModelElement> children = new ArrayList<>();
		
		if (node instanceof EAM_Application) {
			// create communication edge
			((EAM_Application) node).getApplication().stream().map(
				s -> createCommunication(node, s, EAM_MetamodelPackage.eINSTANCE.getApplication_Application()))
				.filter(Objects::nonNull).forEach(children::add);
			
			// create association edge
			((EAM_Application) node).getProduct().stream().map(
				s -> createAssociation(node, s, EAM_MetamodelPackage.eINSTANCE.getApplication_Product()))
				.filter(Objects::nonNull).forEach(children::add);
			((EAM_Application) node).getHost().stream().map(
				s -> createAssociation(node, s, EAM_MetamodelPackage.eINSTANCE.getApplication_Host()))
				.filter(Objects::nonNull).forEach(children::add);
		
		} else if (node instanceof EAM_DataCenter) {
			// create association edge
			List<EAM_Host> hosts = ((EAM_DataCenter) node).getHost();
			for (EAM_Host host : hosts) {
				children.add(createAssociation(node, host, EAM_MetamodelPackage.eINSTANCE.getDataCenter_Host()));
			}
		}

		return children;
	}

	public List<GModelElement> createAttributeLabels(EAM_Node node) {
		List<GModelElement> result = null;

		if (node instanceof EAM_Product) {
			result =  createAttributeLabels((EAM_Product) node);
		} else if (node instanceof EAM_Application) {
			result =  createAttributeLabels((EAM_Application) node);
		} else if (node instanceof EAM_Host) {
			result =  createAttributeLabels((EAM_Host) node);
		} else if (node instanceof EAM_DataCenter) {
			result =  createAttributeLabels((EAM_DataCenter) node);
		}

		if (result == null) {
			throw createFailed(node);
		}
		return result;
	}

	private List<GModelElement> createAttributeLabels(EAM_Product node) {
		List<GModelElement> list = new ArrayList<>();
		list.add(createAttributeCompartment(node, "id", String.valueOf(node.getId()), EAM_MetamodelPackage.eINSTANCE.getNode_Id()));
		list.add(createAttributeCompartment(node, "name", String.valueOf(node.getName()), EAM_MetamodelPackage.eINSTANCE.getNode_Name()));
		list.add(createAttributeCompartment(node, "owner", String.valueOf(node.getOwner()), EAM_MetamodelPackage.eINSTANCE.getProduct_Owner()));
		list.add(createAttributeCompartment(node, "department", String.valueOf(node.getDepartment()), EAM_MetamodelPackage.eINSTANCE.getProduct_Department()));
		return list;
	}

	private List<GModelElement> createAttributeLabels(EAM_Application node) {
		List<GModelElement> list = new ArrayList<>();
		list.add(createAttributeCompartment(node, "id", String.valueOf(node.getId()), EAM_MetamodelPackage.eINSTANCE.getNode_Id()));
		list.add(createAttributeCompartment(node, "name", String.valueOf(node.getName()), EAM_MetamodelPackage.eINSTANCE.getNode_Name()));
		list.add(createAttributeCompartment(node, "type", String.valueOf(node.getType().getLiteral()), EAM_MetamodelPackage.eINSTANCE.getApplication_Type()));
		list.add(createAttributeCompartment(node, "version", String.valueOf(node.getVersion()), EAM_MetamodelPackage.eINSTANCE.getApplication_Version()));
		return list;
	}

	private List<GModelElement> createAttributeLabels(EAM_Host node) {
		List<GModelElement> list = new ArrayList<>();
		list.add(createAttributeCompartment(node, "id", String.valueOf(node.getId()), EAM_MetamodelPackage.eINSTANCE.getNode_Id()));
		list.add(createAttributeCompartment(node, "name", String.valueOf(node.getName()), EAM_MetamodelPackage.eINSTANCE.getNode_Name()));
		list.add(createAttributeCompartment(node, "vendor", String.valueOf(node.getVendor()), EAM_MetamodelPackage.eINSTANCE.getHost_Vendor()));
		return list;
	}

	private List<GModelElement> createAttributeLabels(EAM_DataCenter node) {
		List<GModelElement> list = new ArrayList<>();
		list.add(createAttributeCompartment(node, "id", String.valueOf(node.getId()), EAM_MetamodelPackage.eINSTANCE.getNode_Id()));
		list.add(createAttributeCompartment(node, "name", String.valueOf(node.getName()), EAM_MetamodelPackage.eINSTANCE.getNode_Name()));
		list.add(createAttributeCompartment(node, "location", String.valueOf(node.getLocation()), EAM_MetamodelPackage.eINSTANCE.getDataCenter_Location()));
		return list;
	}

	private GCompartment createAttributeCompartment(EAM_Node node, String name, String value, EAttribute attribute) {
		GCompartmentBuilder compartmentBuilder = new GCompartmentBuilder(Types.COMP_ATTRIBUTE) //
			.layout(GConstants.Layout.HBOX) //
			.layoutOptions(new GLayoutOptions() //
					.hAlign(GConstants.HAlign.LEFT) //
					.resizeContainer(true)) //
			.add(labelFactory.createAttributeName(name))
			.add(labelFactory.create(node, name, value, attribute));

			String id = UUID_Provider.getUUID(node, attribute);
			String change = this.modelState.getHighlight().get(id);
			if (change != null) {
				compartmentBuilder.addCssClass(changeToAttributeHighlightClass(change));
			}
			
			return compartmentBuilder.build();
	}
	
	public GEdge createCommunication(EAM_Node source, EAM_Node target, EReference reference) {
		String sourceId = toId(source);
		String targetId = toId(target);
		if (sourceId.isEmpty() || sourceId.isEmpty()) {
			return null;
		}
		
		String id = sourceId + "_" + targetId;
		if (modelState.getClientOptions().get("useStaticIds") != null && modelState.getClientOptions().get("useStaticIds").equals("true")) {
			id = UUID_Provider.getUUID(source, reference, target);
		}

		GEdgeBuilder edgeBuilder = new GEdgeBuilder(Types.EDGE_COMMUNICATION) //
				.id(id)//
				.addCssClass(CSS.EAM_EDGE) //
				.addCssClass(CSS.EAM_EDGE_COMMUNICATION) //
				.sourceId(sourceId) //
				.targetId(targetId) //
				.routerKind(GConstants.RouterKind.MANHATTAN);

		String change = this.modelState.getHighlight().get(id);
		if (change != null) {
			edgeBuilder.addCssClass(changeToEdgeHighlightClass(change));
		}

		return edgeBuilder.build();
	}

	public GEdge createAssociation(EAM_Node source, EAM_Node target, EReference reference) {
		String sourceId = toId(source);
		String targetId = toId(target);
		if (sourceId.isEmpty() || sourceId.isEmpty()) {
			return null;
		}

		String type = (target instanceof EAM_Host && source instanceof EAM_DataCenter) ? Types.EDGE_ASSOCIATION_CONTAINMENT : Types.EDGE_ASSOCIATION;

		String id = sourceId + "_" + targetId;
		if (modelState.getClientOptions().get("useStaticIds") != null && modelState.getClientOptions().get("useStaticIds").equals("true")) {
			id = UUID_Provider.getUUID(source, reference, target);
		}

		GEdgeBuilder edgeBuilder =  new GEdgeBuilder(type) //
				.id(id)//
				.addCssClass(CSS.EAM_EDGE) //
				.addCssClass(CSS.EAM_EDGE_ASSOCIATION) //
				.sourceId(sourceId) //
				.targetId(targetId) //
				.routerKind(GConstants.RouterKind.MANHATTAN);

		String change = this.modelState.getHighlight().get(id);
		if (change != null) {
			edgeBuilder.addCssClass(changeToEdgeHighlightClass(change));
		}

		return edgeBuilder.build();
	}

	private String changeToAttributeHighlightClass(String color) {
		switch (color) {
			case "red":
				return "outlineRed";
			case "green":
				return "outlineGreen";
			case "yellow":
				return "outlineBlue";
		}
		return "";
	}

	private String changeToEdgeHighlightClass(String color) {
		switch (color) {
			case "red":
				return "strokeRed";
			case "green":
				return "strokeGreen";
			case "yellow":
				return "strokeBlue";
		}
		return "";
	}

	public static GLSPServerException createFailed(EObject semanticElement) {
		return new GLSPServerException("Error during model initialization!", new Throwable(
				"No matching GModelElement found for the semanticElement of type: " + semanticElement.getClass()));
	}

	private GGraph getOrCreateRoot() {
		GModelRoot existingRoot = modelState.getRoot();
		if (existingRoot != null && existingRoot instanceof GGraph) {
			GGraph graph = (GGraph) existingRoot;
			graph.getChildren().clear();
			return graph;
		} else {
			GGraph graph = new GGraphBuilder().build();
			modelState.setRoot(graph);
			return graph;
		}
	}
}
