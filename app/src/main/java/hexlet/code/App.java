package hexlet.code;

import io.javalin.Javalin;

import java.io.IOException;
import java.sql.SQLException;

public class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");

        return Integer.valueOf(port);
    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() throws IOException, SQLException {

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }
}