package com.blitz.idm.idp.netty.dc;

import com.blitz.idm.idp.dc.Attribute;

import java.util.HashMap;
import java.util.Map;

/**
 * User: agumerov
 * Date: 14.12.12
 */
public enum AttributeEnum implements Attribute {
    /* PRINCIPAL INFO */
    //old attribute
    AUTH_TOKEN("urn:mace:dir:attribute:authToken", "authToken", HashCode.AUTH_TOKEN_HASH_CODE),    //legacy attribute
    DEVICE_TYPE("urn:blitz:deviceType", "deviceType", HashCode.DEVICE_TYPE_HASH_CODE),
    PERSON_TYPE("urn:blitz:personType", "personType", HashCode.PERSON_TYPE_HASH_CODE),

    ID_TOKEN("urn:blitz:IDToken", "IDToken", HashCode.ID_TOKEN_HASH_CODE),
    AUTHN_METHOD("urn:blitz:authnMethod", "authnMethod", HashCode.AUTHN_METHOD_HASH_CODE),
    GLOBAL_ROLE("urn:blitz:globalRole", "globalRole", HashCode.GLOBAL_ROLE_HASH_CODE),
    USER_ID("urn:mace:dir:attribute:userId", "userId", HashCode.USER_ID_HASH_CODE),
    USER_NAME("urn:blitz:userName", "userName", HashCode.USER_NAME_HASH_CODE),
    USER_NAME_OLD("urn:mace:dir:attribute:name", "name", HashCode.USER_NAME_OLD_HASH_CODE),//legacy attribute
    ASSURANCE_LEVEL("urn:blitz:assuranceLevel", "assuranceLevel", HashCode.ASSURANCE_LEVEL_HASH_CODE),

    /* PRINCIPAL ORGANIZATION INFO */
    ORGANIZATION_ID("urn:blitz:orgId", "orgId", HashCode.ORGANIZATION_ID_HASH_CODE),
    ORGANIZATION_TYPE("urn:blitz:orgType", "orgType", HashCode.ORGANIZATION_TYPE_HASH_CODE),
    ORGANIZATION_NAME("urn:blitz:orgName", "orgName", HashCode.ORGANIZATION_NAME_HASH_CODE),

    /* PERSON FULL INFO , PERSON_WITH_CONTACTS, PERSON_SHORT_INFO */
    PERSON_LASTNAME("urn:mace:dir:attribute:lastName", "lastName", HashCode.PERSON_LASTNAME_HASH_CODE),
    PERSON_FIRSTNAME("urn:mace:dir:attribute:firstName", "firstName", HashCode.PERSON_FIRSTNAME_HASH_CODE),
    PERSON_MIDDLENAME("urn:mace:dir:attribute:middleName", "middleName", HashCode.PERSON_MIDDLENAME_HASH_CODE),
    PERSON_SNILS("urn:blitz:personSNILS", "personSNILS", HashCode.PERSON_SNILS_HASH_CODE),
    PERSON_SNILS_OLD("urn:mace:dir:attribute:snils", "snils", HashCode.PERSON_SNILS_OLD_HASH_CODE),//legacy attribute
    PERSON_INN("urn:blitz:personINN", "personINN", HashCode.PERSON_INN_HASH_CODE),
    PERSON_INN_OLD("urn:mace:dir:attribute:inn", "inn", HashCode.PERSON_INN_OLD_HASH_CODE), //legacy attribute
    PERSON_CITIZENSHIP("urn:blitz:personCitizenship", "personCitizenship", HashCode.PERSON_CITIZENSHIP_HASH_CODE),//not used yet

    /* PERSON FULL INFO , PERSON_WITH_CONTACTS */
    PERSON_CONTACTS("urn:blitz:principalContacts", "principalContacts", HashCode.PERSON_CONTACTS_HASH_CODE),//not used yet
    PERSON_EMAIL("urn:blitz:personEMail", "personEMail", HashCode.PERSON_EMAIL_HASH_CODE), //not used yet
    PERSON_MOBILE("urn:blitz:personMobilePhone", "personMobilePhone", HashCode.PERSON_MOBILE_HASH_CODE), //not used yet

    /* PERSON FULL INFO */
    PERSON_DOCUMENTS("urn:blitz:principalDocuments", "principalDocuments", HashCode.PERSON_DOCUMENTS_HASH_CODE),//not used yet
    PERSON_ADDRESSES("urn:blitz:principalAddresses", "principalAddresses", HashCode.PERSON_ADDRESSES_HASH_CODE), //not used yet

    /* PERSON FULL INFO */
    PERSON_IN_ORG("urn:blitz:attachedToOrg", "attachedToOrg", HashCode.PERSON_IN_ORG_HASH_CODE), //not used yet

    /* ORGANIZATION SHORT INFO */
    ORGANIZATION_OGRN("urn:blitz:orgOGRN", "orgOGRN", HashCode.ORGANIZATION_OGRN_HASH_CODE),
    ORGANIZATION_INN("urn:blitz:orgINN", "orgINN", HashCode.ORGANIZATION_INN_HASH_CODE),
    ORGANIZATION_SHORT_NAME("urn:blitz:orgShortName", "orgShortName", HashCode.ORGANIZATION_SHORT_NAME_HASH_CODE),//not used yet
    ORGANIZATION_KPP("urn:blitz:orgKPP", "orgKPP", HashCode.ORGANIZATION_KPP_HASH_CODE),
    ORGANIZATION_BRANCH_KPP("urn:blitz:orgBranchKPP", "orgBranchKPP", HashCode.ORGANIZATION_BRANCH_KPP_HASH_CODE), //not used yet
    ORGANIZATION_LEGAL_FORM("urn:blitz:orgLegalForm", "orgLegalForm", HashCode.ORGANIZATION_LEGAL_FORM_HASH_CODE),
    ORGANIZATION_NSI_ID("urn:blitz:nsiId", "nsiId", HashCode.ORGANIZATION_NSI_ID_HASH_CODE),//not used yet


    /* ORGANIZATION FULL INFO */
    STAFF_UNIT_POSITION("urn:blitz:orgPosition", "orgPosition", HashCode.STAFF_UNIT_POSITION_HASH_CODE),
    ORGANIZATION_CONTACTS("urn:blitz:orgContacts", "orgContacts", HashCode.ORGANIZATION_CONTACTS_HASH_CODE),
    ORGANIZATION_ADDRESSES("urn:blitz:orgAddresses", "orgAddresses", HashCode.ORGANIZATION_ADDRESSES_HASH_CODE),

    /* BUSINESS PERSON INFO */
    PERSON_OGRN("urn:blitz:personOGRN", "personOGRN", HashCode.PERSON_OGRN_HASH_CODE),

    /* AUTHORITY INFO */
    SYSTEM_AUTHORITIES("urn:blitz:systemAuthority", "systemAuthority", HashCode.SYSTEM_AUTHORITIES_HASH_CODE),


    /* not used attributes */
    AUTHN_CERTIFICATE("urn:blitz:authnCertificate", "authnCertificate", HashCode.AUTHN_CERTIFICATE_HASH_CODE),
    ENTERPRISE_ROLE("urn:blitz:enterpriseRole", "enterpriseRole", HashCode.ENTERPRISE_ROLE_HASH_CODE),
    IS_MOBILE_SIGN_EXISTS("urn:blitz:isMsignExists", "isMsignExists", HashCode.IS_MOBILE_SIGN_EXISTS_HASH_CODE),
    LAST_UPDATE("urn:blitz:lastUpdate", "lastUpdate", HashCode.LAST_UPDATE_HASH_CODE),
    ERROR_CODE("urn:mace:dir:attribute:errorCode", "errorCode", HashCode.ERROR_CODE_HASH_CODE),
    ERROR_MESSAGE("urn:mace:dir:attribute:errorMsg", "errorMsg", HashCode.ERROR_MESSAGE_HASH_CODE),
    PORTAL_VERSION("urn:mace:dir:attribute:portalVersion", "portalVersion", HashCode.PORTAL_VERSION_HASH_CODE),
    PRINCIPAL_STATUS("urn:mace:dir:attribute:principalStatus", "principalStatus", HashCode.PRINCIPAL_STATUS_HASH_CODE);


    private static Map<String, AttributeEnum> lookupMap;

    static {
        lookupMap = new HashMap<String, AttributeEnum>(values().length);
        for (AttributeEnum element : values()) {
            lookupMap.put(element.sysname, element);
        }
    }

    public static AttributeEnum lookup(String sysname) {
        return lookupMap.get(sysname);
    }

    public static void printHashCodes() {
        for (AttributeEnum attrEnum : values()){
        System.out.println("    public static final int " + attrEnum.name() + "_HASH_CODE = " + attrEnum.getSysname().hashCode() + ";");
        }
    }

    private final String sysname;
    private final String sourceAttributeId;
    private final int hashCode;


    private AttributeEnum(String sysname, String sourceAttributeId, int hashCode) {
        this.sysname = sysname;
        this.hashCode = hashCode;
        this.sourceAttributeId = sourceAttributeId;
       if (hashCode != sysname.hashCode()) {
            throw new IllegalArgumentException("Bad attribute " + sysname + " hash code" + hashCode);
        }
    }

    public String getSysname() {
        return sysname;
    }

    public String getSourceAttributeId() {
        return sourceAttributeId;
    }

    public int getHashCode() {
        return hashCode;
    }

    public static class HashCode {
        public static final int AUTH_TOKEN_HASH_CODE = -413106613;
        public static final int DEVICE_TYPE_HASH_CODE = -1094077426;
        public static final int PERSON_TYPE_HASH_CODE = -1196582515;
        public static final int ID_TOKEN_HASH_CODE = 695384320;
        public static final int AUTHN_METHOD_HASH_CODE = 768157353;
        public static final int GLOBAL_ROLE_HASH_CODE = 1560878103;
        public static final int USER_ID_HASH_CODE = -1263989908;
        public static final int USER_NAME_HASH_CODE = -1292080684;
        public static final int USER_NAME_OLD_HASH_CODE = -1968019151;
        public static final int ASSURANCE_LEVEL_HASH_CODE = 1361370805;
        public static final int ORGANIZATION_ID_HASH_CODE = 1883012865;
        public static final int ORGANIZATION_TYPE_HASH_CODE = 1394483104;
        public static final int ORGANIZATION_NAME_HASH_CODE = 1394281201;
        public static final int PERSON_LASTNAME_HASH_CODE = -411340313;
        public static final int PERSON_FIRSTNAME_HASH_CODE = -1730858379;
        public static final int PERSON_MIDDLENAME_HASH_CODE = 1536806886;
        public static final int PERSON_SNILS_HASH_CODE = 1558404994;
        public static final int PERSON_SNILS_OLD_HASH_CODE = -874050161;
        public static final int PERSON_INN_HASH_CODE = -869895370;
        public static final int PERSON_INN_OLD_HASH_CODE = -63488893;
        public static final int PERSON_CITIZENSHIP_HASH_CODE = 380576753;
        public static final int PERSON_CONTACTS_HASH_CODE = -360033405;
        public static final int PERSON_EMAIL_HASH_CODE = 1545469897;
        public static final int PERSON_MOBILE_HASH_CODE = 1313084473;
        public static final int PERSON_DOCUMENTS_HASH_CODE = -1216428664;
        public static final int PERSON_ADDRESSES_HASH_CODE = -1285427598;
        public static final int PERSON_IN_ORG_HASH_CODE = -1546798841;
        public static final int ORGANIZATION_OGRN_HASH_CODE = 1394285146;
        public static final int ORGANIZATION_INN_HASH_CODE = -1756143933;
        public static final int ORGANIZATION_SHORT_NAME_HASH_CODE = 1417450625;
        public static final int ORGANIZATION_KPP_HASH_CODE = -1756141947;
        public static final int ORGANIZATION_BRANCH_KPP_HASH_CODE = -1636941309;
        public static final int ORGANIZATION_LEGAL_FORM_HASH_CODE = 1246862967;
        public static final int ORGANIZATION_NSI_ID_HASH_CODE = 1882121057;
        public static final int STAFF_UNIT_POSITION_HASH_CODE = -1496349041;
        public static final int ORGANIZATION_CONTACTS_HASH_CODE = 1483361721;
        public static final int ORGANIZATION_ADDRESSES_HASH_CODE = 25246460;
        public static final int PERSON_OGRN_HASH_CODE = -1196780473;
        public static final int SYSTEM_AUTHORITIES_HASH_CODE = -740837578;
        public static final int AUTHN_CERTIFICATE_HASH_CODE = -1317910321;
        public static final int ENTERPRISE_ROLE_HASH_CODE = -1885643307;
        public static final int IS_MOBILE_SIGN_EXISTS_HASH_CODE = 1231774462;
        public static final int LAST_UPDATE_HASH_CODE = 117611613;
        public static final int ERROR_CODE_HASH_CODE = -1534658257;
        public static final int ERROR_MESSAGE_HASH_CODE = -1850610689;
        public static final int PORTAL_VERSION_HASH_CODE = 966739686;
        public static final int PRINCIPAL_STATUS_HASH_CODE = -1397924006;
    }
}
