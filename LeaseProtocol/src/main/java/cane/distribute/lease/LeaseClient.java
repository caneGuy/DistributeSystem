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
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;

public class LeaseClient implements Closeable{

    Thread clientThread;

    Boolean running = false;

    Socket clientSocket;

    ObjectOutputStream out;

    String clientName = "Default"; // client name may be configurable

    long leaseExpireTime;

    String masterHost;
    int masterPort;

    LeaseClient(String host, int port) throws LeaseException{
        try {
            masterHost = host;
            masterPort = port;
            clientSocket = new Socket(host, port);
            clientThread = ((ThreadFactory)(Runnable r) -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }).newThread(this::handleMessage);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            clientName = InetAddress.getLocalHost().getHostName();
            sendMessage(new LeaseProtocol.Hello(clientName));
            running = true;
        } catch (Exception e) {
            throw new LeaseException("Failed to init client!", e);
        }

    }

    private void handleMessage() {
        while (running) {
            try {
                FilteredObjectInputStream in =
                        new FilteredObjectInputStream(clientSocket.getInputStream());
                LeaseProtocol.ServerMessage message =
                        (LeaseProtocol.ServerMessage)in.readObject();
                if (message instanceof LeaseProtocol.AcKnowledgeLease) {
                    leaseExpireTime = message.leaseExpireTime;
                    updateLease();
                } else if (message instanceof LeaseProtocol.InvalidLease) {
                    close();
                    clientSocket = new Socket(masterHost, masterPort);
                    out = new ObjectOutputStream(clientSocket.getOutputStream());
                }
            } catch (Exception e) {
                // TODO
            }
        }
    }

    private void updateLease() throws LeaseException{
        while (leaseExpireTime > System.currentTimeMillis()) {
            // Do nothing
        }
        sendMessage(new LeaseProtocol.UpdateLease(leaseExpireTime, clientName));
    }

    public void doCall(LeaseCallBack callBack) throws LeaseException{
        if (leaseExpireTime < System.currentTimeMillis()) {
            throw new LeaseException("Lease has expired,can not call this operation.");
        }
        callBack.call();
    }

    private void sendMessage(LeaseProtocol.ClientMessage message) throws LeaseException{
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            throw new LeaseException("Send message failed!", e);
        }
    }

    @Override
    public void close() throws IOException {
        leaseExpireTime = -1L;
        out.close();
        clientSocket.close();
    }
}
