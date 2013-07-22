import com.blitz.idm.handler.HttpsServletBridgeChannelPipelineFactory;
import com.blitz.idm.idp.config.IdpConfig;
import com.blitz.idm.idp.config.IdpConfigParam;
import com.blitz.idm.idp.lb.LoadBalancingFilter;
import com.blitz.idm.idp.netty.authn.provider.*;
import com.blitz.idm.idp.storage.StorageServiceFilter;
import com.blitz.idm.idp.system.MemoryConsumingServlet;
import com.blitz.idm.servlet.config.WebApp;
import com.blitz.idm.servlet.config.WebAppFilter;
import com.blitz.idm.servlet.config.WebAppServlet;
import com.blitz.idm.servlet.config.WebAppServletContextListener;
import edu.internet2.middleware.shibboleth.common.log.SLF4JMDCCleanupFilter;
import edu.internet2.middleware.shibboleth.common.profile.ProfileRequestDispatcherServlet;
import edu.internet2.middleware.shibboleth.idp.StatusServlet;
import edu.internet2.middleware.shibboleth.idp.authn.AuthenticationEngine;
import edu.internet2.middleware.shibboleth.idp.session.IdPSessionFilter;
import edu.internet2.middleware.shibboleth.idp.slo.LogoutServlet;
import edu.internet2.middleware.shibboleth.idp.slo.SLOContextFilter;
import edu.internet2.middleware.shibboleth.idp.slo.SLOServlet;
import edu.internet2.middleware.shibboleth.idp.util.NoCacheFilter;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final String APP_NAME = "idp";
    private static final String SERVER_NAME = "netty-idp-server";
    private static final String CONFIG_DIR = IdpConfig.getConfigDir();
    private static final String WEB_ROOT_PATH = IdpConfig.getStringProperty(IdpConfigParam.WEB_CONTEXT_ROOT);
    private static final int HTTPS_PORT = IdpConfig.getIntProperty(IdpConfigParam.HTTPS_PORT);
    private static final String STATUS_PAGE_ALLOWED_IPS = IdpConfig.getStringProperty(IdpConfigParam.STATUS_PAGE_ALLOWED_IPS);


    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        // Configure the server.
        final ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(Executors
                        .newCachedThreadPool(), Executors.newCachedThreadPool()));


        /* CONTEXT PARAMETERS */
        Map<String, String> contextParameters = new HashMap<String, String>(1);
        /*
        Spring 2.0 application context files.  Files are loaded in the order they appear with subsequent files
        overwriting same named beans in previous files.
        */
        contextParameters.put("contextConfigLocation", "file:${IDP_HOME}/conf/internal.xml; file:${IDP_HOME}/conf/service.xml;");


        /* LISTENERS */
        Collection<WebAppServletContextListener> contextListeners = new ArrayList<WebAppServletContextListener>(1);
        /*
         Spring 2.0 listener used to load up the configuration
        */
        contextListeners.add(new WebAppServletContextListener(APP_NAME, new ContextLoaderListener()));


        /* FILTERS */
        Collection<WebAppFilter> requestFilters = new ArrayList<WebAppFilter>();
        Collection<WebAppFilter> forwardFilters = new ArrayList<WebAppFilter>();

        /*Add IdP SLF4J MDC cleanup filter to all requests*/
        requestFilters.add(new WebAppFilter(SLF4JMDCCleanupFilter.class, "/*"));

         /*Load balancer filter*/
        requestFilters.add(new WebAppFilter(LoadBalancingFilter.class, "/*"));

        /* Add IdP Session object to incoming profile requests */
        requestFilters.add(new WebAppFilter(IdPSessionFilter.class, "/*"));

        /* Filter for caching session, login and logout context in external storage at the end of request . */
        requestFilters.add(new WebAppFilter(StorageServiceFilter.class, "/*"));

        /* SLO added : Add IdP SLO Context object to incoming profile requests */
        requestFilters.add(new WebAppFilter(SLOContextFilter.class,
                "/profile/SAML2/SOAP/SLO",
                "/profile/SAML2/Redirect/SLO",
                "/profile/SAML2/POST/SLO",
                "/SLOServlet"));
        forwardFilters.add(new WebAppFilter(SLOContextFilter.class,
               "/SLOServlet"));

        /* HTTP headers to every response in order to prevent response caching */
        requestFilters.add(new WebAppFilter(NoCacheFilter.class, "/*"));


        /* SERVLETS */
        Collection<WebAppServlet> servlets = new ArrayList<WebAppServlet>();
        /* Profile Request Dispatcher */
        servlets.add(new WebAppServlet(ProfileRequestDispatcherServlet.class, "/profile/*"));

        /* Authentication Engine Entry Point */
        servlets.add(new WebAppServlet(AuthenticationEngine.class, "/AuthnEngine"));

        /* SLO added : SLO Servlet */
        servlets.add(new WebAppServlet(SLOServlet.class, "/SLOServlet"));

        /* SLO added : Servlet for IdP - initiated Logout */
        servlets.add(new WebAppServlet(LogoutServlet.class, "/Logout")
                .addInitParameter("profileHandlerPath", "/profile/SAML2/Redirect/SLO"));

        /* Servlet for redirecting to external authentication server */
        servlets.add(new WebAppServlet(ExternalSystemAuthnServlet.class, "/authn/external"));

        /* Servlet for displaying IdP status. (Comment it for disabling). */
        servlets.add(new WebAppServlet(StatusServlet.class, "/status")
                .addInitParameter("AllowedIPs", STATUS_PAGE_ALLOWED_IPS));

        /* Servlet for displaying cache usage. (Comment it for disabling). */
        servlets.add(new WebAppServlet(MemoryConsumingServlet.class, "/memoryConsuming"));

        /* Send request to the EntityID to the SAML metadata handler. */
        servlets.add(new WebAppServlet(ShibbolethJspServlet.class, "/shibboleth"));

        /* Error handler. */
        servlets.add(new WebAppServlet(SloControllerJspServlet.class, "/sloController.jsp"));

        /* Error handler. */
        servlets.add(new WebAppServlet(ErrorJspServlet.class, "/error.jsp"));

        WebApp webapp = new WebApp(APP_NAME, "/idp", 60*60,
                contextParameters, contextListeners, requestFilters, forwardFilters, servlets);

        webapp.setStaticResourcesFolder("/webstatic");
        Map<String, WebApp> webappConfigurationMap = new HashMap<String, WebApp>(1);
        webappConfigurationMap.put(webapp.getName(), webapp);

        // Set up the event pipeline factory.
        final HttpsServletBridgeChannelPipelineFactory servletBridgeFactory = new HttpsServletBridgeChannelPipelineFactory(SERVER_NAME, CONFIG_DIR,
                webappConfigurationMap);
        bootstrap.setPipelineFactory(servletBridgeFactory);

        // Bind and start to accept incoming connections.
        final Channel serverChannel = bootstrap
                .bind(new InetSocketAddress(HTTPS_PORT));

        long end = System.currentTimeMillis();
        log.info(">>> Server started in {} ms .... <<< ", (end - start));

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                servletBridgeFactory.shutdown();
                serverChannel.close().awaitUninterruptibly();
                bootstrap.releaseExternalResources();
            }
        });

    }
}
