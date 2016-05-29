package com.angkorteam.mbaas.server;

/**
 * Created by socheat on 5/22/16.
 */
public class Jdbc {

    public static final String USER = "user";
    public static final String MOBILE = "mobile";
    public static final String ROLE = "role";
    public static final String COLLECTION = "collection";
    public static final String ATTRIBUTE = "attribute";
    public static final String AUTHORIZATION = "authorization";
    public static final String QUERY = "query";
    public static final String QUERY_PARAMETER = "query_parameter";
    public static final String FILE = "file";
    public static final String ASSET = "asset";
    public static final String JAVASCRIPT = "javascript";
    public static final String JOB = "job";
    public static final String CLIENT = "client";
    public static final String VISIBLE_BY_FRIEND = "visible_by_friend";
    public static final String VISIBLE_BY_ANONYMOUS = "visible_by_anonymous";
    public static final String VISIBLE_BY_REGISTERED_USER = "visible_by_registered_user";
    public static final String FRIEND = "friend";
    public static final String PARTICIPANT = "participant";
    public static final String CONVERSATION = "conversation";
    public static final String MESSAGE = "message";
    public static final String EAV_DATE_TIME = "eav_date_time";
    public static final String EAV_DECIMAL = "eav_decimal";
    public static final String EAV_INTEGER = "eav_integer";
    public static final String EAV_TEXT = "eav_text";
    public static final String EAV_VARCHAR = "eav_varchar";
    public static final String EAV_BOOLEAN = "eav_boolean";
    public static final String EAV_CHARACTER = "eav_character";
    public static final String EAV_DATE = "eav_date";
    public static final String EAV_TIME = "eav_time";
    public static final String PAGE = "page";
    public static final String MENU = "menu";

    public static final class User {
        public static final String USER_ID = "user_id";
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
        public static final String FULL_NAME = "full_name";
        public static final String ROLE_ID = Role.ROLE_ID;
        public static final String SYSTEM = "system";
        public static final String AUTHENTICATION = "authentication";
        public static final String MOBILE_NUMBER = "mobile_number";
        public static final String EMAIL_ADDRESS = "email_address";
        public static final String TOTP_SECRET = "totp_secret";
        public static final String TOTP_HASH = "totp_hash";
        public static final String TOTP_STATUS = "totp_status";
        public static final String ACCOUNT_NON_EXPIRED = "account_non_expired";
        public static final String ACCOUNT_NON_LOCKED = "account_non_locked";
        public static final String CREDENTIALS_NON_EXPIRED = "credentials_non_expired";
        public static final String STATUS = "status";
        public static final String PASSWORD_RESET_TOKEN = "password_reset_token";
        public static final String PASSWORD_RESET_TOKEN_EXPIRED_DATE = "password_reset_token_expired_date";
    }

    public static final class Role {
        public static final String ROLE_ID = "role_id";
        public static final String SYSTEM = "system";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
    }

    public static final class Mobile {
        public static final String MOBILE_ID = "mobile_id";
        public static final String USER_ID = User.USER_ID;
        public static final String CLIENT_ID = "client_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String DATE_CREATED = "date_created";
        public static final String DATE_SEEN = "date_seen";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String ACCESS_TOKEN_ISSUED_DATE = "access_token_issued_date";
        public static final String TIME_TO_LIVE = "time_to_live";
        public static final String GRANT_TYPE = "grant_type";
        public static final String USER_AGENT = "user_agent";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String DEVICE_TYPE = "device_type";
        public static final String DEVICE_ALIAS = "device_alias";
        public static final String DEVICE_OPERATING_SYSTEM = "device_operating_system";
        public static final String DEVICE_OS_VERSION = "device_os_version";
        public static final String CLIENT_IP = "client_ip";
    }

    public static final class Collection {
        public static final String COLLECTION_ID = "collection_id";
        public static final String OWNER_APPLICATION_USER_ID = "owner_application_user_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String LOCKED = "locked";
        public static final String SYSTEM = "system";
    }

    public static final class Attribute {
        public static final String ATTRIBUTE_ID = "attribute_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = "collection_id";
        public static final String NAME = "name";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String VISIBILITY = "visibility";
        public static final String DATE_CREATED = "date_created";
        public static final String EAV = "eav";
        public static final String SYSTEM = "system";
        public static final String EXTRA = "extra";
    }

    public static final class Authorization {
        public static final String AUTHORIZATION_ID = "authorization_id";
        public static final String DATE_CREATED = "date_created";
        public static final String TIME_TO_LIVE = "time_to_live";
        public static final String APPLICATION_CODE = "application_code";
        public static final String CLIENT_ID = "client_id";
        public static final String STATE = "state";
        public static final String USER_ID = User.USER_ID;
    }

    public static final class Query {
        public static final String QUERY_ID = "query_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String SCRIPT = "script";
        public static final String RETURN_TYPE = "return_type";
        public static final String RETURN_SUB_TYPE = "return_sub_type";
        public static final String DATE_CREATED = "date_created";
        public static final String SECURITY = "security";
        public static final String USER_ID = User.USER_ID;
        public static final String APPLICATION_CODE = "application_code";
    }

    public static final class QueryParameter {
        public static final String QUERY_PARAMETER_ID = "query_parameter_id";
        public static final String QUERY_ID = "query_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String SUB_TYPE = "sub_type";
    }

    public static final class File {
        public static final String FILE_ID = "file_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String LABEL = "label";
        public static final String PATH = "path";
        public static final String MIME = "mime";
        public static final String EXTENSION = "extension";
        public static final String LENGTH = "length";
        public static final String DATE_CREATED = "date_created";
        public static final String USER_ID = User.USER_ID;
        public static final String CLIENT_ID = "client_id";
    }

    public static final class Asset {
        public static final String ASSET_ID = "asset_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String LABEL = "label";
        public static final String PATH = "path";
        public static final String MIME = "mime";
        public static final String EXTENSION = "extension";
        public static final String LENGTH = "`length`";
        public static final String DATE_CREATED = "date_created";
        public static final String USER_ID = User.USER_ID;
        public static final String CLIENT_ID = "client_id";
    }

    public static final class Javascript {
        public static final String JAVASCRIPT_ID = "javascript_id";
        public static final String PATH = "path";
        public static final String DESCRIPTION = "description";
        public static final String SCRIPT = "script";
        public static final String DATE_CREATED = "date_created";
        public static final String SECURITY = "security";
        public static final String USER_ID = User.USER_ID;
        public static final String APPLICATION_CODE = "application_code";
    }

    public static final class Job {
        public static final String JOB_ID = "job_id";
        public static final String NAME = "name";
        public static final String CRON = "cron";
        public static final String USER_ID = User.USER_ID;
        public static final String CONSUME = "consume";
        public static final String JAVASCRIPT = "javascript";
        public static final String ERROR_MESSAGE = "error_message";
        public static final String ERROR_CLASS = "error_class";
        public static final String DATE_LAST_EXECUTED = "date_last_executed";
        public static final String SECURITY = "security";
        public static final String DATE_CREATED = "date_created";
        public static final String APPLICATION_CODE = "application_code";
    }

    public static final class Client {
        public static final String CLIENT_ID = "client_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String USER_ID = User.USER_ID;
        public static final String CLIENT_SECRET = "client_secret";
        public static final String DATE_CREATED = "date_created";
        public static final String PUSH_VARIANT_ID = "push_variant_id";
        public static final String PUSH_SECRET = "push_secret";
        public static final String PUSH_GCM_SENDER_ID = "push_gcm_sender_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String SECURITY = "security";
    }

    public static final class VisibleByFriend {
        public static final String VISIBLE_BY_FRIEND_ID = "visible_by_friend_id";
        public static final String USER_ID = User.USER_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
    }

    public static final class VisibleByAnonymous {
        public static final String VISIBLE_BY_ANONYMOUS_ID = "visible_by_anonymous_id";
        public static final String USER_ID = User.USER_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
    }

    public static final class VisibleByRegisteredUser {
        public static final String VISIBLE_BY_REGISTERED_USER_ID = "visible_by_registered_user_id";
        public static final String USER_ID = User.USER_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
    }

    public static final class Friend {
        public static final String FRIEND_ID = "friend_id";
        public static final String USER_ID = User.USER_ID;
        public static final String FRIEND_USER_ID = "friend_" + User.USER_ID;
        public static final String SUBSCRIPTION = "subscription";
        public static final String ASK_SUBSCRIPTION = "ask_subscription";
        public static final String DATE_CREATED = "date_created";
    }

    public static final class Participant {
        public static final String PARTICIPANT_ID = "participant_id";
        public static final String CONVERSATION_ID = Conversation.CONVERSATION_ID;
        public static final String USER_ID = User.USER_ID;
        public static final String DATE_CREATED = "date_created";
    }

    public static final class Conversation {
        public static final String CONVERSATION_ID = "conversation_id";
        public static final String NAME = "name";
        public static final String DATE_CREATED = "date_created";
    }

    public static final class Message {
        public static final String MESSAGE_ID = "message_id";
        public static final String CONVERSATION_ID = Conversation.CONVERSATION_ID;
        public static final String SENDER_USER_ID = "sender_" + User.USER_ID;
        public static final String RECEIVER_USER_ID = "receiver_" + User.USER_ID;
        public static final String BODY = "body";
        public static final String READ = "`read`";
        public static final String DATE_CREATED = "date_created";
        public static final String DATE_READ = "date_read";
    }

    public static final class EavCharacter {
        public static final String EAV_CHARACTER_ID = "eav_character_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavDecimal {
        public static final String EAV_DECIMAL_ID = "eav_decimal_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavInteger {
        public static final String EAV_INTEGER_ID = "eav_integer_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavText {
        public static final String EAV_TEXT_ID = "eav_text_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavVarchar {
        public static final String EAV_VARCHAR_ID = "eav_varchar_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavBoolean {
        public static final String EAV_BOOLEAN_ID = "eav_boolean_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavDateTime {
        public static final String EAV_DATE_TIME_ID = "eav_date_time_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavDate {
        public static final String EAV_DATE_ID = "eav_date_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavTime {
        public static final String EAV_TIME_ID = "eav_time_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class Page {
        public static final String PAGE_ID = "page_id";
        public static final String USER_ID = "user_id";
        public static final String SECURITY = "security";
        public static final String MENU_ID = "menu_id";
        public static final String HTML = "html";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String JAVASCRIPT = "javascript";
        public static final String DATE_CREATED = "date_created";
        public static final String DATE_MODIFIED = "date_modified";
    }

    public static final class Menu {
        public static final String MENU_ID = "menu_id";
        public static final String TITLE = "title";
        public static final String USER_ID = "user_id";
        public static final String PARENT_MENU_ID = "parent_menu_id";
        public static final String DATE_CREATED = "date_created";
    }
}
