import com.blitz.idm.idp.config.IdpConfig;
import com.blitz.idm.idp.config.IdpConfigParam;
import com.blitz.idm.idp.netty.authn.provider.ErrorJspServlet;
import com.blitz.idm.idp.netty.authn.provider.ExternalSystemAuthnServlet;
import com.blitz.idm.idp.netty.authn.provider.ShibbolethJspServlet;
import edu.internet2.middleware.shibboleth.common.log.SLF4JMDCCleanupFilter;
import edu.internet2.middleware.shibboleth.common.profile.ProfileRequestDispatcherServlet;
import edu.internet2.middleware.shibboleth.idp.StatusServlet;
import edu.internet2.middleware.shibboleth.idp.authn.AuthenticationEngine;
import edu.internet2.middleware.shibboleth.idp.session.IdPSessionFilter;
import edu.internet2.middleware.shibboleth.idp.slo.LogoutServlet;
import edu.internet2.middleware.shibboleth.idp.slo.SLOContextFilter;
import edu.internet2.middleware.shibboleth.idp.slo.SLOServlet;
import edu.internet2.middleware.shibboleth.idp.util.NoCacheFilter;
import net.javaforge.netty.servlet.bridge.ServletBridgeChannelPipelineFactory;
import net.javaforge.netty.servlet.bridge.config.FilterConfiguration;
import net.javaforge.netty.servlet.bridge.config.ServletConfiguration;
import net.javaforge.netty.servlet.bridge.config.WebappConfiguration;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import com.blitz.idm.idp.lb.LoadBalancingFilter;
import com.blitz.idm.idp.storage.StorageServiceFilter;
import com.blitz.idm.idp.system.MemoryConsumingServlet;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final String WEB_ROOT_PATH = IdpConfig.getStringProperty(IdpConfigParam.WEB_CONTEXT_ROOT);
    private static final int HTTPS_PORT = IdpConfig.getIntProperty(IdpConfigParam.HTTPS_PORT);
    private static final String STATUS_PAGE_ALLOWED_IPS = IdpConfig.getStringProperty(IdpConfigParam.STATUS_PAGE_ALLOWED_IPS);


    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        // Configure the server.
        final ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(Executors
                        .newCachedThreadPool(), Executors.newCachedThreadPool()));

        WebappConfiguration webapp = new WebappConfiguration();

        /*
        Spring 2.0 application context files.  Files are loaded in the order they appear with subsequent files
        overwriting same named beans in previous files.
        */
        webapp.addContextParameter("contextConfigLocation","file:${IDP_HOME}/conf/internal.xml; file:${IDP_HOME}/conf/service.xml;");

        /*
         Spring 2.0 listener used to load up the configuration
        */
        webapp.addServletContextListener(ContextLoaderListener.class);

        /*Add IdP SLF4J MDC cleanup filter to all requests*/
        webapp.addFilter(SLF4JMDCCleanupFilter.class, WEB_ROOT_PATH + "/*");

         /*Load balancer filter*/
        webapp.addFilter(LoadBalancingFilter.class, WEB_ROOT_PATH + "/*");

        /* Add IdP Session object to incoming profile requests */
        webapp.addFilter(IdPSessionFilter.class, WEB_ROOT_PATH + "/*");

        /* Filter for caching session, login and logout context in external storage at the end of request . */
        webapp.addFilter(StorageServiceFilter.class, WEB_ROOT_PATH + "/*");

        /* SLO added : Add IdP SLO Context object to incoming profile requests */
        FilterConfiguration sloContextFilterConf = new FilterConfiguration(SLOContextFilter.class,
                WEB_ROOT_PATH + "/profile/SAML2/SOAP/SLO",
                WEB_ROOT_PATH + "/profile/SAML2/Redirect/SLO",
                WEB_ROOT_PATH + "/profile/SAML2/POST/SLO",
                WEB_ROOT_PATH + "/SLOServlet");
        webapp.addFilterConfigurations(sloContextFilterConf);

        /* HTTP headers to every response in order to prevent response caching */
        webapp.addFilter(NoCacheFilter.class, WEB_ROOT_PATH + "/*");

        /* Profile Request Dispatcher */
        webapp.addHttpServlet(ProfileRequestDispatcherServlet.class, WEB_ROOT_PATH + "/profile/*");

        /* Authentication Engine Entry Point */
        webapp.addHttpServlet(AuthenticationEngine.class, WEB_ROOT_PATH + "/AuthnEngine");

        /* SLO added : SLO Servlet */
        webapp.addHttpServlet(SLOServlet.class, WEB_ROOT_PATH + "/SLOServlet");

        /* SLO added : Servlet for IdP - initiated Logout */
        ServletConfiguration logoutServletConf = new ServletConfiguration(LogoutServlet.class, WEB_ROOT_PATH + "/Logout");
        logoutServletConf.addInitParameter("profileHandlerPath", "/profile/SAML2/Redirect/SLO");
        webapp.addServletConfigurations(logoutServletConf);

        /* Servlet for redirecting to external authentication server */
        webapp.addHttpServlet(ExternalSystemAuthnServlet.class, WEB_ROOT_PATH + "/authn/external");

        /* Servlet for displaying IdP status. (Comment it for disabling). */
        ServletConfiguration statusServletConf = new ServletConfiguration(StatusServlet.class, WEB_ROOT_PATH + "/status");
        statusServletConf.addInitParameter("AllowedIPs", STATUS_PAGE_ALLOWED_IPS);
        webapp.addServletConfigurations(statusServletConf);


        /* Servlet for displaying cache usage. (Comment it for disabling). */
        webapp.addHttpServlet(MemoryConsumingServlet.class, WEB_ROOT_PATH + "/memoryConsuming");

        /* Send request to the EntityID to the SAML metadata handler. */
        webapp.addHttpServlet(ShibbolethJspServlet.class, WEB_ROOT_PATH + "/shibboleth");


        /* Error handler. */
        webapp.addHttpServlet(ErrorJspServlet.class, WEB_ROOT_PATH + "/error.jsp");

        // Set up the event pipeline factory.
        final ServletBridgeChannelPipelineFactory servletBridge = new ServletBridgeChannelPipelineFactory(
                webapp);
        bootstrap.setPipelineFactory(servletBridge);

        // Bind and start to accept incoming connections.
        final Channel serverChannel = bootstrap
                .bind(new InetSocketAddress(HTTPS_PORT));

        long end = System.currentTimeMillis();
        log.info(">>> Server started in {} ms .... <<< ", (end - start));

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                servletBridge.shutdown();
                serverChannel.close().awaitUninterruptibly();
                bootstrap.releaseExternalResources();
            }
        });

    }
}
