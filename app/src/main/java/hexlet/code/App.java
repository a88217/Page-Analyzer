package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.dto.MainPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.DatabaseConfig;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.http.NotFoundResponse;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.validation.ValidationException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static hexlet.code.utils.Time.getTime;

public class App {

    private static final String SCHEMA_FILE = "schema.sql";

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static void main(String[] args) throws IOException, SQLException {
        HttpResponse<String> jsonResponse
                = Unirest.get("https://www.w3schools.com")
                .asString();
        var statusCode = jsonResponse.getStatus();
        var body = jsonResponse.getBody();
        Document doc = Jsoup.parse(body);
        String title = doc.title();
        Element h1Element = doc.selectFirst("h1");
        String h1 = Objects.isNull(h1Element) ? "" : h1Element.text();
        Element descriptionTag = doc.select("meta[name=description]").first();
        String description = Objects.isNull(descriptionTag) ? "" : descriptionTag.attr("content");

        System.out.println(statusCode);
        System.out.println(title);
        System.out.println(description);
        System.out.println(h1);
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
//        Подключение к локальной БД hexlet-jdbc
//        hikariConfig.setJdbcUrl(DatabaseConfig.getDbUrl());
//        hikariConfig.setUsername(DatabaseConfig.getDbUsername());
//        hikariConfig.setPassword(DatabaseConfig.getDbPassword());

        var dataSource = new HikariDataSource(hikariConfig);

        var sql = readResourceFile(SCHEMA_FILE);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        JavalinJte.init(createTemplateEngine());

        app.get(NamedRoutes.rootPath(), ctx -> {
            ctx.render("index.jte");
        });

        app.post(NamedRoutes.urlsPath(), ctx -> {
            var url = ctx.formParamAsClass("url", String.class)
                    .get()
                    .toLowerCase()
                    .trim();
            try {
                var parcedUrl = new URI(url).toURL();
                var protocol = parcedUrl.getProtocol();
                var host = parcedUrl.getHost();
                var port = parcedUrl.getPort();
                var urlString = port == -1 ? protocol + "://" + host : protocol + "://" + host + ":" + port;
                if (UrlRepository.isPresence(urlString)) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.sessionAttribute("flashType", "warning");
                    ctx.redirect(NamedRoutes.urlsPath());
                } else {
                    var urlObject = new Url(urlString, getTime());
                    UrlRepository.save(urlObject);
                    ctx.sessionAttribute("flash", "Страница успешно добавлена");
                    ctx.sessionAttribute("flashType", "success");
                    ctx.redirect(NamedRoutes.urlsPath());
                }
            } catch (URISyntaxException | IllegalArgumentException e) {
                var page = new MainPage();
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flashType", "error");
                page.setFlash(ctx.consumeSessionAttribute("flash"));
                page.setFlashType(ctx.consumeSessionAttribute("flashType"));
                ctx.render("index.jte", Collections.singletonMap("page", page));
            }
        });

        app.get(NamedRoutes.urlsPath(), ctx -> {
            var urls = UrlRepository.getEntities();
            var page = new UrlsPage(urls);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setFlashType(ctx.consumeSessionAttribute("flashType"));
            ctx.render("urls/index.jte", Collections.singletonMap("page", page));
        });

        app.get(NamedRoutes.urlPath("{id}"), ctx -> {
            var id = ctx.pathParamAsClass("id", Long.class).get();
            var url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
            var urlChecks = UrlChecksRepository.getEntities(id);
            var page = new UrlPage(url, urlChecks);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setFlashType(ctx.consumeSessionAttribute("flashType"));
            ctx.render("urls/show.jte", Collections.singletonMap("page", page));
        });

        app.post(NamedRoutes.urlCheckPath("{id}"), ctx -> {
            var urlId = ctx.pathParamAsClass("id", Long.class).get();
            HttpResponse<String> httpResponse
                    = Unirest.get(UrlRepository.find(urlId).get().getName())
                    .asString();
            var statusCode = httpResponse.getStatus();
            var body = httpResponse.getBody();
            Document doc = Jsoup.parse(body);
            String title = doc.title();
            Element h1Element = doc.selectFirst("h1");
            String h1 = Objects.isNull(h1Element) ? "" : h1Element.text();
            Element descriptionTag = doc.select("meta[name=description]").first();
            String description = Objects.isNull(descriptionTag) ? "" : descriptionTag.attr("content");

            var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId, getTime());
            UrlChecksRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        });

        return app;
    }
}