package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import hexlet.code.utils.DatabaseConfig;
import io.javalin.Javalin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class App {

    private static final String SCHEMA_FILE = "schema.sql";

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");

        return Integer.valueOf(port);
    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();
        app.start(getPort());
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static Javalin getApp() throws IOException, SQLException {

        var hikariConfig = new HikariConfig();
        var isProd = System.getenv().getOrDefault("APP_ENV", "dev").equals("prod");

        if (isProd) {
            String username = System.getenv("JDBC_DATABASE_USERNAME");
            String password = System.getenv("JDBC_DATABASE_PASSWORD");
            String url = System.getenv("JDBC_DATABASE_URL");
            hikariConfig.setJdbcUrl(url);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
        } else {
            hikariConfig.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        }

//        hikariConfig.setJdbcUrl(DatabaseConfig.getDbUrl());
//        hikariConfig.setUsername(DatabaseConfig.getDbUsername());
//        hikariConfig.setPassword(DatabaseConfig.getDbPassword());
        var dataSource = new HikariDataSource(hikariConfig);

        // Получаем путь до файла в src/main/resources
//        var url = App.class.getClassLoader().getResource("schema.sql");
//        var file = new File(url.getFile());
//        var sql = Files.lines(file.toPath())
//                .collect(Collectors.joining("\n"));
//
//        // Получаем соединение, создаем стейтмент и выполняем запрос
//        try (var connection = dataSource.getConnection();
//             var statement = connection.createStatement()) {
//            statement.execute(sql);
//        }
        var sql = readResourceFile(SCHEMA_FILE);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }
}