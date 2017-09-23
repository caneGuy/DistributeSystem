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

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;

public class LeaseMaster implements Closeable{

    private ServerSocket masterServer;

    private ThreadFactory factory;

    private Thread masterServerThread;

    private Boolean running = false;

    private LeaseManager leaseManager;

    private LinkedBlockingDeque<Socket> sockets;

    private Thread replyThread;

    LeaseMaster(int port) throws LeaseException{
        try {
            masterServer = new ServerSocket();
            masterServer.setReuseAddress(true);
            masterServer.bind(new InetSocketAddress(port));
            factory = (r) -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
            };
            // running flag should be set before thread start
            running = true;
            masterServerThread = factory.newThread(this::acceptConnections);
            masterServerThread.setName("Master-Server");
            masterServerThread.start();
            //TODO:Default interval is 1 minute, need to fetch from config file
            leaseManager = new LeaseManager(60000);
            sockets = new LinkedBlockingDeque<>();
            replyThread = factory.newThread(this::replyClient);
            replyThread.setName("Server-Reply");
            replyThread.start();
        } catch (IOException e) {
            running = false;
            throw new LeaseException("Init master server instance failed!", e);
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
            } else if (message instanceof LeaseProtocol.UpdateLease) {
                LeaseProtocol.UpdateLease updateMessage = (LeaseProtocol.UpdateLease) message;
                long lastUpdateTime = updateMessage.lastUpdateTime;
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                Lease lease = leaseManager.updateLease(clientName);
                if (lastUpdateTime <= System.currentTimeMillis() - 1000 || lease == null) {
                    out.writeObject(new LeaseProtocol.InvalidLease(lastUpdateTime));
                    leaseManager.removeLease(clientName);
                } else {
                    LeaseProtocol.AcKnowledgeLease acKnowledgeLease =
                            new LeaseProtocol.AcKnowledgeLease(
                                    lease.getLastUpdateTime() + leaseManager.leaseInterval);
                    out.writeObject(acKnowledgeLease);
                }
                out.flush();
            }
        } catch (Exception e) {
            throw new LeaseException("Handle failed for " + socket.getRemoteSocketAddress(), e);
        } finally {
            socket.close();
        }
    }

    /**
     * Clear resources
     */
    public void close() throws IOException{
        running = false;
        sockets.forEach(socket -> {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO
            }
        });
    }
}
