package com.blitz.idm.idp.netty.authn.provider;

import edu.internet2.middleware.shibboleth.idp.slo.SingleLogoutContext;
import edu.internet2.middleware.shibboleth.idp.slo.SingleLogoutContextStorageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class SloQuestionJspServlet extends HttpServlet {

    /**
     * Class logger.
     */
    private final Logger log = LoggerFactory.getLogger(SloQuestionJspServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SingleLogoutContext sloContext = SingleLogoutContextStorageHelper.getSingleLogoutContext(request);
        String contextPath = request.getContextPath();
        Locale defaultLocale = Locale.ENGLISH;
        Locale locale = request.getLocale();
        String requesterSP = sloContext.getServiceInformation().
                get(sloContext.getRequesterEntityID()).getDisplayName(locale, defaultLocale);

        StringBuilder html = new StringBuilder();
        html    .append("<html>\n")
                .append("<head>\n")
                .append("   <link title=\"style\" href=\"").append(contextPath).append("/css/main.css\" type=\"text/css\" rel=\"stylesheet\" />\n")
                .append("   <title>Shibboleth IdP Logout</title>\n")
                .append("</head>\n\n")

                .append("<body>\n")
                .append("   <div class=\"content\">\n")
                .append("       <h1>Logging out</h1>\n")
                .append("       <h2>You have logged out from</h2>\n")
                .append("       <div class=\"row\">").append(requesterSP).append("</div>\n")
                .append("       <h2>You are logged in on these services</h2>\n");


        int i = 0;
        for (SingleLogoutContext.LogoutInformation service : sloContext.getServiceInformation().values()) {
            if (service.getEntityID().equals(sloContext.getRequesterEntityID())) {
                continue;
            }
            i++;
            html    .append("<div class=\"row\">").append(service.getDisplayName(locale, defaultLocale)).append("</div>\n");
        }
        html    .append("       <div class=\"controller\">\n")
                .append("           Do you want to logout from all the services above?<br />\n")
                .append("           <form action=\"").append(contextPath).append("/SLOServlet\">\n")
                .append("               <input type=\"hidden\" name=\"logout\"/>\n")
                .append("               <input type=\"submit\" value=\"Yes, all services\" />\n")
                .append("           </form>\n")
                .append("           <form action=\"").append(contextPath).append("/SLOServlet\">\n")
                .append("               <input type=\"hidden\" name=\"finish\"/>\n")
                .append("               <input type=\"submit\" value=\"No, only from ").append(requesterSP).append("\" />\n")
                .append("           </form>\n")
                .append("       <div class=\"clear\"></div>\n")
                .append("       </div>\n")
                .append("   </div>\n")
                .append("</body>\n")
                .append("</html>\n");

        log.debug("SloQuestion page generated:\n " + html.toString());
        PrintWriter out = response.getWriter();
        out.print(html.toString());

    }

}
