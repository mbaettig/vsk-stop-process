
/*
 * Copyright 2026 Hochschule Luzern - Informatik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

/**
 * Server program implementing the Daytime protocol
 * (<a href="https://www.rfc-editor.org/rfc/rfc867.html">RFC 867</a>).
 * The server uses a single thread for accepting and processing connections.
 * After a connection is established, the program immediately sends the daytime
 * string, closes the connection, and waits for a new connection.
 */
public class DaytimeServer {
    private final String host;
    private final int port;
    private Thread thread;
    private final Object hasStopped = new Object();

    public DaytimeServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void stop() {
        System.out.println("Server stopping");
        this.thread.interrupt();
        try {
            synchronized (hasStopped) {
                hasStopped.wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Server stopped");
    }

    public void serve() {
        System.out.println("DaytimeServer listening for connections on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        thread = Thread.currentThread();

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host))) {
            serverSocket.setSoTimeout(1000);
            while (!thread.isInterrupted()) {
                try (Socket client = serverSocket.accept()) {
                    System.out.println("Connection to " + client.getInetAddress());
                    DataOutputStream os = new DataOutputStream(client.getOutputStream());
                    Date date = new Date();
                    os.write((date.toString()).getBytes());
                    System.out.println("Sent " + date + " to " + client.getInetAddress());
                } catch (SocketTimeoutException e) {
                    // ignore: timeout occurs every second allowing check for interruption
                } catch (SocketException e) {
                    System.err.println("Exception while handling connection");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Exception while accepting connection");
            e.printStackTrace();
        }
        System.out.println("Listening stopped");
        synchronized (hasStopped) {
            hasStopped.notify();
        }
    }

    public static void main(final String[] args) {
        try {
            String listenInterface = args[0];
            int listenPort = Integer.parseInt(args[1]);
            DaytimeServer daytimeServer = new DaytimeServer(listenInterface, listenPort);
            daytimeServer.serve();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("usage: DaytimeServer <listenInterface> <listenPort>");
            System.exit(1);
        }
    }
}
