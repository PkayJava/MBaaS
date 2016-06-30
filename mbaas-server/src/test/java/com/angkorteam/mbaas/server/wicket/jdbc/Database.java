package com.angkorteam.mbaas.server.wicket.jdbc;

import com.mysql.jdbc.Driver;
import org.apache.commons.io.FileUtils;

/**
 * Created by socheat on 6/29/16.
 */
public interface Database {

    String USER = "root";
    String PASSWORD = "yT2poZujAqCbgMVuOKHYn3O016S7wI";
    String DRIVER = Driver.class.getName();
    String URL = "jdbc:mysql://192.168.1.103/school_management";
    String PROPERTIES = "autoReconnect=true;serverTimezone=UTC;useLegacyDatetimeCode=false;useSSL=false;useUnicode=true;useJDBCCompliantTimezoneShift=true";
    String DICTIONARY = FileUtils.getUserDirectory() + "/.xml/dictionary.txt";
}
