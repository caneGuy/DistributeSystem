/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cane.distribute.lease;

import javax.lang.model.element.NestingKind;
import java.io.Serializable;

final class LeaseProtocol {

    static class Message implements Serializable {

    }

    /**
     * Base class for message send from client to server.
     */
    static class ClientMessage extends Message {
        final String clientName;

        ClientMessage(String name) {
            clientName = name;
        }
    }

    /**
     * Hello message send from client to server.
     */
    static class Hello extends ClientMessage {

        Hello(String name) {
            super(name);
        }
    }

    /**
     * Send from client to server,like heartbeat to
     * update lease.
     */
    static class UpdateLease extends ClientMessage {
        final long lastUpdateTime;

        UpdateLease(long time, String name) {
            super(name);
            lastUpdateTime = time;
        }
    }

    /**
     * Base class for message send from server to client.
     */
    static class ServerMessage extends Message {
        final long leaseExpireTime;

        ServerMessage(long time) {
            leaseExpireTime = time;
        }
    }

    /**
     * Send from server to client,indicate that server know
     * client need update the lease.
     */
    static class AcKnowledgeLease extends ServerMessage {

        AcKnowledgeLease(long expireTime) {
            super(expireTime);
        }
    }

    /**
     * Send from server to client to indicate that lease has expired,
     * client need to reconnect to server.
     */
    static class InvalidLease extends ServerMessage {

        InvalidLease(long expireTime) {
            super(expireTime);
        }
    }
}
