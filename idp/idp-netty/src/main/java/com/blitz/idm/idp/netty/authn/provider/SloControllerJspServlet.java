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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class SloControllerJspServlet extends HttpServlet {

    /**
     * Class logger.
     */
    private final Logger log = LoggerFactory.getLogger(SloControllerJspServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SingleLogoutContext sloContext = SingleLogoutContextStorageHelper.getSingleLogoutContext(request);
        String contextPath = request.getContextPath();
        Locale defaultLocale = Locale.ENGLISH;
        Locale locale = request.getLocale();
        Boolean logoutString = false;
        Boolean sloFailed = false;
        Boolean sloAttempted = false;


        StringBuilder html = new StringBuilder();
        html    .append("<html>\n")
                .append("<head>\n")
                .append("   <link title=\"style\" href=\"").append(contextPath).append("/css/main.css\" type=\"text/css\" rel=\"stylesheet\" />\n")
                .append("   <title>Shibboleth IdP Logout</title>\n")
                .append("   <script language=\"javascript\" type=\"text/javascript\">\n")
                .append("       <!--\n")
                .append("       var timer = 0;\n")
                .append("       var timeout;\n\n")

                .append("       var xhr = new XMLHttpRequest();\n\n")

                .append("       function checkStatus() {\n")
                .append("           xhr.onreadystatechange = updateStatus;\n")
                .append("           xhr.open(\"GET\", \"").append(contextPath).append("/SLOServlet?status\", true);\n")
                .append("           xhr.send(null);\n")
                .append("       }\n\n")

                .append("       function updateStatus() {\n")
                .append("           if (xhr.readyState != 4 || xhr.status != 200) {\n")
                .append("               return;\n")
                .append("           }\n\n")

                .append("           var sloFailed = false;\n")
                .append("           var resp = eval(\"(\" + xhr.responseText + \")\");\n")
                .append("           var ready = true;\n\n")

                .append("           for (var service in resp) {\n")
                .append("               var entity = resp[service].entityID;\n")
                .append("               var status = resp[service].logoutStatus;\n")
                .append("               var src = \"indicator.gif\";\n\n")

                .append("               switch(status) {\n")
                .append("                   case \"LOGOUT_SUCCEEDED\" :\n")
                .append("                       src = \"success.png\";\n")
                .append("                       break;\n")
                .append("                   case \"LOGOUT_FAILED\" :\n")
                .append("                   case \"LOGOUT_UNSUPPORTED\" :\n")
                .append("                   case \"LOGOUT_TIMED_OUT\" :\n")
                .append("                       src = \"failed.png\";\n")
                .append("                       sloFailed = true;\n")
                .append("                       break;\n")
                .append("                   case \"LOGOUT_ATTEMPTED\" :\n")
                .append("                   case \"LOGGED_IN\" :\n")
                .append("                       if (timer >= 8) {\n")
                .append("                           src = \"failed.png\";\n")
                .append("                           sloFailed = true;\n")
                .append("                           ready = true;\n")
                .append("                       } else {\n")
                .append("                           src = \"indicator.gif\";\n")
                .append("                           ready = false;\n")
                .append("                       }\n")
                .append("                       break;\n")
                .append("               }\n\n")

                .append("               document.getElementById(entity).src = \"").append(contextPath).append("/images/\" + src;\n")
                .append("           }\n\n")

                .append("           if (ready) {\n")
                .append("               finish(sloFailed);\n")
                .append("           }\n")
                .append("       }\n\n")

                .append("       function finish(sloFailed) {\n")
                .append("           var str = \"You have successfully logged out\";\n")
                .append("           var className = \"success\";\n")
                .append("           if (sloFailed){\n")
                .append("               str = \"Logout failed. Please exit from your browser to complete the logout process.\" ;\n")
                .append("               className = \"fail\";\n")
                .append("           }\n")
                .append("           document.getElementById(\"result\").className = className;\n")
                .append("           document.getElementById(\"result\").innerHTML = str;\n")
                .append("           //document.getElementById(\"result\").innerHTML += '<iframe src=\"").append(contextPath).append("/SLOServlet?finish\"></iframe><div class=\"clear\"></div>';\n")
                .append("           clearTimeout(timeout);\n")
                .append("           if(!sloFailed) {\n")
                .append("               setTimeout(\"window.location='").append(contextPath).append("/SLOServlet?finish'\", 2500);\n")
                .append("           }\n")
                .append("       }\n\n")

                .append("       function tick() {\n")
                .append("           timer += 1;\n")
                .append("           if (timer  == 1 || timer  == 2 || timer  == 4 || timer  == 8) {\n")
                .append("               checkStatus();\n")
                .append("           }\n")
                .append("           if (timer > 8) {\n")
                .append("               finish(true);\n")
                .append("           } else {\n")
                .append("               timeout = setTimeout(\"tick()\", 2000);\n")
                .append("           }\n")
                .append("       }\n\n")

                .append("       timeout = setTimeout(\"tick()\", 2000);\n")
                .append("       //-->\n")
                .append("   </script>\n")
                .append("</head>\n\n")

                .append("<body>\n")
                .append("   <div class=\"content\">\n")
                .append("       <h1>Logging out</h1>\n");
        int i = 0;
        for (SingleLogoutContext.LogoutInformation service : sloContext.getServiceInformation().values()) {
            i++;
            String entityID = null;
            try {
                entityID = URLEncoder.encode(service.getEntityID(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }

            StringBuilder src = new StringBuilder(contextPath);
            src.append("/images/");
            switch (service.getLogoutStatus()) {
                case LOGGED_IN:
                    logoutString = true;
                case LOGOUT_ATTEMPTED:
                    sloAttempted = true;
                    src.append("indicator.gif");
                    break;
                case LOGOUT_UNSUPPORTED:
                case LOGOUT_FAILED:
                case LOGOUT_TIMED_OUT:
                    sloFailed = true;
                    src.append("failed.png");
                    break;
                case LOGOUT_SUCCEEDED:
                    logoutString = false;
                    src.append("success.png");
                    break;
            }
            html.append("       <div class=\"row\">\n")
                .append("           <script type=\"text/javascript\">\n")
                .append("               <!-- \n")
                .append("               document.write('").append(service.getDisplayName(locale, defaultLocale)).append(" <img alt=\"").append(service.getLogoutStatus().toString()).append("\" id=\"" + service.getEntityID() + "\" src=\"" + src.toString()).append("\">');\n")
                .append("               //-->\n")
                .append("           </script>\n")
                .append("           <noscript>").append(service.getDisplayName(locale, defaultLocale));

            if (logoutString) {
                html
                .append(" <a href=\"").append(contextPath).append("/SLOServlet?action&entityID=").append(entityID).append("\" target=\"_blank\">Logout from this SP</a>\n");
            } else {
                html
                .append(" <img alt=\"").append(service.getLogoutStatus().toString()).append("\" id=\"").append(service.getEntityID()).append("\" src=\"").append(src.toString()).append("\">\n");
            }
            html
                .append("           </noscript>\n")
                .append("       </div>\n");

            if (service.isLoggedIn()) {
                html
                .append("       <script type=\"text/javascript\">\n")
                .append("           <!--\n")
                .append("           document.write('<iframe src=\"").append(contextPath).append("/SLOServlet?action&entityID=").append(entityID +"\" width=\"0\" height=\"0\"></iframe>');\n")
                .append("           //-->\n")
                .append("       </script>\n");
            }
        }

        html
                .append("       <div id=\"result\"></div>\n")
                .append("       <noscript>\n")
                .append("           <p align=\"center\">\n");
        if (logoutString || sloAttempted) {
            html
                 .append("              <form action=\"").append(contextPath).append("/SLOServlet\" style=\"padding-top:10px;width:90%;clear:both;\">)\n")
                 .append("                  <input type=\"hidden\" name=\"logout\" />)\n")
                 .append("                  <input type=\"submit\" value=\"Refresh\" />\n")
                 .append("              </form>\n")
                 .append("              <div class=\"clear\">\n")
                 .append("              </div>\n");
        } else {
            if (sloFailed) {
                html
                 .append("              <div id=\"result\" class=\"fail\">Logout failed. Please exit from your browser to complete the logout process.\n")
                 .append("              </div>\n");

            } else {
                html
                 .append("              <div id=\"result\" class=\"success\">You have successfully logged out\n")
                 .append("                  <form action=\"").append(contextPath).append("/SLOServlet\" style=\"padding-top:10px;width:90%;clear:both;\">\n")
                 .append("                      <input type=\"hidden\" name=\"finish\" />\n")
                 .append("                      <input type=\"submit\" value=\"Finish logout\" />\n")
                 .append("                  </form>\n")
                 .append("                  <div class=\"clear\">\n")
                 .append("                  </div>\n")
                 .append("              </div>\n");
            }
        }
        html    .append("           </p>\n")
                .append("       </noscript>\n")
                .append("   </div>\n")
                .append("</body>\n")
                .append("</html>\n");

        log.debug("SloController page generated:\n " + html.toString());
        PrintWriter out = response.getWriter();
        out.print(html.toString());

    }

}
