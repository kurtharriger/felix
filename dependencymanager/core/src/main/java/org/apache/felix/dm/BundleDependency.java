/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.dm;

import org.osgi.framework.Bundle;

public interface BundleDependency extends Dependency, ComponentDependencyDeclaration {
    /**
     * Sets the callbacks for this dependency. These callbacks can be used as hooks whenever a dependency is added or removed.
     * When you specify callbacks, the auto configuration feature is automatically turned off, because we're assuming you don't
     * need it in this case.
     * 
     * @param added the method to call when a bundle was added
     * @param removed the method to call when a bundle was removed
     * @return the bundle dependency
     */
    public BundleDependency setCallbacks(String added, String removed);

    /**
     * Sets the callbacks for this dependency. These callbacks can be used as hooks whenever a dependency is added, changed or
     * removed. When you specify callbacks, the auto configuration feature is automatically turned off, because we're assuming
     * you don't need it in this case.
     * 
     * @param added the method to call when a bundle was added
     * @param changed the method to call when a bundle was changed
     * @param removed the method to call when a bundle was removed
     * @return the bundle dependency
     */
    public BundleDependency setCallbacks(String added, String changed, String removed);

    /**
     * Sets the callbacks for this dependency. These callbacks can be used as hooks whenever a dependency is added or removed.
     * They are called on the instance you provide. When you specify callbacks, the auto configuration feature is automatically
     * turned off, because we're assuming you don't need it in this case.
     * 
     * @param instance the instance to call the callbacks on
     * @param added the method to call when a bundle was added
     * @param removed the method to call when a bundle was removed
     * @return the bundle dependency
     */
    public BundleDependency setCallbacks(Object instance, String added, String removed);

    /**
     * Sets the callbacks for this dependency. These callbacks can be used as hooks whenever a dependency is added, changed or
     * removed. They are called on the instance you provide. When you specify callbacks, the auto configuration feature is
     * automatically turned off, because we're assuming you don't need it in this case.
     * 
     * @param instance the instance to call the callbacks on
     * @param added the method to call when a bundle was added
     * @param changed the method to call when a bundle was changed
     * @param removed the method to call when a bundle was removed
     * @return the bundle dependency
     */
    public BundleDependency setCallbacks(Object instance, String added, String changed, String removed);

    /**
     * Enables auto configuration for this dependency. This means the component implementation (composition) will be
     * injected with this bundle dependency automatically.
     * 
     * @param autoConfig <code>true</code> to enable auto configuration
     * @return the bundle dependency
     */
    public BundleDependency setAutoConfig(boolean autoConfig);

    /**
     * Sets the dependency to be required.
     * 
     * @param required <code>true</code> if this bundle dependency is required
     * @return the bundle dependency
     */
    public BundleDependency setRequired(boolean required);

    /**
     * Sets the bundle to depend on directly.
     * 
     * @param bundle the bundle to depend on
     * @return the bundle dependency
     */
    public BundleDependency setBundle(Bundle bundle);

    /**
     * Sets the filter condition to depend on. Filters are matched against the full manifest of a bundle.
     * 
     * @param filter the filter condition
     * @return the bundle dependency
     * @throws IllegalArgumentException
     */
    public BundleDependency setFilter(String filter) throws IllegalArgumentException;

    /**
     * Sets the bundle state mask to depend on. The OSGi BundleTracker explains this mask in more detail, but
     * it is basically a mask with flags for each potential state a bundle can be in.
     * 
     * @param mask the mask to use
     * @return the bundle dependency
     */
    public BundleDependency setStateMask(int mask);

    /**
     * Sets property propagation. If set to <code>true</code> any bundle manifest properties will be added
     * to the service properties of the component that has this dependency (if it registers as a service).
     * 
     * @param propagate <code>true</code> to propagate the bundle manifest properties
     * @return the bundle dependency
     */
    public BundleDependency setPropagate(boolean propagate);

    /**
     * Sets the dependency to be bound to this instance. An instance bound dependency means the component is
     * kept alive even if the dependency goes away, which internally means you can keep your state until you
     * get activated again. This is mainly used when declaring additional dependencies from within the
     * <code>init</code> life cycle method of a component.
     * 
     * @param isInstanceBound <code>true</code> if this dependency is instance bound
     * @return the bundle dependency
     */
    public BundleDependency setInstanceBound(boolean isInstanceBound);
}
