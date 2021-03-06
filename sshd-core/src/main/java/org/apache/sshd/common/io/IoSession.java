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
package org.apache.sshd.common.io;

import java.net.SocketAddress;

import org.apache.sshd.common.future.CloseFuture;
import org.apache.sshd.common.util.Buffer;

public interface IoSession {

    /**
     * Returns a unique identifier for this session.  Every session has its own
     * ID which is different from each other.
     */
    long getId();

    /**
     * Returns the value of the user-defined attribute of this session.
     *
     * @param key the key of the attribute
     * @return <tt>null</tt> if there is no attribute with the specified key
     */
    Object getAttribute(Object key);

    /**
     * Sets a user-defined attribute.
     *
     * @param key   the key of the attribute
     * @param value the value of the attribute
     * @return The old value of the attribute.  <tt>null</tt> if it is new.
     */
    Object setAttribute(Object key, Object value);

    /**
     * Returns the socket address of remote peer.
     */
    SocketAddress getRemoteAddress();

    /**
     * Returns the socket address of local machine which is associated with this
     * session.
     */
    SocketAddress getLocalAddress();

    /**
     * Write a packet on the socket.
     */
    IoWriteFuture write(Buffer buffer);


    /**
     * Closes this session immediately or after all queued write requests
     * are flushed.  This operation is asynchronous.  Wait for the returned
     * {@link CloseFuture} if you want to wait for the session actually closed.
     *
     * @param immediately {@code true} to close this session immediately.
     *                    The pending write requests will simply be discarded.
     *                    {@code false} to close this session after all queued
     *                    write requests are flushed.
     */
    IoCloseFuture close(boolean immediately);

    /**
     * Returns the IoService that created this session.
     */
    IoService getService();
}
