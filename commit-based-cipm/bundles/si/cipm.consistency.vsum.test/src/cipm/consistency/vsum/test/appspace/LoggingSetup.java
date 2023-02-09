package cipm.consistency.vsum.test.appspace;

import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class LoggingSetup {

    private static final Level DEFAULT_LOG_LEVEL = Level.DEBUG;

    private static class TrimmingLogFormat extends PatternLayout {
        private List<String> trim;

        public TrimmingLogFormat(String format, List<String> trim) {
            super(format);
            this.trim = trim;
        }

        @Override
        public String format(LoggingEvent event) {
            String msg = super.format(event);
            for (var t : trim) {
                msg = msg.replace(t, "[..]");
            }
            return msg;
        }
    }

    public static void setupLogging(Level logLevel) {
        // set log levels of the framework
        Logger.getLogger("cipm.consistency")
            .setLevel(Level.INFO);

        Logger.getLogger("cipm.consistency.commitintegration.cpr.luapcm")
            .setLevel(Level.WARN);
        Logger.getLogger("mir.reactions")
            .setLevel(Level.WARN);
        Logger.getLogger("mir.routines")
            .setLevel(Level.WARN);
        Logger.getLogger("mir.routines.statement_actions")
            .setLevel(Level.WARN);
        Logger.getLogger("jamopp")
            .setLevel(Level.ALL);
        Logger.getLogger("tools.vitruv")
            .setLevel(Level.INFO);
        Logger.getLogger("tools.vitruv.framework.vsum.internal.VirtualModelImpl")
            .setLevel(Level.WARN);
        Logger.getLogger("org.xtext.lua")
            .setLevel(Level.INFO);

        var rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ALL);
        rootLogger.removeAllAppenders();
        var toTrim = List.of(System.getProperty("user.dir"), "org.xtext.lua.lua.impl", "cipm.consistency");
        var logFormat = new TrimmingLogFormat("%-5p: %c%n    %m%n", toTrim);
        ConsoleAppender ap = new ConsoleAppender(logFormat, ConsoleAppender.SYSTEM_OUT);
        rootLogger.addAppender(ap);
    }

    public static void setupLogging() {
        setupLogging(DEFAULT_LOG_LEVEL);
    }
}
