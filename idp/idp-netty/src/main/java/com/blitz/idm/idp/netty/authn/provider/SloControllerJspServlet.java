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
        html.append("<html>");
        html.append("<head>");
        html.append("<link title=\"style\" href=\"" + contextPath + "/webstatic/css/main.css\" type=\"text/css\" rel=\"stylesheet\" />");
        html.append("<title>Shibboleth IdP Logout</title>");
        html.append("<script language=\"javascript\" type=\"text/javascript\">");
        html.append("<!--\n" +
                " var timer = 0;\n" +
                "var timeout;\n" +
                "\n" +
                "var xhr = new XMLHttpRequest();\n" +
                "\n" +
                "function checkStatus() {\n" +
                "        xhr.onreadystatechange = updateStatus;\n" +
                "xhr.open(\"GET\", \"" + contextPath + "/SLOServlet?status\", true);\n" +
                "xhr.send(null);\n" +
                "}\n" +
                "\n" +
                "        function updateStatus() {\n" +
                "        if (xhr.readyState != 4 || xhr.status != 200) {\n" +
                "        return;\n" +
                "}\n" +
                "\n" +
                "        var sloFailed = false;\n" +
                "var resp = eval(\"(\" + xhr.responseText + \")\");\n" +
                "var ready = true;\n" +
                "\n" +
                "for (var service in resp) {\n" +
                "        var entity = resp[service].entityID;\n" +
                "var status = resp[service].logoutStatus;\n" +
                "var src = \"indicator.gif\";\n" +
                "\n" +
                "switch(status) {\n" +
                "        case \"LOGOUT_SUCCEEDED\" :\n" +
                "        src = \"success.png\";\n" +
                "break;\n" +
                "case \"LOGOUT_FAILED\" :\n" +
                "        case \"LOGOUT_UNSUPPORTED\" :\n" +
                "        case \"LOGOUT_TIMED_OUT\" :\n" +
                "        src = \"failed.png\";\n" +
                "sloFailed = true;\n" +
                "break;\n" +
                "case \"LOGOUT_ATTEMPTED\" :\n" +
                "        case \"LOGGED_IN\" :\n" +
                "        if (timer >= 8) {\n" +
                "        src = \"failed.png\";\n" +
                "sloFailed = true;\n" +
                "ready = true;\n" +
                "} else {\n" +
                "        src = \"indicator.gif\";\n" +
                "ready = false;\n" +
                "}\n" +
                "        break;\n" +
                "}\n" +
                "\n" +
                "        document.getElementById(entity).src = \"" + contextPath + "/webstatic/images/\" + src;\n" +
                "}\n" +
                "\n" +
                "        if (ready) {\n" +
                "        finish(sloFailed);\n" +
                "}\n" +
                "        }\n" +
                "\n" +
                "        function finish(sloFailed) {\n" +
                "        var str = \"You have successfully logged out\";\n" +
                "var className = \"success\";\n" +
                "if (sloFailed){\n" +
                "        str = \"Logout failed. Please exit from your browser to complete the logout process.\" ;\n" +
                "className = \"fail\";\n" +
                "}\n" +
                "        //document.getElementById(\"result\").className = className;\n" +
                "        //document.getElementById(\"result\").innerHTML = str;\n" +
                "        //document.getElementById(\"result\").innerHTML += '<iframe src=\"" + contextPath + "/SLOServlet?finish\"></iframe><div class=\"clear\"></div>';\n" +
                "        clearTimeout(timeout);\n" +
                "if(!sloFailed) {setTimeout(\"window.location='" + contextPath + "/SLOServlet?finish'\", 2500); }\n" +
                "        }\n" +
                "\n" +
                "        function tick() {\n" +
                "        timer += 1;\n" +
                "if (timer  == 1 || timer  == 2 || timer  == 4 || timer  == 8) {\n" +
                "        checkStatus();\n" +
                "}\n" +
                "        if (timer > 8) {\n" +
                "        finish(true);\n" +
                "} else {\n" +
                "        timeout = setTimeout(\"tick()\", 1000);\n" +
                "}\n" +
                "        }\n" +
                "\n" +
                "        timeout = setTimeout(\"tick()\", 1000);\n" +
                "//-->");
        html.append("</script>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"content\">");
        html.append("<h1>Logging out</h1>");
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
            src.append("/webstatic/images/");
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
            html.append("<div class=\"row\">");
            html.append("<script type=\"text/javascript\">");
            html.append("<!-- \n" +
                    "document.write('" + service.getDisplayName(locale, defaultLocale)  +
                    "<img alt=\"" + service.getLogoutStatus().toString() + "\" id=\"" + service.getEntityID() + "\" src=\"" + src.toString() + "\">');\n" +
                    "//-->");
            html.append("</script>");
            html.append("<noscript>" + service.getDisplayName(locale, defaultLocale));
            if (logoutString) {
                html.append("<a href=\"" +  contextPath + "/SLOServlet?action&entityID=" + entityID + "\" target=\"_blank\">Logout from this SP</a>");
            }  else {
                html.append("<img alt=\"" + service.getLogoutStatus().toString() + "\" id=\"" + service.getEntityID()+ "\" src=\"" + src.toString() +"\">");
            }
            html.append("</noscript>");
            html.append("</div>");
            if (service.isLoggedIn()) {
                html.append("<script type=\"text/javascript\">");
                html.append("<!--\n" +
                        "document.write('<iframe src=\"" + contextPath + "/SLOServlet?action&entityID=" + entityID +"\" width=\"0\" height=\"0\"></iframe>');" +
                        "//-->");
                html.append("</script>");
            }
        }
        html.append("<div id=\"result\"></div>");
        html.append("<noscript>");
        html.append("<p align=\"center\">");
        if (logoutString || sloAttempted) {
            html.append("<form action=\"" + contextPath + "/SLOServlet\" style=\"padding-top:10px;width:90%;clear:both;\"><input type=\"hidden\" name=\"logout\" /><input type=\"submit\" value=\"Refresh\" /></form><div class=\"clear\"></div>");
        } else {
            if (sloFailed) {
                html.append("<div id=\"result\" class=\"fail\">Logout failed. Please exit from your browser to complete the logout process.</div>");

             } else {
                html.append("<div id=\"result\" class=\"success\">You have successfully logged out<form action=\"" + contextPath + "/SLOServlet\" style=\"padding-top:10px;width:90%;clear:both;\"><input type=\"hidden\" name=\"finish\" /><input type=\"submit\" value=\"Finish logout\" /></form><div class=\"clear\"></div></div>");
             }
        }
        html.append("</p>");
        html.append("</noscript>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");


        PrintWriter out = response.getWriter();
        out.print(html.toString());

    }

}
