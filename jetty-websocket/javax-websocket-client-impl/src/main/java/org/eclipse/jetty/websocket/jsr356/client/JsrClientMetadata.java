//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.websocket.jsr356.client;

import java.util.List;

import javax.websocket.ClientEndpoint;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;

import org.eclipse.jetty.websocket.api.InvalidWebSocketException;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.eclipse.jetty.websocket.jsr356.annotations.JsrMetadata;

public class JsrClientMetadata extends JsrMetadata<ClientEndpoint>
{
    private final ClientEndpoint endpoint;
    private final AnnotatedClientEndpointConfig config;

    public JsrClientMetadata(ClientContainer container, Class<?> websocket) throws DeploymentException
    {
        super(websocket);

        ClientEndpoint anno = websocket.getAnnotation(ClientEndpoint.class);
        if (anno == null)
        {
            throw new InvalidWebSocketException(String.format("Unsupported WebSocket object [%s], missing @%s annotation",websocket.getName(),
                    ClientEndpoint.class.getName()));
        }

        this.endpoint = anno;
        this.config = new AnnotatedClientEndpointConfig(anno);
    }

    @Override
    public ClientEndpoint getAnnotation()
    {
        return endpoint;
    }

    public AnnotatedClientEndpointConfig getConfig()
    {
        return config;
    }

    @Override
    protected List<Class<? extends Decoder>> getConfiguredDecoders()
    {
        return config.getDecoders();
    }
}