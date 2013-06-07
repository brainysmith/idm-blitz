package com.blitz.idm.idp.netty.authn.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShibbolethJspServlet extends HttpServlet {

    /**
     * Class logger.
     */
    private final Logger log = LoggerFactory.getLogger(ShibbolethJspServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/profile/Metadata/SAML");
            dispatcher.forward(request, response);
            return;
        } catch (IOException e) {
            log.error("Unable to return control back to authentication engine", e);
        } catch (ServletException e) {
            log.error("Unable to return control back to authentication engine", e);
        }
    }

}