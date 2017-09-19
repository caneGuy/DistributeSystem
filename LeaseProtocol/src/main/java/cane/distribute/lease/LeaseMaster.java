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

import cane.distribute.lease.exception.LeaseException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ThreadFactory;

public class LeaseMaster {

    ServerSocket masterServer;

    ThreadFactory factory;

    Thread masterServerThread;

    LeaseMaster(int port) throws LeaseException{
        try {
            masterServer = new ServerSocket();
            masterServer.setReuseAddress(true);
            masterServer.bind(new InetSocketAddress(port));
            factory = new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            };
            masterServerThread = factory.newThread(new ConnectionAcceptor());
            masterServerThread.start();
        } catch (IOException e) {
            throw new LeaseException("Init master server instance failed!");
        }
    }

    private class ConnectionAcceptor implements Runnable {
        public void run() {
            // TODO
        }
    }
}
