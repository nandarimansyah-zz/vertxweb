package id.nanda.vertxweb;

import com.github.davidmoten.rx.jdbc.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import id.nanda.vertxweb.database.DataStore;
import id.nanda.vertxweb.database.DataStoreImpl;
import id.nanda.vertxweb.model.User;
import id.nanda.vertxweb.model.UserSearchResult;
import id.nanda.vertxweb.util.Runner;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.templ.ThymeleafTemplateEngine;

import java.util.List;

/**
 * Created by nanda on 8/24/16.
 */
public class Server extends AbstractVerticle {

    private DataStore dataStore;

    public static void main(String[] args) {
        Runner.runExample(Server.class);
    }

    @Override
    public void start() throws Exception {
        Config config = createConfig();
        dataStore = createDataStore(config);

        // To simplify the development of the web components we use a Router to route all HTTP requests
        // to organize our code in a reusable way.
        final Router router = Router.router(vertx);

        // In order to use a Thymeleaf template we first need to create an engine
        final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();

        // Entry point to the application, this will render a custom JADE template.
        router.get("/").handler(ctx -> {

            List<User> users = dataStore.getAllUsersAsStream().toBlocking().last();
            ctx.put("users", users);

            // and now delegate to the engine to render it.
            engine.render(ctx, "templates/index.html", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        router.get("/add-user").handler(event -> {

            engine.render(event, "templates/insert-user.html", res -> {
                if (res.succeeded()) {
                    event.response().end(res.result());
                } else {
                    event.fail(res.cause());
                }
            });
        });

        router.get("/search").handler(event -> {
            String searchTerm = event.request().getParam("searchTerm");

            int limit = 2;
            int offset = 0;

            String offsetParam = event.request().getParam("offset");
            if (offsetParam != null && !offsetParam.isEmpty()) {
                int offsetParamInt = Integer.parseInt(offsetParam);
                offset = offsetParamInt;
                event.put("previous-offset", offset - offsetParamInt);
            }

            if (searchTerm != null) {
                try {
                    List<UserSearchResult> users = dataStore.searchUserByName(searchTerm, limit, offset).toBlocking().last();

                    if(users != null && users.size() > 0) {
                        event.put("users", users);

                        int total = users.get(0).getTotalSearchResult();
                        int nextOffset = offset + limit;
                        if(nextOffset < total) {
                            event.put("next-offset", nextOffset);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            engine.render(event, "templates/search.html", res -> {
                if (res.succeeded()) {
                    event.response().end(res.result());
                } else {
                    event.fail(res.cause());
                }
            });
        });

        router.post().handler(BodyHandler.create());
        router.post("/user/insert").handler(event -> {

            String userId = event.request().formAttributes().get("userId");
            String userName = event.request().formAttributes().get("userName");

            boolean result = false;
            if (userId != null && userName != null) {
                try {
                    result = dataStore.insertUser(userId, userName).toBlocking().last() > 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            event.put("response", result ? "Success insert user for id [" + userId + "]" : "fail insert user");


            engine.render(event, "templates/response.html", res -> {
                if (res.succeeded()) {
                    event.response().end(res.result());
                } else {
                    event.fail(res.cause());
                }
            });
        });

        // start a HTTP web server on port 8080
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private Config createConfig() {
        Config config = new Config();
        config.set(Config.JDBC_URL, "jdbc:postgresql://localhost:2345/sample");
        config.set(Config.JDBC_USERNAME, "username");
        config.set(Config.JDBC_PASSWORD, "password");
        config.set(Config.JDBC_MAX_POOL_SIZE, "10");
        config.set(Config.JDBC_CONNECTION_TIME_OUT_MS, "30000");
        return config;
    }

    private static DataStore createDataStore(Config config) {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.postgresql.ds.PGSimpleDataSource");
        hikariConfig.setJdbcUrl(config.get(Config.JDBC_URL));
        hikariConfig.setUsername(config.get(Config.JDBC_USERNAME));
        hikariConfig.setPassword(config.get(Config.JDBC_PASSWORD));
        hikariConfig.setMaximumPoolSize(config.getAsInt(Config.JDBC_MAX_POOL_SIZE));
        hikariConfig.setConnectionTimeout(config.getAsLong(Config.JDBC_CONNECTION_TIME_OUT_MS));

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        return new DataStoreImpl(Database.fromDataSource(dataSource));
    }
}
