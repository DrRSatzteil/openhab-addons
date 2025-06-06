/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.bluetooth.bluegiga.internal.command.attributeclient;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.bluetooth.bluegiga.internal.BlueGigaDeviceCommand;

/**
 * Class to implement the BlueGiga command <b>readLong</b>.
 * <p>
 * This command can be used to read long attribute values, which are longer than 22 bytes and
 * cannot be read with a simple Read by Handle command. The command starts a procedure, where the
 * client first sends a normal read command to the server and if the returned attribute value
 * length is equal to MTU, the client will send further read long read requests until rest of the
 * attribute is read.
 * <p>
 * This class provides methods for processing BlueGiga API commands.
 * <p>
 * Note that this code is autogenerated. Manual changes may be overwritten.
 *
 * @author Chris Jackson - Initial contribution of Java code generator
 */
@NonNullByDefault
public class BlueGigaReadLongCommand extends BlueGigaDeviceCommand {
    public static final int COMMAND_CLASS = 0x04;
    public static final int COMMAND_METHOD = 0x08;

    /**
     * Attribute handle
     * <p>
     * BlueGiga API type is <i>uint16</i> - Java type is {@link int}
     */
    private int chrHandle;

    /**
     * Attribute handle
     *
     * @param chrHandle the chrHandle to set as {@link int}
     */
    public void setChrHandle(int chrHandle) {
        this.chrHandle = chrHandle;
    }

    @Override
    public int[] serialize() {
        // Serialize the header
        serializeHeader(COMMAND_CLASS, COMMAND_METHOD);

        // Serialize the fields
        serializeUInt8(connection);
        serializeUInt16(chrHandle);

        return getPayload();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BlueGigaReadLongCommand [connection=");
        builder.append(connection);
        builder.append(", chrHandle=");
        builder.append(chrHandle);
        builder.append(']');
        return builder.toString();
    }
}
