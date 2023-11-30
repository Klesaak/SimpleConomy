package ua.klesaak.simpleconomy.storage.mysql.driver;

import java.util.HashMap;
import java.util.Map;

public class PostgresConnectionFactory extends AbstractConnectionFactory{
    public PostgresConnectionFactory(String userName, String passWord, String address, String port, String database, boolean isUseSSL) {
        super(userName, passWord, address, port, database, isUseSSL);
    }

    @Override
    protected String getImplementationName() {
        return "PostgreSQL";
    }

    @Override
    protected String defaultPort() {
        return "5432";
    }

    @Override
    protected String driverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "postgresql";
    }

    @Override
    protected void overrideProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.putIfAbsent("prepStmtCacheSize", "250");
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        properties.putIfAbsent("cachePrepStmts", "true");
        properties.putIfAbsent("useServerPrepStmts", "true");
        properties.putIfAbsent("cacheServerConfiguration", "true");
        properties.putIfAbsent("useLocalSessionState", "true");
        properties.putIfAbsent("rewriteBatchedStatements", "true");
        properties.putIfAbsent("maintainTimeStats", "false");
        properties.putIfAbsent("useUnbufferedInput", "false");
        properties.putIfAbsent("useReadAheadInput", "false");
        properties.putIfAbsent("useSSL", String.valueOf(this.isUseSSL));
        properties.putIfAbsent("autoReconnect", "true");
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            this.hikariConfig.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }
}
