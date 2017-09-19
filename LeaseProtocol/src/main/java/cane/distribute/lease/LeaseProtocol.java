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

import java.io.Serializable;

final class LeaseProtocol {

    static class Message implements Serializable {

    }

    /**
     * Hello message send from client to server.
     */
    static class Hello extends Message {

    }

    /**
     * Send from client to server,like heartbeat to
     * update lease.
     */
    static class UpdateLease extends Message {

    }

    /**
     * Send from server to client,indicate that server know
     * client need update the lease.
     */
    static class AcKnowledgeLease extends Message {

    }
}
