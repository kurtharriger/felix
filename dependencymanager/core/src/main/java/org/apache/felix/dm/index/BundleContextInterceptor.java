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
package org.apache.felix.dm.index;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class BundleContextInterceptor extends BundleContextInterceptorBase {
    private final ServiceRegistryCache m_cache;

    public BundleContextInterceptor(ServiceRegistryCache cache, BundleContext context) {
        super(context);
        m_cache = cache;
    }

    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        FilterIndex filterIndex = m_cache.hasFilterIndexFor(null, filter);
        if (filterIndex != null) {
            filterIndex.addServiceListener(listener, filter);
        }
        else {
            m_context.addServiceListener(listener, filter);
        }
    }

    public void addServiceListener(ServiceListener listener) {
        FilterIndex filterIndex = m_cache.hasFilterIndexFor(null, null);
        if (filterIndex != null) {
            filterIndex.addServiceListener(listener, null);
        }
        else {
            m_context.addServiceListener(listener);
        }
    }

    public void removeServiceListener(ServiceListener listener) {
        FilterIndex filterIndex = m_cache.hasFilterIndexFor(null, null);
        if (filterIndex != null) {
            filterIndex.removeServiceListener(listener);
        }
        else {
            m_context.removeServiceListener(listener);
        }
    }

    public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // first we ask the cache if there is an index for our request (class and filter combination)
        FilterIndex filterIndex = m_cache.hasFilterIndexFor(clazz, filter);
        if (filterIndex != null) {
            List /* <ServiceReference> */ result = filterIndex.getAllServiceReferences(clazz, filter);
            Iterator iterator = result.iterator();
            while (iterator.hasNext()) {
                ServiceReference reference = (ServiceReference) iterator.next();
                String[] list = (String[]) reference.getProperty(Constants.OBJECTCLASS);
                for (int i = 0; i < list.length; i++) {
                    if (!reference.isAssignableTo(m_context.getBundle(), list[i])) {
                        iterator.remove();
                        break;
                    }
                }
            }
            return (ServiceReference[]) result.toArray(new ServiceReference[result.size()]);
        }
        else {
            // if they don't know, we ask the real bundle context instead
            return m_context.getServiceReferences(clazz, filter);
        }
    }

    public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // first we ask the cache if there is an index for our request (class and filter combination)
        FilterIndex filterIndex = m_cache.hasFilterIndexFor(clazz, filter);
        if (filterIndex != null) {
            List /* <ServiceReference> */ result = filterIndex.getAllServiceReferences(clazz, filter);
            return (ServiceReference[]) result.toArray(new ServiceReference[result.size()]);
        }
        else {
            // if they don't know, we ask the real bundle context instead
            return m_context.getAllServiceReferences(clazz, filter);
        }
    }

    public ServiceReference getServiceReference(String clazz) {
        ServiceReference[] references;
        try {
            references = getServiceReferences(clazz, null);
            Arrays.sort(references);
            return references[references.length - 1];
        }
        catch (InvalidSyntaxException e) {
            throw new Error("Invalid filter syntax thrown for null filter.", e);
        }
    }

    public void serviceChanged(ServiceEvent event) {
        m_cache.serviceChangedForFilterIndices(event);
    }
}
