/*
 * Copyright 2022, OpenSergo Authors
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
package io.opensergo.util;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import io.opensergo.log.OpenSergoLogger;

/**
 * @author xzd
 */
public class IdentifierUtils {

    private static String ip;
    private static String hostName;
    private static int currentPid = -1;

    private static final AtomicInteger counter = new AtomicInteger(0);

    static {
        try {
            // Init the host information.
            resolveHost();
        } catch (Throwable e) {
            OpenSergoLogger.error("Failed to resolve local host", e);
        }
    }

    private static void resolveHost() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        hostName = addr.getHostName();
        ip = addr.getHostAddress();
        if (addr.isLoopbackAddress()) {
            // find the first IPv4 Address that not loopback
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface in = interfaces.nextElement();
                Enumeration<InetAddress> addrs = in.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress address = addrs.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        ip = address.getHostAddress();
                    }
                }
            }
        }
    }

    public static int getPid() {
        if (currentPid < 0) {
            resolvePid();
        }
        return currentPid;
    }

    private static void resolvePid() {
        // Note: this will trigger local host resolve, which might be slow.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        currentPid = Integer.parseInt(name.split("@")[0]);
    }

    /**
     * Generate a unique identifier of an OpenSergo client.
     *
     * @return a unique identifier
     */
    public static String generateIdentifier(long clientId) {
        // format: hostname-ip-pid-clientHash
        return String.format("%s-%s-%d-%d", hostName, ip, getPid(), clientId);
    }

}
