package com.angkorteam.mbaas.server.factory;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Created by socheatkhauv on 12/31/16.
 */
public class DataSource implements javax.sql.DataSource {

    public static final String CONNECTION = "CONNECTION";

    private final BasicDataSource dataSource;

    public DataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = null;
        if (servletRequestAttributes != null) {
            request = servletRequestAttributes.getRequest();
        }
        if (request == null) {
            return this.dataSource.getConnection();
        }
        Connection connection = (Connection) request.getAttribute(CONNECTION);
        if (connection == null) {
            connection = this.dataSource.getConnection();
            request.setAttribute(CONNECTION, connection);
        }
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = null;
        if (servletRequestAttributes != null) {
            request = servletRequestAttributes.getRequest();
        }
        if (request == null) {
            return this.dataSource.getConnection(username, password);
        }
        Connection connection = (Connection) request.getAttribute(CONNECTION);
        if (connection == null) {
            connection = this.dataSource.getConnection(username, password);
            request.setAttribute(CONNECTION, connection);
        }
        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.dataSource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.dataSource.getParentLogger();
    }

    public void close() throws SQLException {
        this.dataSource.close();
    }

}
