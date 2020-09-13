package org.eclipse.emfcloud.eam.glsp.palette;

import java.util.List;
import java.util.Map;

import org.eclipse.emfcloud.eam.glsp.util.EAMConfig.Types;
import org.eclipse.glsp.api.action.kind.TriggerEdgeCreationAction;
import org.eclipse.glsp.api.action.kind.TriggerNodeCreationAction;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.provider.ToolPaletteItemProvider;
import org.eclipse.glsp.api.types.PaletteItem;

import com.google.common.collect.Lists;

public class EAMToolPaletteItemProvider implements ToolPaletteItemProvider {

	@Override
	public List<PaletteItem> getItems(Map<String, String> args, GraphicalModelState modelState) {
		System.err.println("Create palette");
		return Lists.newArrayList(nodes(), edges());
	}

	private PaletteItem nodes() {
		PaletteItem createNode1 = node(Types.NODE_PRODUCT, "Product");
		PaletteItem createNode2 = node(Types.NODE_APPLICATION, "Application");
		PaletteItem createNode3 = node(Types.NODE_HOST, "Host");
		PaletteItem createNode4 = node(Types.NODE_DATACENTER, "Datacenter");

		List<PaletteItem> nodes = Lists.newArrayList(createNode1, createNode2, createNode3, createNode4); // add more here
		return PaletteItem.createPaletteGroup("eam.classifier", "Nodes", nodes);
	}

	private PaletteItem edges() {
		List<PaletteItem> edges = Lists.newArrayList();
		edges.add(edge(Types.EDGE_ASSOCIATION, "Association"));
		edges.add(edge(Types.EDGE_COMMUNICATION, "Communication"));
		return PaletteItem.createPaletteGroup("eam.edges", "Edges", edges);
	}

	private PaletteItem node(String elementTypeId, String label) {
		return new PaletteItem(elementTypeId, label, new TriggerNodeCreationAction(elementTypeId));
	}

	private PaletteItem edge(String elementTypeId, String label) {
		return new PaletteItem(elementTypeId, label, new TriggerEdgeCreationAction(elementTypeId));
	}
}
