package hexlet.code;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import javax.print.DocFlavor;

import static org.assertj.core.api.Assertions.assertThat;


public class TestApp {

    Javalin app;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testRootPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(
                    "Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Сайты");
        });
    }

    @Test
    public void testCreateUrl() throws SQLException{
        var url = new Url("https://example.com", Time.getTime());
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string().contains("https://example.com"));
            assertThat(UrlRepository.getEntities()).hasSize(1);
        });
    }

    @Test
    public void testCreateUrlWithPort() throws SQLException{
        var url = new Url("https://example.com:8080", Time.getTime());
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com:8080";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string().contains("https://example.com:8080"));
            assertThat(UrlRepository.getEntities()).hasSize(1);
        });
    }

    @Test
    public void testCreateWrongUrl() throws SQLException{
        var url = new Url("123456789", Time.getTime());
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=123456789";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string().contains("Анализатор страниц"));
            assertThat(UrlRepository.getEntities()).hasSize(0);
        });
    }

    @Test
    public void testCreateExistingUrl() throws SQLException {
        var url = new Url("https://example.com", Time.getTime());
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string().contains("https://example.com"));
            assertThat(UrlRepository.getEntities()).hasSize(1);

            var response2 = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response2.code()).isEqualTo(200);
            assertThat(UrlRepository.getEntities()).hasSize(1);
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var time = Time.getTime();
        var url = new Url("https://example.com", time);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var urlId = url.getId();
            var response = client.get(NamedRoutes.urlPath(urlId));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(
                    "Сайт:", url.getName()
            );
            assertThat(UrlRepository.getEntities()).hasSize(1);
        });
    }

}
