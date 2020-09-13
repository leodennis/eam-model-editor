/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.emfcloud.eam.glsp.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.operation.Operation;
import org.eclipse.glsp.api.registry.MapRegistry;
import org.eclipse.glsp.api.registry.OperationHandlerRegistry;
import org.eclipse.glsp.api.utils.ReflectionUtil;
import org.eclipse.glsp.server.registry.DIOperationHandlerRegistry;

import com.google.inject.Inject;

/**
 * <p>
 * Temporary workaround to support EAM CreateOperations, until https://github.com/eclipse-glsp/glsp/issues/21
 * is fixed.
 * </p>
 * <p>
 * The GLSP version {@link DIOperationHandlerRegistry} has special handling for CreateOperations that requires
 * 1 CreateOperationHandler per element type, which doesn't match the current EAM GLSP structure.
 * </p>
 */
public class EAMDIOperationHandlerRegistry implements OperationHandlerRegistry {

	private final MapRegistry<String, List<OperationHandler>> internalRegistry;

	@Inject
	public EAMDIOperationHandlerRegistry(Set<OperationHandler> handlers) {
		internalRegistry = new MapRegistry<>() {
		};
		handlers.forEach(handler -> {
			ReflectionUtil.construct(handler.getHandledOperationType())
					.ifPresent(operation -> register(operation, handler));
		});
	}

	@Override
	public boolean register(final Operation key, final OperationHandler handler) {
		String keyStr = deriveKey(key);
		List<OperationHandler> handlers;
		if (!internalRegistry.hasKey(keyStr)) {
			handlers = new ArrayList<>();
			internalRegistry.register(keyStr, handlers);
		} else {
			Optional<List<OperationHandler>> optional = internalRegistry.get(keyStr);
			if (! optional.isPresent()) {
				return false;
			}
			handlers = optional.get();
		}
		handlers.add(handler);
		return true;
	}

	@Override
	public boolean deregister(final Operation key) {
		return internalRegistry.deregister(deriveKey(key));
	}

	@Override
	public boolean hasKey(final Operation key) {
		return internalRegistry.hasKey(deriveKey(key));
	}

	@Override
	public Optional<OperationHandler> get(final Operation key) {
		return internalRegistry.get(deriveKey(key))
				.flatMap(list -> list.stream().filter(handler -> handler.handles(key)).findFirst());
	}

	@Override
	public Set<OperationHandler> getAll() {
		return internalRegistry.getAll().stream().flatMap(Collection::stream).collect(Collectors.toSet());
	}

	protected String deriveKey(final Operation key) {
		return key.getClass().getName();
	}
}
