/*
 * Copyright 2020 White Magic Software, Ltd.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.whitemagicsoftware.kmcaster.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Responsible for notifying its list of managed listeners when property
 * change events have occurred. The change events will only be fired if the
 * new value differs from the old value.
 *
 * @param <P> Type of property that, when changed, will try to issue a change
 *            event to any listeners.
 */
public abstract class PropertyDispatcher<P> {
  /**
   * Calling {@link PropertyChangeSupport#getPropertyChangeListeners()} creates
   * a new list every time. Calls to add listeners are only performed during
   * setup, so this is a micro-optimization to avoid recreating the list on
   * each event fired.
   */
  private PropertyChangeListener[] mListeners;

  private final PropertyChangeSupport mDispatcher =
      new PropertyChangeSupport( this );

  /**
   * Adds a new listener to the internal dispatcher. The dispatcher uses a map,
   * so calling this multiple times for the same listener will not result in
   * the same listener receiving multiple notifications for one event.
   *
   * @param listener The class to notify when property values change, a value
   *                 of {@code null} will have no effect.
   */
  public void addPropertyChangeListener(
      final PropertyChangeListener listener ) {
    mDispatcher.addPropertyChangeListener( listener );
    mListeners = mDispatcher.getPropertyChangeListeners();
  }

  /**
   * Called to fire the property change with the two given values differ.
   * Normally events for the same old and new value are swallowed silently,
   * which prevents double-key presses from bubbling up. Tracking double-key
   * presses is used to increment a counter that is displayed on the key when
   * the user continually types the same regular key.
   *
   * @param p Property name that has changed.
   * @param o Old property value.
   * @param n New property value.
   */
  protected void fire( final P p, final String o, final String n ) {
    final var pName = p.toString();
    final var event = new PropertyChangeEvent( mDispatcher, pName, o, n );

    for( final var listener : mListeners ) {
      listener.propertyChange( event );
    }
  }

  /**
   * Delegates to {@link #fire(P, String, String)} with {@link Boolean} values
   * as strings. If the old and new values are the same, this will not send
   * the event.
   *
   * @param p Property name that has changed.
   * @param o Old property value.
   * @param n New property value.
   */
  protected void tryFire( final P p, final boolean o, final boolean n ) {
    if( o != n ) {
      fire( p, Boolean.toString( o ), Boolean.toString( n ) );
    }
  }
}
