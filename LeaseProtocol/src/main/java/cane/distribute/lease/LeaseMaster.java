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
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;

public class LeaseMaster implements Closeable{

    ServerSocket masterServer;

    ThreadFactory factory;

    Thread masterServerThread;

    Boolean running;

    LeaseManager leaseManager;

    LinkedBlockingDeque<Socket> sockets;

    Thread replyThread;

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
            masterServerThread = factory.newThread(this::acceptConnections);
            masterServerThread.start();
            //TODO:Default interval is 1 minute, need to fetch from config file
            leaseManager = new LeaseManager(60000);
            sockets = new LinkedBlockingDeque<>();
            replyThread = factory.newThread(this::replyClient);
            replyThread.start();
            running = true;
        } catch (IOException e) {
            throw new LeaseException("Init master server instance failed!");
        }
    }

    private void acceptConnections() {
        try {
            while (running) {
                Socket socket = masterServer.accept();
                sockets.add(socket);
            }
        } catch (IOException e) {
            //TODO
        }
    }

    public void replyClient() {
        try {
            while (running) {
                Socket socket = sockets.take();
                handleMessage(socket);
            }
        } catch (InterruptedException | IOException e) {
            // TODO
        } catch (LeaseException e) {
            // TODO
        }
    }

    private void handleMessage(Socket socket) throws LeaseException, IOException{
        try {
            FilteredObjectInputStream in = new FilteredObjectInputStream(socket.getInputStream());
            LeaseProtocol.ClientMessage message = (LeaseProtocol.ClientMessage) in.readObject();
            String clientName = message.clientName;
            if (message instanceof LeaseProtocol.Hello) {
                Lease lease = leaseManager.newLease(clientName);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                LeaseProtocol.AcKnowledgeLease acKnowledgeLease =
                        new LeaseProtocol.AcKnowledgeLease(
                                lease.getLastUpdateTime() + leaseManager.leaseInterval);
                out.writeObject(acKnowledgeLease);
                out.flush();
                out.close();
            } else {
                // update: need to check if expire or not by lastUpdateTime
                // if expired then send a invalid message to client
            }
        } catch (Throwable e) {
            throw new LeaseException("Handle failed for " + socket.getRemoteSocketAddress());
        } finally {
            socket.close();
        }
    }

    /**
     * Clear resources
     */
    public void close() {
        sockets.forEach(socket -> {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO
            }
        });
    }
}
