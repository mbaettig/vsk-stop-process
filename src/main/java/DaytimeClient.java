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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Connects to a server on localhost that implements the daytime protocol.
 * The program queries the server and outputs its response to the console.
 */
public class DaytimeClient {
    private final String host;
    private final int port;

    public DaytimeClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Queries date and time from the DayTime service specified during construction.
     * 
     * @return The daytime if the call is successful.
     * @throws IOException if any kind of error occurs.
     */
    public String getTime() throws IOException {
        // note: try-with-resources automatically closes socket
        try (Socket socket = new Socket(host, port)) {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("error occuring during fetching date time");
            throw e;
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            DaytimeClient daytimeClient = new DaytimeClient(host, port);
            String time = daytimeClient.getTime();
            System.out.println(time);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("usage: DaytimeClient <listenInterface> <listenPort>");
            System.exit(1);
        }
    }
}
