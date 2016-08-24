package id.nanda.vertxweb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nanda on 8/24/16.
 */
public class Config {

    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_USERNAME = "jdbc.username";
    public static final String JDBC_PASSWORD = "jdbc.password";
    public static final String JDBC_MAX_POOL_SIZE = "jdbc.max.pool.size";
    public static final String JDBC_CONNECTION_TIME_OUT_MS = "jdbc.connection.time.out.ms";

    private Properties properties = new Properties();

    public Config() {
    }

    public Config(String path) throws Exception{
        properties.putAll(loadProperties(path));
    }
    private Properties loadProperties(String path) throws Exception{
        InputStream inputStream = new FileInputStream(path);
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } finally {
            inputStream.close();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public int getAsInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public long getAsLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    public void set(String key, String value) {
        properties.setProperty(key,value);
    }
}
