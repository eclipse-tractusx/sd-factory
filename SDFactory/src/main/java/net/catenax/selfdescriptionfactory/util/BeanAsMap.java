package net.catenax.selfdescriptionfactory.util;
/*
 * Copyright 2011 Jesper de Jong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class BeanAsMap {

    private BeanAsMap() {
    }

    /**
     * Returns a {@code Map<String, Object>} that reflects the bean. The bean's properties are visible as entries in the map.
     * The map is backed by the bean - putting a value in the map will set the value in the bean.
     *
     * @param bean The Java bean to view as a map.
     * @return A {@code Map<String, Object>} which is a view on the bean.
     * @throws IntrospectionException If an error occurs while trying to introspect the bean.
     */
    public static Map<String, Object> asMap(final Object bean) throws IntrospectionException {
        // Find getter and setter methods of bean properties
        final Map<String, Method[]> methods = new HashMap<String, Method[]>();
        for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors()) {
            methods.put(pd.getName(), new Method[]{pd.getReadMethod(), pd.getWriteMethod()});
        }

        return new AbstractMap<String, Object>() {

            @Override
            public Set<Entry<String, Object>> entrySet() {
                return new AbstractSet<Entry<String, Object>>() {

                    @Override
                    public int size() {
                        return methods.size();
                    }

                    @Override
                    public Iterator<Entry<String, Object>> iterator() {
                        return new Iterator<Entry<String, Object>>() {

                            private Iterator<Entry<String, Method[]>> iter = methods.entrySet().iterator();

                            @Override
                            public boolean hasNext() {
                                return iter.hasNext();
                            }

                            @Override
                            public Entry<String, Object> next() {
                                final Entry<String, Method[]> e = iter.next();
                                final Method[] m = e.getValue();

                                return new Entry<String, Object>() {

                                    @Override
                                    public String getKey() {
                                        return e.getKey();
                                    }

                                    @Override
                                    public Object getValue() {
                                        try {
                                            return m[0].invoke(bean);
                                        } catch (InvocationTargetException ite) {
                                            throw new IllegalArgumentException(ite.getTargetException());
                                        } catch (IllegalAccessException iae) {
                                            throw new IllegalArgumentException(iae);
                                        }
                                    }

                                    @Override
                                    public Object setValue(Object value) {
                                        Object old;

                                        try {
                                            old = m[0].invoke(bean);
                                            m[1].invoke(bean, value);
                                        } catch (InvocationTargetException ite) {
                                            throw new IllegalArgumentException(ite.getTargetException());
                                        } catch (IllegalAccessException iae) {
                                            throw new IllegalArgumentException(iae);
                                        }

                                        return old;
                                    }
                                };
                            }

                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException("remove() is not supported");
                            }
                        };
                    }
                };
            }

            @Override
            public Object put(String key, Object value) {
                Method[] m = methods.get(key);
                if (m == null) {
                    throw new IllegalArgumentException("Unknown property: " + key);
                }

                Object old;

                try {
                    old = m[0].invoke(bean);
                    m[1].invoke(bean, value);
                } catch (InvocationTargetException ite) {
                    throw new IllegalArgumentException(ite.getTargetException());
                } catch (IllegalAccessException iae) {
                    throw new IllegalArgumentException(iae);
                }

                return old;
            }
        };
    }
}