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
    public static final String MASTER_PAGE = "master_page";
    public static final String PAGE_ROLE = "page_role";
    public static final String BLOCK = "block";
    public static final String RESTORE = "restore";
    public static final String SOCKET = "socket";
    public static final String ENUM = "enum";
    public static final String ENUM_ITEM = "enum_item";
    public static final String JSON = "json";
    public static final String JSON_FIELD = "json_field";
    public static final String HTTP_HEADER = "http_header";
    public static final String HTTP_QUERY = "http_query";
    public static final String REST = "rest";
    public static final String REST_REQUEST_HEADER = "rest_request_header";
    public static final String REST_REQUEST_QUERY = "rest_request_query";
    public static final String REST_RESPONSE_HEADER = "rest_response_header";

    public static final class User {
        public static final String USER_ID = USER + "_id";
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
        public static final String ROLE_ID = ROLE + "_id";
        public static final String SYSTEM = "system";
        public static final String NAME = "name";
        public static final String HOME_PAGE_ID = "home_" + Page.PAGE_ID;
        public static final String DESCRIPTION = "description";
    }

    public static final class Mobile {
        public static final String MOBILE_ID = MOBILE + "_id";
        public static final String USER_ID = User.USER_ID;
        public static final String CLIENT_ID = Client.CLIENT_ID;
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
        public static final String COLLECTION_ID = COLLECTION + "_id";
        public static final String OWNER_USER_ID = "owner_" + User.USER_ID;
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String LOCKED = "locked";
        public static final String SYSTEM = "system";
    }

    public static final class Attribute {
        public static final String ATTRIBUTE_ID = ATTRIBUTE + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String NAME = "name";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String VISIBILITY = "visibility";
        public static final String DATE_CREATED = "date_created";
        public static final String EAV = "eav";
        public static final String SYSTEM = "system";
        public static final String EXTRA = "extra";
    }

    public static final class Authorization {
        public static final String AUTHORIZATION_ID = AUTHORIZATION + "_id";
        public static final String DATE_CREATED = "date_created";
        public static final String TIME_TO_LIVE = "time_to_live";
        public static final String APPLICATION_CODE = "application_code";
        public static final String CLIENT_ID = Client.CLIENT_ID;
        public static final String STATE = "state";
        public static final String USER_ID = User.USER_ID;
    }

    public static final class Query {
        public static final String QUERY_ID = QUERY + "_id";
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
        public static final String QUERY_PARAMETER_ID = QUERY_PARAMETER + "_id";
        public static final String QUERY_ID = Query.QUERY_ID;
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String SUB_TYPE = "sub_type";
    }

    public static final class File {
        public static final String FILE_ID = FILE + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String LABEL = "label";
        public static final String PATH = "path";
        public static final String MIME = "mime";
        public static final String EXTENSION = "extension";
        public static final String LENGTH = "length";
        public static final String DATE_CREATED = "date_created";
        public static final String USER_ID = User.USER_ID;
        public static final String CLIENT_ID = Client.CLIENT_ID;
    }

    public static final class Asset {
        public static final String ASSET_ID = ASSET + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String NAME = "name";
        public static final String LABEL = "label";
        public static final String PATH = "path";
        public static final String MIME = "mime";
        public static final String EXTENSION = "extension";
        public static final String LENGTH = "length";
        public static final String DATE_CREATED = "date_created";
        public static final String USER_ID = User.USER_ID;
        public static final String CLIENT_ID = Client.CLIENT_ID;
    }

    public static final class Javascript {
        public static final String JAVASCRIPT_ID = JAVASCRIPT + "_id";
        public static final String PATH = "path";
        public static final String DESCRIPTION = "description";
        public static final String SCRIPT = "script";
        public static final String DATE_CREATED = "date_created";
        public static final String SECURITY = "security";
        public static final String USER_ID = User.USER_ID;
        public static final String APPLICATION_CODE = "application_code";
    }

    public static final class Job {
        public static final String JOB_ID = JOB + "_id";
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
        public static final String CLIENT_ID = CLIENT + "_id";
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
        public static final String VISIBLE_BY_FRIEND_ID = VISIBLE_BY_FRIEND + "_id";
        public static final String USER_ID = User.USER_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
    }

    public static final class VisibleByAnonymous {
        public static final String VISIBLE_BY_ANONYMOUS_ID = VISIBLE_BY_ANONYMOUS + "_id";
        public static final String USER_ID = User.USER_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
    }

    public static final class VisibleByRegisteredUser {
        public static final String VISIBLE_BY_REGISTERED_USER_ID = VISIBLE_BY_REGISTERED_USER + "_id";
        public static final String USER_ID = User.USER_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
    }

    public static final class Friend {
        public static final String FRIEND_ID = FRIEND + "_id";
        public static final String USER_ID = User.USER_ID;
        public static final String FRIEND_USER_ID = "friend_" + User.USER_ID;
        public static final String SUBSCRIPTION = "subscription";
        public static final String ASK_SUBSCRIPTION = "ask_subscription";
        public static final String DATE_CREATED = "date_created";
    }

    public static final class Participant {
        public static final String PARTICIPANT_ID = PARTICIPANT + "_id";
        public static final String CONVERSATION_ID = Conversation.CONVERSATION_ID;
        public static final String USER_ID = User.USER_ID;
        public static final String DATE_CREATED = "date_created";
    }

    public static final class Conversation {
        public static final String CONVERSATION_ID = CONVERSATION + "_id";
        public static final String NAME = "name";
        public static final String DATE_CREATED = "date_created";
    }

    public static final class Message {
        public static final String MESSAGE_ID = MESSAGE + "_id";
        public static final String CONVERSATION_ID = Conversation.CONVERSATION_ID;
        public static final String SENDER_USER_ID = "sender_" + User.USER_ID;
        public static final String RECEIVER_USER_ID = "receiver_" + User.USER_ID;
        public static final String BODY = "body";
        public static final String READ = "`read`";
        public static final String DATE_CREATED = "date_created";
        public static final String DATE_READ = "date_read";
    }

    public static final class EavCharacter {
        public static final String EAV_CHARACTER_ID = EAV_CHARACTER + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavDecimal {
        public static final String EAV_DECIMAL_ID = EAV_DECIMAL + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavInteger {
        public static final String EAV_INTEGER_ID = EAV_INTEGER + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavText {
        public static final String EAV_TEXT_ID = EAV_TEXT + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavVarchar {
        public static final String EAV_VARCHAR_ID = EAV_VARCHAR + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavBoolean {
        public static final String EAV_BOOLEAN_ID = EAV_BOOLEAN + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavDateTime {
        public static final String EAV_DATE_TIME_ID = EAV_DATE_TIME + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavDate {
        public static final String EAV_DATE_ID = EAV_DATE + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class EavTime {
        public static final String EAV_TIME_ID = EAV_TIME + "_id";
        public static final String APPLICATION_CODE = "application_code";
        public static final String COLLECTION_ID = Collection.COLLECTION_ID;
        public static final String ATTRIBUTE_ID = Attribute.ATTRIBUTE_ID;
        public static final String DOCUMENT_ID = "document_id";
        public static final String ATTRIBUTE_TYPE = "attribute_type";
        public static final String EAV_VALUE = "eav_value";
    }

    public static final class Page {
        public static final String PAGE_ID = PAGE + "_id";
        public static final String MASTER_PAGE_ID = MasterPage.MASTER_PAGE_ID;
        public static final String USER_ID = User.USER_ID;
        public static final String MENU_ID = Menu.MENU_ID;
        public static final String TITLE = "title";
        public static final String CODE = "code";
        public static final String DESCRIPTION = "description";
        public static final String HTML = "html";
        public static final String JAVASCRIPT = "javascript";
        public static final String STAGE_HTML = "stage_html";
        public static final String STAGE_JAVASCRIPT = "stage_javascript";
        public static final String MODIFIED = "modified";
        public static final String DATE_CREATED = "date_created";
        public static final String DATE_MODIFIED = "date_modified";
    }

    public static final class Menu {
        public static final String MENU_ID = MENU + "_id";
        public static final String TITLE = "title";
        public static final String USER_ID = User.USER_ID;
        public static final String PARENT_MENU_ID = "parent_" + MENU_ID;
        public static final String DATE_CREATED = "date_created";
    }

    public static final class PageRole {
        public static final String PAGE_ROLE_ID = PAGE_ROLE + "_id";
        public static final String PAGE_ID = Page.PAGE_ID;
        public static final String ROLE_ID = Role.ROLE_ID;
    }

    public static final class MasterPage {
        public static final String MASTER_PAGE_ID = MASTER_PAGE + "_id";
        public static final String TITLE = "title";
        public static final String USER_ID = User.USER_ID;
        public static final String DESCRIPTION = "description";
        public static final String JAVASCRIPT = "javascript";
        public static final String HTML = "html";
        public static final String CODE = "code";
        public static final String STAGE_JAVASCRIPT = "stage_javascript";
        public static final String STAGE_HTML = "stage_html";
        public static final String MODIFIED = "modified";
        public static final String DATE_CREATED = "date_created";
        public static final String DATE_MODIFIED = "date_modified";
    }

    public static final class Block {
        public static final String BLOCK_ID = BLOCK + "_id";
        public static final String USER_ID = User.USER_ID;
        public static final String TITLE = "title";
        public static final String CODE = "code";
        public static final String DESCRIPTION = "description";
        public static final String HTML = "html";
        public static final String JAVASCRIPT = "javascript";
        public static final String STAGE_HTML = "stage_html";
        public static final String STAGE_JAVASCRIPT = "stage_javascript";
        public static final String MODIFIED = "modified";
        public static final String DATE_CREATED = "date_created";
        public static final String DATE_MODIFIED = "date_modified";
    }

    public static final class Restore {
        public static final String RESTORE_ID = RESTORE + "_id";
        public static final String FIELDS = "fields";
        public static final String TABLE_NAME = "table_name";
        public static final String DATE_CREATED = "date_created";
    }

    public static final class Socket {
        public static final String SOCKET_ID = SOCKET + "_id";
        public static final String USER_ID = User.USER_ID;
        public static final String DATE_CREATED = "date_created";
        public static final String SESSION_ID = "session_id";
        public static final String RESOURCE_NAME = "resource_name";
        public static final String PAGE_KEY = "page_key";
    }

    public static final class Enum {
        public static final String ENUM_ID = ENUM + "_id";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String FORMAT = "format";
        public static final String DESCRIPTION = "description";
    }

    public static final class EnumItem {
        public static final String ENUM_ITEM_ID = ENUM_ITEM + "_id";
        public static final String ENUM_ID = Enum.ENUM_ID;
        public static final String VALUE = "value";
    }

    public static final class Json {
        public static final String JSON_ID = JSON + "_id";
        public static final String NAME = "name";
        public static final String CONTENT_TYPE = "content_type";
        public static final String DESCRIPTION = "description";
    }

    public static final class JsonField {
        public static final String JSON_FIELD_ID = JSON_FIELD + "_id";
        public static final String JSON_ID = Json.JSON_ID;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String TYPE = "type";
        public static final String REQUIRED = "required";
        public static final String SUB_TYPE = "sub_type";
        public static final String MAP_JSON_ID = "map_" + Json.JSON_ID;
        public static final String ENUM_ID = Enum.ENUM_ID;
    }

    public static final class HttpHeader {
        public static final String HTTP_HEADER_ID = HTTP_HEADER + "_id";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String SUB_TYPE = "sub_type";
        public static final String ENUM_ID = Enum.ENUM_ID;
        public static final String DESCRIPTION = "description";
        public static final String FORMAT = "format";
    }

    public static final class HttpQuery {
        public static final String HTTP_QUERY_ID = HTTP_QUERY + "_id";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String SUB_TYPE = "sub_type";
        public static final String ENUM_ID = Enum.ENUM_ID;
        public static final String DESCRIPTION = "description";
        public static final String FORMAT = "format";
    }

    public static final class Rest {
        public static final String REST_ID = REST + "_id";
        public static final String METHOD = "method";
        public static final String PATH = "path";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String SCRIPT = "script";
        public static final String STAGE_SCRIPT = "stage_script";
        public static final String MODIFIED = "modified";
        public static final String DATE_CREATED = "date_created";
        public static final String SECURITY = "security";
        public static final String REQUEST_CONTENT_TYPE = "request_content_type";
        public static final String REQUEST_BODY_REQUIRED = "request_body_required";
        public static final String REQUEST_BODY_TYPE = "request_body_type";
        public static final String REQUEST_BODY_SUB_TYPE = "request_body_sub_type";
        public static final String REQUEST_BODY_ENUM_ID = "request_body_" + Enum.ENUM_ID;
        public static final String REQUEST_BODY_MAP_JSON_ID = "request_body_map_" + Json.JSON_ID;
        public static final String RESPONSE_CONTENT_TYPE = "response_content_type";
        public static final String RESPONSE_BODY_REQUIRED = "response_body_required";
        public static final String RESPONSE_BODY_TYPE = "response_body_type";
        public static final String RESPONSE_BODY_SUB_TYPE = "response_body_sub_type";
        public static final String RESPONSE_BODY_ENUM_ID = "response_body_" + Enum.ENUM_ID;
        public static final String RESPONSE_BODY_MAP_JSON_ID = "response_body_map_" + Json.JSON_ID;
    }

    public static final class RestRequestHeader {
        public static final String REST_REQUEST_HEADER_ID = REST_REQUEST_HEADER + "_id";
        public static final String REST_ID = Rest.REST_ID;
        public static final String HTTP_HEADER_ID = HttpHeader.HTTP_HEADER_ID;
        public static final String REQUIRED = "required";
    }

    public static final class RestRequestQuery {
        public static final String REST_REQUEST_QUERY_ID = REST_REQUEST_QUERY + "_id";
        public static final String REST_ID = Rest.REST_ID;
        public static final String HTTP_QUERY_ID = HttpQuery.HTTP_QUERY_ID;
        public static final String REQUIRED = "required";
    }

    public static final class RestResponseHeader {
        public static final String REST_RESPONSE_HEADER_ID = REST_RESPONSE_HEADER + "_id";
        public static final String REST_ID = Rest.REST_ID;
        public static final String HTTP_HEADER_ID = HttpHeader.HTTP_HEADER_ID;
        public static final String REQUIRED = "required ";
    }

}
