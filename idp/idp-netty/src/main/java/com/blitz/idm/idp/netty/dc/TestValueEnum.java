package com.blitz.idm.idp.netty.dc;


import java.util.HashMap;
import java.util.Map;

/**
 * User: agumerov
 * Date: 14.12.12
 */
public enum TestValueEnum {
    /* PRINCIPAL INFO */
    //old attribute
    AUTH_TOKEN(AttributeEnum.AUTH_TOKEN, null),    //legacy attribute
    DEVICE_TYPE(AttributeEnum.DEVICE_TYPE, null),
    PERSON_TYPE(AttributeEnum.PERSON_TYPE, "R"),

    ID_TOKEN(AttributeEnum.ID_TOKEN, null),
    AUTHN_METHOD(AttributeEnum.AUTHN_METHOD, "PWD"),
    GLOBAL_ROLE(AttributeEnum.GLOBAL_ROLE, "E"),
    USER_ID(AttributeEnum.USER_ID, "243280312"),
    USER_NAME(AttributeEnum.USER_NAME, "000-000-001 26"),
    ASSURANCE_LEVEL(AttributeEnum.ASSURANCE_LEVEL, "30"),

    /* PRINCIPAL ORGANIZATION INFO */
    ORGANIZATION_ID(AttributeEnum.ORGANIZATION_ID, "1000000000"),
    ORGANIZATION_TYPE(AttributeEnum.ORGANIZATION_TYPE, "A"),
    ORGANIZATION_NAME(AttributeEnum.ORGANIZATION_NAME, "Test organization"),

    /* PERSON FULL INFO , PERSON_WITH_CONTACTS, PERSON_SHORT_INFO */
    PERSON_LASTNAME(AttributeEnum.PERSON_LASTNAME, "Иванов"),
    PERSON_FIRSTNAME(AttributeEnum.PERSON_FIRSTNAME, "Иван"),
    PERSON_MIDDLENAME(AttributeEnum.PERSON_MIDDLENAME, "Иванович"),
    PERSON_SNILS(AttributeEnum.PERSON_SNILS, "000-000-001 26"),
    PERSON_INN(AttributeEnum.PERSON_INN, "123456789012"),
    PERSON_CITIZENSHIP(AttributeEnum.PERSON_CITIZENSHIP, "RUS"),//not used yet

    /* PERSON FULL INFO , PERSON_WITH_CONTACTS */
    PERSON_CONTACTS(AttributeEnum.PERSON_CONTACTS, null),//not used yet
    PERSON_EMAIL(AttributeEnum.PERSON_EMAIL, "test@mail.ru"), //not used yet
    PERSON_MOBILE(AttributeEnum.PERSON_MOBILE, "+7(234)1234567"), //not used yet

    /* PERSON FULL INFO */
    PERSON_DOCUMENTS(AttributeEnum.PERSON_DOCUMENTS, null),//not used yet
    PERSON_ADDRESSES(AttributeEnum.PERSON_ADDRESSES, null), //not used yet

    /* PERSON FULL INFO */
    PERSON_IN_ORG(AttributeEnum.PERSON_IN_ORG, null), //not used yet

    /* ORGANIZATION SHORT INFO */
    ORGANIZATION_OGRN(AttributeEnum.ORGANIZATION_OGRN, "1234567890"),
    ORGANIZATION_INN(AttributeEnum.ORGANIZATION_INN, "123456789012"),
    ORGANIZATION_SHORT_NAME(AttributeEnum.ORGANIZATION_SHORT_NAME, "Test org"),//not used yet
    ORGANIZATION_KPP(AttributeEnum.ORGANIZATION_KPP, "0987654"),
    ORGANIZATION_BRANCH_KPP(AttributeEnum.ORGANIZATION_BRANCH_KPP, null), //not used yet
    ORGANIZATION_LEGAL_FORM(AttributeEnum.ORGANIZATION_LEGAL_FORM, null),
    ORGANIZATION_NSI_ID(AttributeEnum.ORGANIZATION_NSI_ID, null),//not used yet


    /* ORGANIZATION FULL INFO */
    STAFF_UNIT_POSITION(AttributeEnum.STAFF_UNIT_POSITION, "Senior constructor"),
    ORGANIZATION_CONTACTS(AttributeEnum.ORGANIZATION_CONTACTS, null),
    ORGANIZATION_ADDRESSES(AttributeEnum.ORGANIZATION_ADDRESSES, null),

    /* BUSINESS PERSON INFO */
    PERSON_OGRN(AttributeEnum.PERSON_OGRN, null),

    /* AUTHORITY INFO */
    SYSTEM_AUTHORITIES(AttributeEnum.SYSTEM_AUTHORITIES, null);

    private static Map<AttributeEnum, TestValueEnum> lookupMap;

    static {
        lookupMap = new HashMap<AttributeEnum, TestValueEnum>(values().length);
        for (TestValueEnum element : values()) {
            lookupMap.put(element.getKey(), element);
        }
    }

    public static TestValueEnum lookup(AttributeEnum key) {
        return lookupMap.get(key);
    }

    private final AttributeEnum key;
    private final String value;

    private TestValueEnum(AttributeEnum key, String value) {
        this.key = key;
        this.value = value;
    }

    public AttributeEnum getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


}
