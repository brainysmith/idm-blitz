/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.profile.provider;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.opensaml.ws.transport.InTransport;
import org.opensaml.ws.transport.OutTransport;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.util.DatatypeHelper;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.shibboleth.common.profile.AbstractErrorHandler;
import edu.internet2.middleware.shibboleth.common.util.StringResourceLoader;

/**
 * An error handler that render an error page by means of evaluating a Velocity template..
 * 
 * The following attributes are available within the velocity context page:
 * 
 * <table>
 * <th>
 * <td>Attribute Name</td>
 * <td>Object Type</td>
 * <td>Value</td>
 * </th>
 * <tr>
 * <td>requestError</td>
 * <td>{@link Throwable}</td>
 * <td>Error that was thrown that triggered the invocation of this handler. </td>
 * </tr>
 * </table>
 */
public class VelocityErrorHandler extends AbstractErrorHandler {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(VelocityErrorHandler.class);

    /** Velocity engine used to render error page. */
    private VelocityEngine velocityEngine;

    /** Location of the template to use to render the error page. */
    private String templatePath;

    /**
     * Constructor.
     * 
     * @param engine engine used to render error page
     * @param template classpath location of template used to render error page
     */
    public VelocityErrorHandler(VelocityEngine engine, String template) {
        if (engine == null) {
            log.error("Velocity engine may not be null");
            throw new IllegalArgumentException("Velocity engine may not be null");
        }
        velocityEngine = engine;

        templatePath = DatatypeHelper.safeTrimOrNullString(template);
        if (templatePath == null) {
            log.error("Velocity template path may not be null or empty");
            throw new IllegalArgumentException("Velocity template path may not be null or empty");
        }
    }

    /**
     * Initializes this error handler by loading the velocity template into the engine.
     * 
     * @throws java.io.IOException thrown if there is a problem reading the template file
     */
    public void initialize() throws IOException {
        String templateString = DatatypeHelper.inputstreamToString(getClass().getResourceAsStream(templatePath), null);
        StringResourceRepository repository = StringResourceLoader.getRepository();

        repository.putStringResource(templatePath, templateString);
    }

    /** {@inheritDoc} */
    public void processRequest(InTransport in, OutTransport out) {
        VelocityContext context = new VelocityContext();
        context.put("request", ((HttpServletRequestAdapter) in).getWrappedRequest());
        context.put("requestError", in.getAttribute(AbstractErrorHandler.ERROR_KEY));
        context.put("encoder", ESAPI.encoder());

        HttpServletResponse response = ((HttpServletResponseAdapter) out).getWrappedResponse();
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "content=\"no-store,no-cache,must-revalidate\"");
        response.setHeader("Pragma","no-cache");
        response.setHeader("Expires","-1");

        try {
            OutputStreamWriter responseWriter = new OutputStreamWriter(out.getOutgoingStream());
            Template template = velocityEngine.getTemplate(templatePath);
            template.merge(context, responseWriter);
            responseWriter.flush();
        } catch (Throwable t) {
            log.error("Unable to evaluate velocity error template", t);
        }

        return;
    }
}