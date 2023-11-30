package ua.klesaak.simpleconomy.storage.mysql.driver;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.val;

import java.util.concurrent.TimeUnit;

@Getter
public abstract class AbstractConnectionFactory {
    protected final HikariConfig hikariConfig;
    protected final boolean isUseSSL;

    public AbstractConnectionFactory(String userName, String passWord, String address, String port, String database, boolean isUseSSL) {
        this.hikariConfig = new HikariConfig();
        this.hikariConfig.setDriverClassName(this.driverClassName());
        this.overrideProperties();
        this.hikariConfig.setJdbcUrl(this.getHost(address, port == null ? this.defaultPort() : port, database));
        this.hikariConfig.setMaximumPoolSize(3);
        this.hikariConfig.setPoolName("SimpleConomy-pool");
        this.hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(30L));
        this.hikariConfig.setUsername(userName);
        this.hikariConfig.setPassword(passWord);
        this.isUseSSL = isUseSSL;
    }

    public String getHost(String address, String port, String database) {
        val builder = new StringBuilder("jdbc:");
        builder.append(this.driverJdbcIdentifier());
        builder.append("://");
        builder.append(address);
        builder.append(":");
        builder.append(port);
        builder.append("/");
        builder.append(database);
        return builder.toString();
    }


    protected abstract String getImplementationName();

    protected abstract String defaultPort();

    protected abstract String driverClassName();

    protected abstract String driverJdbcIdentifier();

    protected abstract void overrideProperties();
}
