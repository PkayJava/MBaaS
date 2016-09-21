package com.angkorteam.mbaas.server.wicket;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by socheat on 9/3/16.
 */
public class MultipartFormData {
    public static void main(String[] args) throws UnirestException {
        HttpRequestWithBody requestBody = Unirest.post("http://demo.ddns.net:9080/api/javascript/hello");
        requestBody = requestBody.header("user-agent", "hello");
        requestBody = requestBody.header("authorization", "hello");
        requestBody = requestBody.header("dob", DateFormatUtils.ISO_DATE_FORMAT.format(new Date()));
        HttpRequest httpRequest = requestBody.queryString("game", Arrays.asList(DateFormatUtils.ISO_DATE_FORMAT.format(new Date()), DateFormatUtils.ISO_DATE_FORMAT.format(new Date())));
        httpRequest.queryString("sessionId", UUID.randomUUID().toString());
        MultipartBody body = null;
        {
            body = requestBody.field("string_required", "no error anymore");
        }
        {
            body = body.field("string_optional", "no error anymore");
        }
        {
            body.field("file_optional", new File("/home/socheat/Documents/country.txt"));
        }
        {
            body.field("file_required", new File("/home/socheat/Documents/country.txt"));
        }
        {
            body.field("list_file_required", new File("/home/socheat/Documents/country.txt"));
        }
        {
            body.field("list_file_optional", new File("/home/socheat/Documents/country.txt"));
        }
        {
            List<String> list_datetime_required = new ArrayList<>();
            list_datetime_required.add(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
            list_datetime_required.add(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
            list_datetime_required.add(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
            body = body.field("list_datetime_required", list_datetime_required);
        }
        {
            List<String> list_datetime_optional = new ArrayList<>();
            list_datetime_optional.add(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
            body = body.field("list_datetime_optional", list_datetime_optional);
        }
        {
            List<String> boolean_enum_required = new ArrayList<>();
            boolean_enum_required.add("true");
            body = body.field("boolean_enum_required", boolean_enum_required);
        }
        {
            List<String> string_enum_required = new ArrayList<>();
            string_enum_required.add("people");
            body = body.field("string_enum_required", string_enum_required);
        }
        {
            List<String> string_enum_optional = new ArrayList<>();
            string_enum_optional.add("people");
            body = body.field("string_enum_optional", string_enum_optional);
        }
        {
            List<String> boolean_enum_optional = new ArrayList<>();
            boolean_enum_optional.add("false");
            body = body.field("boolean_enum_optional", boolean_enum_optional);
        }
        {
            List<String> list_string_enum_required = new ArrayList<>();
            list_string_enum_required.add("people");
            body = body.field("list_string_enum_required", list_string_enum_required);
        }
        {
            List<String> list_string_enum_optional = new ArrayList<>();
            list_string_enum_optional.add("people");
            list_string_enum_optional.add("student");
            body = body.field("list_string_enum_optional", list_string_enum_optional);
        }
        {
            List<String> datetime_enum_required = new ArrayList<>();
            datetime_enum_required.add("2016-09-02T01:00:00+07:00");
            body = body.field("datetime_enum_required", datetime_enum_required);
        }
        {
            List<String> datetime_enum_optional = new ArrayList<>();
            datetime_enum_optional.add("2016-09-02T01:00:00+07:00");
            body = body.field("datetime_enum_optional", datetime_enum_optional);
        }
        {
            List<String> list_long_enum_required = new ArrayList<>();
            list_long_enum_required.add("1");
            list_long_enum_required.add("2");
            body = body.field("list_long_enum_required", list_long_enum_required);
        }
        {
            List<String> list_long_enum_optional = new ArrayList<>();
            list_long_enum_optional.add("1");
            list_long_enum_optional.add("2");
            body = body.field("list_long_enum_optional", list_long_enum_optional);
        }
        {
            List<String> double_required = new ArrayList<>();
            double_required.add("1.1");
            body = body.field("double_required", double_required);
        }
        {
            List<String> double_optional = new ArrayList<>();
            double_optional.add("1.1");
            body = body.field("double_optional", double_optional);
        }
        {
            List<String> time_enum_required = new ArrayList<>();
            time_enum_required.add("01:00:00");
            body = body.field("time_enum_required", time_enum_required);
        }
        {
            List<String> time_enum_optional = new ArrayList<>();
            time_enum_optional.add("01:00:00");
            body = body.field("time_enum_optional", time_enum_optional);
        }
        {
            List<String> long_enum_required = new ArrayList<>();
            long_enum_required.add("1");
            body = body.field("long_enum_required", long_enum_required);
        }
        {
            List<String> long_enum_optional = new ArrayList<>();
            long_enum_optional.add("1");
            body = body.field("long_enum_optional", long_enum_optional);
        }
        {
            List<String> list_date_enum_required = new ArrayList<>();
            list_date_enum_required.add("2016-09-02");
            list_date_enum_required.add("2016-09-03");
            list_date_enum_required.add("2016-09-04");
            body = body.field("list_date_enum_required", list_date_enum_required);
        }
        {
            List<String> list_date_enum_optional = new ArrayList<>();
            list_date_enum_optional.add("2016-09-02");
            list_date_enum_optional.add("2016-09-03");
            list_date_enum_optional.add("2016-09-04");
            body = body.field("list_date_enum_optional", list_date_enum_optional);
        }
        {
            List<String> list_long_required = new ArrayList<>();
            list_long_required.add("1");
            list_long_required.add("2");
            list_long_required.add("-2");
            body = body.field("list_long_required", list_long_required);
        }
        {
            List<String> list_date_required = new ArrayList<>();
            list_date_required.add("2016-12-30");
            body = body.field("list_date_required", list_date_required);
        }
        {
            List<String> list_date_optional = new ArrayList<>();
            list_date_optional.add("2016-12-30");
            body = body.field("list_date_optional", list_date_optional);
        }
        {
            List<String> list_long_optional = new ArrayList<>();
            list_long_optional.add("1");
            list_long_optional.add("2");
            list_long_optional.add("-2");
            body = body.field("list_long_optional", list_long_optional);
        }
        {
            List<String> list_time_required = new ArrayList<>();
            list_time_required.add("01:30:30");
            list_time_required.add("01:30:31");
            body = body.field("list_time_required", list_time_required);
        }
        {
            List<String> list_time_optional = new ArrayList<>();
            list_time_optional.add("01:30:30");
            list_time_optional.add("01:30:31");
            body = body.field("list_time_optional", list_time_optional);
        }
        {
            List<String> list_double_enum_required = new ArrayList<>();
            list_double_enum_required.add("1");
            list_double_enum_required.add("1.5");
            body = body.field("list_double_enum_required", list_double_enum_required);
        }
        {
            List<String> list_double_enum_optional = new ArrayList<>();
            list_double_enum_optional.add("1");
            list_double_enum_optional.add("1.5");
            body = body.field("list_double_enum_optional", list_double_enum_optional);
        }
        {
            List<String> list_boolean_required = new ArrayList<>();
            list_boolean_required.add("true");
            list_boolean_required.add("false");
            body = body.field("list_boolean_required", list_boolean_required);
        }
        {
            List<String> list_boolean_optional = new ArrayList<>();
            list_boolean_optional.add("true");
            list_boolean_optional.add("false");
            body = body.field("list_boolean_optional", list_boolean_optional);
        }
        {
            List<String> date_enum_required = new ArrayList<>();
            date_enum_required.add("2016-09-02");
            body = body.field("date_enum_required", date_enum_required);
        }
        {
            List<String> date_enum_optional = new ArrayList<>();
            date_enum_optional.add("2016-09-02");
            body = body.field("date_enum_optional", date_enum_optional);
        }
        {
            List<String> double_enum_required = new ArrayList<>();
            double_enum_required.add("2.5");
            body = body.field("double_enum_required", double_enum_required);
        }
        {
            List<String> double_enum_optional = new ArrayList<>();
            double_enum_optional.add("2.5");
            body = body.field("double_enum_optional", double_enum_optional);
        }
        {
            List<String> list_string_required = new ArrayList<>();
            list_string_required.add("People");
            body = body.field("list_string_required", list_string_required);
        }
        {
            List<String> list_string_optional = new ArrayList<>();
            list_string_optional.add("People");
            body = body.field("list_string_optional", list_string_optional);
        }
        {
            List<String> character_enum_required = new ArrayList<>();
            character_enum_required.add("a");
            body = body.field("character_enum_required", character_enum_required);
        }
        {
            List<String> character_enum_optional = new ArrayList<>();
            character_enum_optional.add("a");
            body = body.field("character_enum_optional", character_enum_optional);
        }
        {
            List<String> list_double_required = new ArrayList<>();
            list_double_required.add("1.4");
            list_double_required.add("1");
            list_double_required.add("1.45");
            body = body.field("list_double_required", list_double_required);
        }
        {
            List<String> list_double_optional = new ArrayList<>();
            list_double_optional.add("1.4");
            list_double_optional.add("1");
            list_double_optional.add("1.45");
            body = body.field("list_double_optional", list_double_optional);
        }
        {
            List<String> long_required = new ArrayList<>();
            long_required.add("1");
            body = body.field("long_required", long_required);
        }
        {
            List<String> long_optional = new ArrayList<>();
            long_optional.add("1");
            body = body.field("long_optional", long_optional);
        }
        {
            List<String> list_datetime_enum_required = new ArrayList<>();
            list_datetime_enum_required.add("2016-09-02T01:00:00+07:00");
            list_datetime_enum_required.add("2016-09-02T02:00:00+07:00");
            body = body.field("list_datetime_enum_required", list_datetime_enum_required);
        }
        {
            List<String> list_datetime_enum_optional = new ArrayList<>();
            list_datetime_enum_optional.add("2016-09-02T01:00:00+07:00");
            list_datetime_enum_optional.add("2016-09-02T02:00:00+07:00");
            body = body.field("list_datetime_enum_optional", list_datetime_enum_optional);
        }
        {
            List<String> list_time_enum_required = new ArrayList<>();
            list_time_enum_required.add("01:00:00");
            list_time_enum_required.add("02:00:00");
            body = body.field("list_time_enum_required", list_time_enum_required);
        }
        {
            List<String> list_time_enum_optional = new ArrayList<>();
            list_time_enum_optional.add("01:00:00");
            list_time_enum_optional.add("02:00:00");
            body = body.field("list_time_enum_optional", list_time_enum_optional);
        }
        {
            List<String> boolean_required = new ArrayList<>();
            boolean_required.add("false");
            body = body.field("boolean_required", boolean_required);
        }
        {
            List<String> boolean_optional = new ArrayList<>();
            boolean_optional.add("false");
            body = body.field("boolean_optional", boolean_optional);
        }
        {
            List<String> time_required = new ArrayList<>();
            time_required.add("01:22:22");
            body = body.field("time_required", time_required);
        }
        {
            List<String> time_optional = new ArrayList<>();
            time_optional.add("01:22:22");
            body = body.field("time_optional", time_optional);
        }
        {
            List<String> datetime_required = new ArrayList<>();
            datetime_required.add("2016-09-02T02:00:00+07:00");
            body = body.field("datetime_required", datetime_required);
        }
        {
            List<String> datetime_optional = new ArrayList<>();
            datetime_optional.add("2016-09-02T02:00:00+07:00");
            body = body.field("datetime_optional", datetime_optional);
        }
        {
            List<String> list_boolean_enum_required = new ArrayList<>();
            list_boolean_enum_required.add("true");
            list_boolean_enum_required.add("false");
            body = body.field("list_boolean_enum_required", list_boolean_enum_required);
        }
        {
            List<String> list_boolean_enum_optional = new ArrayList<>();
            list_boolean_enum_optional.add("true");
            list_boolean_enum_optional.add("false");
            body = body.field("list_boolean_enum_optional", list_boolean_enum_optional);
        }
        {
            List<String> list_character_enum_required = new ArrayList<>();
            list_character_enum_required.add("b");
            list_character_enum_required.add("a");
            body = body.field("list_character_enum_required", list_character_enum_required);
        }
        {
            List<String> list_character_enum_optional = new ArrayList<>();
            list_character_enum_optional.add("b");
            list_character_enum_optional.add("a");
            body = body.field("list_character_enum_optional", list_character_enum_optional);
        }
        {
            List<String> date_required = new ArrayList<>();
            date_required.add("2016-12-24");
            body = body.field("date_required", date_required);
        }
        {
            List<String> date_optional = new ArrayList<>();
            date_optional.add("2016-12-24");
            body = body.field("date_optional", date_optional);
        }

        System.out.println(requestBody.asString());
    }

}
