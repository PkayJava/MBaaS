//package com.angkorteam.mbaas.server.nashorn;
//
//import org.slf4j.Logger;
//
///**
// * Created by socheat on 3/12/16.
// */
//public class Console {
//
//    private final Logger logger;
//
//    public Console(Logger logger) {
//        this.logger = logger;
//    }
//
//    public void info(String msg) {
//        if (this.logger.isInfoEnabled()) {
//            this.logger.info(msg);
//        }
//    }
//
//    public void info(String format, Object arg) {
//        if (this.logger.isInfoEnabled()) {
//            this.logger.info(format, arg);
//        }
//    }
//
//    public void info(String format, Object arg1, Object arg2) {
//        if (this.logger.isInfoEnabled()) {
//            this.logger.info(format, arg1, arg2);
//        }
//    }
//
//    public void info(String format, Object... arguments) {
//        if (this.logger.isInfoEnabled()) {
//            this.logger.info(format, arguments);
//        }
//    }
//
//    public void debug(String msg) {
//        if (this.logger.isDebugEnabled()) {
//            this.logger.debug(msg);
//        }
//    }
//
//    public void debug(String format, Object arg) {
//        if (this.logger.isDebugEnabled()) {
//            this.logger.debug(format, arg);
//        }
//    }
//
//    public void debug(String format, Object arg1, Object arg2) {
//        if (this.logger.isDebugEnabled()) {
//            this.logger.debug(format, arg1, arg2);
//        }
//    }
//
//    public void debug(String format, Object... arguments) {
//        if (this.logger.isDebugEnabled()) {
//            this.logger.debug(format, arguments);
//        }
//    }
//
//    public void error(String msg) {
//        if (this.logger.isErrorEnabled()) {
//            this.logger.error(msg);
//        }
//    }
//
//    public void error(String format, Object arg) {
//        if (this.logger.isErrorEnabled()) {
//            this.logger.error(format, arg);
//        }
//    }
//
//    public void error(String format, Object arg1, Object arg2) {
//        if (this.logger.isErrorEnabled()) {
//            this.logger.error(format, arg1, arg2);
//        }
//    }
//
//    public void error(String format, Object... arguments) {
//        if (this.logger.isErrorEnabled()) {
//            this.logger.error(format, arguments);
//        }
//    }
//
//    public void trace(String msg) {
//        if (this.logger.isTraceEnabled()) {
//            this.logger.trace(msg);
//        }
//    }
//
//    public void trace(String format, Object arg) {
//        if (this.logger.isTraceEnabled()) {
//            this.logger.trace(format, arg);
//        }
//    }
//
//    public void trace(String format, Object arg1, Object arg2) {
//        if (this.logger.isTraceEnabled()) {
//            this.logger.trace(format, arg1, arg2);
//        }
//    }
//
//    public void trace(String format, Object... arguments) {
//        if (this.logger.isTraceEnabled()) {
//            this.logger.trace(format, arguments);
//        }
//    }
//
//    public void warn(String msg) {
//        if (this.logger.isWarnEnabled()) {
//            this.logger.warn(msg);
//        }
//    }
//
//    public void warn(String format, Object arg) {
//        if (this.logger.isWarnEnabled()) {
//            this.logger.warn(format, arg);
//        }
//    }
//
//    public void warn(String format, Object arg1, Object arg2) {
//        if (this.logger.isWarnEnabled()) {
//            this.logger.warn(format, arg1, arg2);
//        }
//    }
//
//    public void warn(String format, Object... arguments) {
//        if (this.logger.isWarnEnabled()) {
//            this.logger.warn(format, arguments);
//        }
//    }
//
//}
