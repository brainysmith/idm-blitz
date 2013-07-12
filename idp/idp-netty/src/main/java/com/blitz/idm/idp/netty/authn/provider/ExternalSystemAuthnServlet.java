package com.blitz.idm.idp.netty.authn.provider;

import com.blitz.idm.idp.config.AuthnMethodEnum;
import com.blitz.idm.idp.config.IdpConfig;
import com.blitz.idm.idp.config.IdpConfigParam;
import com.blitz.idm.idp.config.OrganizationTypeEnum;
import com.blitz.idm.idp.netty.authn.ExtIdpPrincipal;
import com.blitz.idm.idp.netty.authn.ExtIdpPrincipalImpl;
import com.blitz.idm.idp.netty.dc.TestValueEnum;
import edu.internet2.middleware.shibboleth.idp.authn.AuthenticationEngine;
import edu.internet2.middleware.shibboleth.idp.authn.LoginContextEntry;
import edu.internet2.middleware.shibboleth.idp.authn.LoginHandler;
import edu.internet2.middleware.shibboleth.idp.util.HttpServletHelper;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.util.storage.StorageService;
import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExternalSystemAuthnServlet extends HttpServlet {

    /**
     * Class logger.
     */
    private final Logger log = LoggerFactory.getLogger(ExternalSystemAuthnServlet.class);
    /**
     * The authentication method returned to the authentication engine.
     */
    private String authenticationMethod;

    private ServletContext servletContext;

    private static StorageService<String, LoginContextEntry> storageService;

    /**
     * The context-relative url to authentication server.
     */
    private String loginEndpointUrl;


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        String method =
                DatatypeHelper.safeTrimOrNullString(config.getInitParameter(LoginHandler.AUTHENTICATION_METHOD_KEY));
        if (method != null) {
            authenticationMethod = method;
        } else {
            authenticationMethod = AuthnContext.PPT_AUTHN_CTX;
        }

        servletContext = config.getServletContext();
        if (servletContext == null) {
            log.error("Servlet context may not be null");
            throw new IllegalArgumentException("Servlet context may not be null");
        }
        storageService = (StorageService<String, LoginContextEntry>) HttpServletHelper.getStorageService(servletContext);
        if (storageService == null) {
            log.error("Storage service may not be null");
            throw new IllegalArgumentException("Storage service may not be null");
        }

        loginEndpointUrl = IdpConfig.getStringProperty(IdpConfigParam.WEBLOGIN_URL);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/*        String base64AuthnToken = request.getParameter(AuthnToken.TOKEN_PRM);
        if (base64AuthnToken == null) {
            String base64RequestToken = generateBase64RequestToken(request);
            redirectToLoginEndpoint(request, response, base64RequestToken);
            return;
        }
        AuthnToken authnToken = null;
        try {
            authnToken = new AuthnToken(base64AuthnToken);
        } catch (TokenException e) {
            log.error("Bad authn token {} received", authnToken);
            return;
        }

        Cookie loginContextKeyCookie = HttpServletHelper.getCookie(request, HttpServletHelper.LOGIN_CTX_KEY_NAME);
        if (loginContextKeyCookie == null) {
            log.debug("LoginContext key cookie was not present in request");
            return;
        }

        String loginContextKey = DatatypeHelper.safeTrimOrNullString(loginContextKeyCookie.getValue());
        if (loginContextKey == null) {
            log.warn("Corrupted LoginContext Key cookie, it did not contain a value");
            return;
        }

        if (!authnToken.getLoginContextId().equals(loginContextKey)){
            log.error("Another's authn token {} received", authnToken);
            return;
        }
         ExtIdpPrincipal newPrincipal = new ExtIdpPrincipalImpl(authnToken);

        ExtMetadataFilter metadataFilter = getMetadataFilter(request);
        ExtIdpPrincipal principal = getPrincipal(request);
        AuthnMethodEnum authnMethod = AuthnMethodEnum.PWD;
        long oid = 243280312L;
        long orgOid = 0L;
        String orgName = null;
        String name = "000-000-001 26";
        OrganizationTypeEnum orgType = null;
        if (metadataFilter.getAssuranceLevel() != null && metadataFilter.getAssuranceLevel().getLevel() > 20) {
            authnMethod = AuthnMethodEnum.DS;
        }
        if (metadataFilter.needEmployeeRole() || (principal != null && principal.getOrgOid() != 0)) {
            orgOid = 1000000000;
            orgName = "TestOrganization";
            orgType = OrganizationTypeEnum.AGENCY;
        }*/

        ExtIdpPrincipal newPrincipal = new ExtIdpPrincipalImpl(AuthnMethodEnum.lookup(TestValueEnum.AUTHN_METHOD.getValue()), Long.parseLong(TestValueEnum.USER_ID.getValue()) , TestValueEnum.USER_NAME.getValue(), true,
                Long.parseLong(TestValueEnum.ORGANIZATION_ID.getValue()), TestValueEnum.ORGANIZATION_NAME.getValue(), OrganizationTypeEnum.lookup(TestValueEnum.ORGANIZATION_TYPE.getValue()), false);

        request.setAttribute(LoginHandler.PRINCIPAL_KEY, newPrincipal);
        request.setAttribute(LoginHandler.AUTHENTICATION_METHOD_KEY, authenticationMethod);
        AuthenticationEngine.returnToAuthenticationEngine(request, response);
    }

/*    private void redirectToLoginEndpoint(HttpServletRequest request, HttpServletResponse response,
                                       String base64IdToken) {
        try {

            CacheEntryManager.cacheContext(request);
            //

            // redirect to web login
            String loginEndpointUrl = getLoginEndpointUrl(request, base64IdToken);
            log.debug("Redirecting to {}", loginEndpointUrl);
            response.sendRedirect(loginEndpointUrl);
        } catch (IOException ex) {
            log.error("Unable to redirect to authentication server.", ex);
        }
    }

    private ExtMetadataFilter getMetadataFilter(HttpServletRequest request) {
        LoginContext loginContext = (LoginContext) request.getAttribute(HttpServletHelper.LOGIN_CTX_KEY_NAME);
        Session userSession = HttpServletHelper.getUserSession(request);
        String peerEntityId = loginContext.getRelyingPartyId();
        if (peerEntityId == null) {
            log.error("Unable to find peer entity id");
            return null;
        }
        EntityDescriptor entityDescriptor = HttpServletHelper.getRelyingPartyMetadata(peerEntityId,
                HttpServletHelper.getRelyingPartyConfirmationManager(getServletContext()));
        if (entityDescriptor == null) {
            log.error("Unable to find peer entity descriptor");
            return null;
        }
        return new ExtMetadataFilter(entityDescriptor);
    }

    private ExtIdpPrincipal getPrincipal(HttpServletRequest request) {
        Session userSession = HttpServletHelper.getUserSession(request);
        ExtIdpPrincipal principal = null;
        if (userSession != null)
            principal = (ExtIdpPrincipal) IdpPrincipalHelper.getPrincipal(userSession.getSubject());
        return principal;
    }

    private String generateBase64RequestToken(HttpServletRequest request) {
        LoginContext loginContext = (LoginContext) request.getAttribute(HttpServletHelper.LOGIN_CTX_KEY_NAME);
        ExtMetadataFilter metadataFilter = getMetadataFilter(request);
        ExtIdpPrincipal principal = getPrincipal(request);
        String name = null;
        long oid = 0L;
        long orgOid = 0L;
        boolean needEmployeeRole = false;
        AssuranceLevelEnum assuranceLevel = null;
        Set<OrganizationTypeEnum> orgTypes = null;
        if (principal != null) {
            name = principal.getName();
            oid = principal.getOid();
            orgOid = principal.getOrgOid();
        }
        if (metadataFilter != null) {
            needEmployeeRole = metadataFilter.needEmployeeRole();
            assuranceLevel = metadataFilter.getAssuranceLevel();
            orgTypes = metadataFilter.getOrgTypes();
        }
        RequestToken requestToken = new RequestToken(loginContext.getContextKey(), request.getRequestURL().toString(),
                name, oid, orgOid, needEmployeeRole, assuranceLevel, orgTypes);
        String base64RequestToken = requestToken.toBase64();
        if (base64RequestToken == null) {
            log.error("Error converting authn filter token");
        } else {
            log.debug("Autnn filter token [{}] created", base64RequestToken);
        }
        return base64RequestToken;
    }


    private String getLoginEndpointUrl(HttpServletRequest request, String base64AuthnFilter) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(loginEndpointUrl)
                .append("?").append(RequestToken.TOKEN_PRM).append("=").append(base64AuthnFilter);
        return pathBuilder.toString();
    }
    */
}