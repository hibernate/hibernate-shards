/**
 * Copyright (C) 2007 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package org.hibernate.shards.util;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * {@link Interceptor} implementation that delegates to multiple {@Interceptor}s.
 *
 * @author maxr@google.com (Max Ross)
 */
public class InterceptorList implements Interceptor {

  private final Collection<Interceptor> interceptors;

  /**
   * Construct an InterceptorList
   * @param interceptors the interceptors to which we'll delegate
   */
  public InterceptorList(Collection<Interceptor> interceptors) {
    this.interceptors = Lists.newArrayList(interceptors);
  }


  /**
   * {@inheritDoc}
   *
   * @param entity {@inheritDoc}
   * @param id {@inheritDoc}
   * @param state {@inheritDoc}
   * @param propertyNames {@inheritDoc}
   * @param types {@inheritDoc}
   * @return true if any of the contained interceptors return true, false otherwise
   * @throws CallbackException {@inheritDoc}
   */
  public boolean onLoad(Object entity, Serializable id, Object[] state,
      String[] propertyNames, Type[] types) throws CallbackException {
    boolean result = false;
    for(Interceptor interceptor : interceptors) {
      result |= interceptor.onLoad(entity, id, state, propertyNames, types);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * @param entity {@inheritDoc}
   * @param id {@inheritDoc}
   * @param currentState {@inheritDoc}
   * @param previousState {@inheritDoc}
   * @param propertyNames {@inheritDoc}
   * @param types {@inheritDoc}
   * @return true if any of the contained interceptors return true, false otherwise
   * @throws CallbackException {@inheritDoc}
   */
  public boolean onFlushDirty(Object entity, Serializable id,
      Object[] currentState, Object[] previousState, String[] propertyNames,
      Type[] types) throws CallbackException {
    boolean result = false;
    for(Interceptor interceptor : interceptors) {
      result |= interceptor.onFlushDirty(
          entity, id, currentState, previousState, propertyNames, types);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   * @param entity {@inheritDoc}
   * @param id {@inheritDoc}
   * @param state {@inheritDoc}
   * @param propertyNames {@inheritDoc}
   * @param types {@inheritDoc}
   * @return true if any of the contained interceptors return true, false otherwise
   * @throws CallbackException {@inheritDoc}
   */
  public boolean onSave(Object entity, Serializable id, Object[] state,
      String[] propertyNames, Type[] types) throws CallbackException {
    boolean result = false;
    for(Interceptor interceptor : interceptors) {
      result |= interceptor.onSave(entity, id, state, propertyNames, types);
    }
    return result;
  }

  public void onDelete(Object entity, Serializable id, Object[] state,
      String[] propertyNames, Type[] types) throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      interceptor.onDelete(entity, id, state, propertyNames, types);
    }
  }

  public void onCollectionRecreate(Object collection, Serializable key)
      throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      interceptor.onCollectionRecreate(collection, key);
    }
  }

  public void onCollectionRemove(Object collection, Serializable key)
      throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      interceptor.onCollectionRemove(collection, key);
    }
  }

  public void onCollectionUpdate(Object collection, Serializable key)
      throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      interceptor.onCollectionUpdate(collection, key);
    }
  }

  public void preFlush(Iterator entities) throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      interceptor.preFlush(entities);
    }
  }

  public void postFlush(Iterator entities) throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      interceptor.postFlush(entities);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param entity {@inheritDoc}
   * @return the first non-null result returned by a contained interceptor, or
   * null if none of the contained interceptors return a non-null result
   */
  public Boolean isTransient(Object entity) {
    for(Interceptor interceptor : interceptors) {
      Boolean result = interceptor.isTransient(entity);
      if(result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @param entity {@inheritDoc}
   * @param id {@inheritDoc}
   * @param currentState {@inheritDoc}
   * @param previousState {@inheritDoc}
   * @param propertyNames {@inheritDoc}
   * @param types {@inheritDoc}
   * @return the first non-null result returned by a contained interceptor, or
   * null if none of the contained interceptors return a non-null result
   */
  public int[] findDirty(Object entity, Serializable id, Object[] currentState,
      Object[] previousState, String[] propertyNames, Type[] types) {
    for(Interceptor interceptor : interceptors) {
      int[] result = interceptor.findDirty(
          entity, id, currentState, previousState, propertyNames, types);
      if(result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @param entityName {@inheritDoc}
   * @param entityMode {@inheritDoc}
   * @param id {@inheritDoc}
   * @return the first non-null result returned by a contained interceptor, or
   * null if none of the contained interceptors return a non-null result
   * @throws CallbackException {@inheritDoc}
   */
  public Object instantiate(String entityName, EntityMode entityMode,
      Serializable id) throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      Object result = interceptor.instantiate(entityName, entityMode, id);
      if(result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @param object {@inheritDoc}
   * @return the first non-null result returned by a contained interceptor, or
   * null if none of the contained interceptors return a non-null result
   * @throws CallbackException {@inheritDoc}
   */
  public String getEntityName(Object object) throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      String result = interceptor.getEntityName(object);
      if(result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @param entityName {@inheritDoc}
   * @param id {@inheritDoc}
   * @return the first non-null result returned by a contained interceptor, or
   * null if none of the contained interceptors return a non-null result
   * @throws CallbackException {@inheritDoc}
   */
  public Object getEntity(String entityName, Serializable id)
      throws CallbackException {
    for(Interceptor interceptor : interceptors) {
      Object result = interceptor.getEntity(entityName, id);
      if(result != null) {
        return result;
      }
    }
    return null;
  }

  public void afterTransactionBegin(Transaction tx) {
    for(Interceptor interceptor : interceptors) {
      interceptor.afterTransactionBegin(tx);
    }
  }

  public void beforeTransactionCompletion(Transaction tx) {
    for(Interceptor interceptor : interceptors) {
      interceptor.beforeTransactionCompletion(tx);
    }
  }

  public void afterTransactionCompletion(Transaction tx) {
    for(Interceptor interceptor : interceptors) {
      interceptor.afterTransactionCompletion(tx);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param sql {@inheritDoc}
   * @return the result of the first contained interceptor that modified the sql,
   * or the original sql if none of the contained interceptors modified the sql.
   */
  public String onPrepareStatement(String sql) {
    for(Interceptor interceptor : interceptors) {
      String modified = interceptor.onPrepareStatement(sql);
      if(!sql.equals(modified)) {
        return modified;
      }
    }
    return sql;
  }

  public Collection<Interceptor> getInnerList() {
    return Collections.unmodifiableCollection(interceptors);
  }
}
