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
package org.openhab.binding.comfoair.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link ComfoAirSerialException} is thrown on errors relating the serial connection.
 *
 * @author Hans Böhm - Initial contribution
 */
@NonNullByDefault
public class ComfoAirSerialException extends Exception {
    private static final long serialVersionUID = 5439939653331189828L;

    public ComfoAirSerialException(Throwable cause) {
        super(cause);
    }

    public ComfoAirSerialException(String message) {
        super(message);
    }

    public ComfoAirSerialException(String message, Exception e) {
        super(message, e);
    }
}
