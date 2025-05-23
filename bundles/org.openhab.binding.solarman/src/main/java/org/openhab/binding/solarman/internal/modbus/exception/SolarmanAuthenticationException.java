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
package org.openhab.binding.solarman.internal.modbus.exception;

import java.io.Serial;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Catalin Sanda - Initial contribution
 */
@NonNullByDefault
public class SolarmanAuthenticationException extends SolarmanException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String requestInverterId;
    private final String responseInverterId;

    public SolarmanAuthenticationException(String message, String requestInverterId, String responseInverterId) {
        super(message);
        this.requestInverterId = requestInverterId;
        this.responseInverterId = responseInverterId;
    }

    public String getRequestInverterId() {
        return requestInverterId;
    }

    public String getResponseInverterId() {
        return responseInverterId;
    }
}
