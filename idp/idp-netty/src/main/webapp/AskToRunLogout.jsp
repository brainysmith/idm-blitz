<%@ page import="com.blitz.idm.idp.authn.provider.PreviousSessionLoginServlet" %>
<%@ page import="com.blitz.idm.idp.model.FormBean" %>
<%@ page import="com.blitz.idm.idp.authn.util.AuthnHelper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: vkarpov
  Date: 21.10.11
  Time: 14:03
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%
    FormBean formBean = (FormBean)request.getAttribute(FormBean.FORM_BEAN);
    String logoutURL = (String)request.getAttribute(PreviousSessionLoginServlet.LOGOUT_URL);
   //TODO set contomize message for roles logout too
    String pageMessage = (String)request.getAttribute(PreviousSessionLoginServlet.ASK_LOGOUT_PAGE_MESSAGE);
    String pageHeader  = (String)request.getAttribute(PreviousSessionLoginServlet.ASK_LOGOUT_PAGE_HEADER);
    if(logoutURL == null){
        logoutURL = "#";
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title><%= pageHeader %></title>
        <!--
        <link href="<%= request.getContextPath() %>/htdocs/resources/css/style.css" type="text/css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/htdocs/resources/info/css/_styles.css" type="text/css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/htdocs/resources/css/authoriz.css" type="text/css" rel="stylesheet">
        -->
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/htdocs/img/icons/russia-flag.png" />
		<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/htdocs/resources/info/nd/css/epgu.css" />


        <script type="text/javascript" src="<%= request.getContextPath() %>/htdocs/resources/js/jquery/jquery-1.4.2.js" charset="UTF-8"></script>

        <!--[if IE 6]>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/htdocs/resources/css/style_ie6.css"/>
        <script type="text/javascript" src="<%= request.getContextPath() %>/htdocs/resources/js/DD_belatedPNG_0_0_8a-min.js"></script>
        <script type="text/javascript">
            DD_belatedPNG.fix('.pngFix, .pngFix a, .pngFix li, .pngFix span');
        </script>
        <![endif]-->
    </head>

    <body>
    <!-- AskToRunLogout -->
    <div id="wrapper">
        <div id="toolbar"> </div>
            <div id="header">
                <!-- Логотип -->
                <a id="logo" href="<%=AuthnHelper.getBannersURL((String)null, getServletConfig().getServletContext())%>"></a>
                <!-- Телефон -->
                <div id="phone">
                    <h5>Телефоны поддержки:</h5>
                    <ul id="phones">

                        <li><em>в России:</em><strong>8 (800) 100-70-10</strong></li>
                        <li><em>за границей:</em><strong>7 (499) 550-18-39</strong></li>
                    </ul>
                </div>
            </div>

                    <div id="content" class="auth">
                        <div class="wide">
                            <div class="top"></div>
                            <div style="height:445px;" class="center">
						      <div class="cover push-top-1px">
                           <div style="padding:20px 10px;">
                            <h3><%= pageHeader %></h3>
                            <br>
                            <p>
                                <% if(pageMessage != null) { %>
                                  <p><%= pageMessage %></p>
                                <% }%>
                                Для выхода перейдите по
                                <a href="<%= request.getContextPath() + logoutURL%>">ссылке.</a>
                            </p>
                           </div>
                         </div>
                        </div>   <!-- center -->
                        <div class="bottom"></div>
                    </div>
                </div>

        <!-- Подвал -->
        <div id="footer">
            <!-- Логотипы -->
            <div id="logo-mks"></div>
            <div id="logo-rtk"></div>
        </div>
    </div>
    </body>

</html>