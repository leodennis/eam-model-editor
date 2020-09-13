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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.elk.alg.layered.options.LayeredMetaDataProvider;
import org.eclipse.glsp.layout.ElkLayoutEngine;
import org.eclipse.glsp.server.launch.DefaultGLSPServerLauncher;
import org.eclipse.glsp.server.launch.GLSPServerLauncher;

import EAM_Metamodel.EAM_MetamodelPackage;

public class EAMServerLauncher {

	private static final Logger LOG = Logger.getLogger(EAMServerLauncher.class);
	
	private static final int DEFAULT_PORT = 5007;
	
	public static void main(String[] args) {
		int port = DEFAULT_PORT;

		System.out.println("Starting Server");
		System.out.println("Starting Server.");
		System.out.println("Starting Server..");
		System.out.println("Starting Server...");
		
		EAM_MetamodelPackage.eINSTANCE.eClass();
		ElkLayoutEngine.initialize(new LayeredMetaDataProvider());
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
		GLSPServerLauncher launcher = new DefaultGLSPServerLauncher(new EAMGLSPModule());
		launcher.start("localhost", port);
	}

	private static int getPort(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if ("--port".contentEquals(args[i])) {
				return Integer.parseInt(args[i+1]);
			}
		}
		LOG.info("The server port was not specified; using default port 5007");
		return DEFAULT_PORT;
	}
}
