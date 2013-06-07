package com.blitz.idm.idp.netty.authn.provider;

import edu.internet2.middleware.shibboleth.common.profile.AbstractErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ErrorJspServlet extends HttpServlet {

    /**
     * Class logger.
     */
    private final Logger log = LoggerFactory.getLogger(ErrorJspServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<body>");
        out.println("<img src=\"images/logo.jpg\"/>");
        out.println("<h3>ERROR</h3>");
        Throwable error = (Throwable) request.getAttribute(AbstractErrorHandler.ERROR_KEY);
        if (error != null) {
            org.owasp.esapi.Encoder esapiEncoder = org.owasp.esapi.ESAPI.encoder();
            out.println("Error Message: " + esapiEncoder.encodeForHTML(error.getMessage()));
        }
        out.println("</body>");
        out.println("</html>");
    }

}