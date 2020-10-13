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
package org.eclipse.emfcloud.eam.glsp.util;

public final class EAMConfig {

	public static final class Types {

		public static final String LABEL_NAME = "label:name";
		public static final String LABEL_TEXT = "label:text";
		public static final String LABEL_EDGE_NAME = "label:edge-name";
		public static final String LABEL_EDGE_MULTIPLICITY = "label:edge-multiplicity";
		public static final String COMP = "comp:comp";
		public static final String COMP_HEADER = "comp:header";
		public static final String COMP_ATTRIBUTE = "comp:attribute";
		public static final String LABEL_ICON = "label:icon";
		public static final String DECORATOR = "comp:decorator";

		public static final String NODE_PRODUCT = "node:product";
		public static final String NODE_APPLICATION = "node:application";
		public static final String NODE_HOST = "node:host";
		public static final String NODE_DATACENTER = "node:datacenter";

		public static final String ATTRIBUTE = "label:attribute";
		public static final String ATTRIBUTE_PRODUCT_TYPE = "label:attribute-product-type";
		public static final String ATTRIBUTE_NAME = "label:attribute-name";
		public static final String EDGE_COMMUNICATION = "edge:communication";
		public static final String EDGE_ASSOCIATION = "edge:association";
		public static final String EDGE_ASSOCIATION_CONTAINMENT = "edge:association-containment";

		private Types() {
		};
	}

	public static final class CSS {

		public static final String NODE = "eam-node";
		public static final String FOREIGN_PACKAGE = "foreign-package";;
		public static final String EAM_EDGE = "eam-edge";
		public static final String EAM_EDGE_COMMUNICATION  = "comunication";
		public static final String EAM_EDGE_ASSOCIATION  = "association";
		public static final String ITALIC = "italic";

		private CSS() {
		};
	}

	private EAMConfig() {
	};
}
