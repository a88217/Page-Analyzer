package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import static hexlet.code.utils.Time.getTime;
import static org.assertj.core.api.Assertions.assertThat;


public class TestApp {

    Javalin app;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUpMock() throws IOException, SQLException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        app = App.getApp();
    }


    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://javalintest.io";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://javalintest.io");
        });
    }

    @Test
    public void testShowUrlById() throws SQLException {
        var url = new Url("https://javalinTest.io", getTime());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://javalinTest.io");
        });
    }

    @Test
    public void testNotFoundUrlById() {
        JavalinTest.test(app, (server, client) -> {
            client.delete("/test/delete/777");
            var response =  client.get("/urls/777");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testNotCorrectUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=notCorrectUrl";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUniqUrlValidation() throws SQLException {
        var url = new Url("https://javalintest.io", getTime());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://javalintest.io";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testCheckShowUrl() throws SQLException {
        var url = new Url("https://javalintest.io", getTime());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("Проверки")
                    .contains("https://javalintest.io");
        });
    }

    @Test
    public void testParsingResponse() throws SQLException, IOException {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(Files.readString(Paths.get("./src/test/resources/test-page.html")));

        mockWebServer.enqueue(mockResponse);
        var urlName = mockWebServer.url("/testParsingResponse");
        var url = new Url(urlName.toString(), getTime());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks", "");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("Hello, World!")
                    .contains("Sample Page")
                    .contains("Open source Java");
        });
    }

    @Test
    void testStore() throws SQLException, IOException {

        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(Files.readString(Paths.get("./src/test/resources/test-page_2.html")));
        mockWebServer.enqueue(mockResponse);
        var urlName = mockWebServer.url("/testStoreResponse");
        var url = new Url(urlName.toString(), getTime());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var requestFormParam = "url=" + url.getName();
            assertThat(client.post("/urls", requestFormParam).code()).isEqualTo(200);
            var actualUrl = UrlRepository.find(url.getId()).get();
            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(url.getName());

            client.post("/urls/" + actualUrl.getId() + "/checks", "");
            var response = client.get("/urls/" + actualUrl.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(url.getName());

            var actualCheckUrl = UrlChecksRepository
                    .getLastCheck(url.getId()).get();

            assertThat(actualCheckUrl).isNotNull();
            assertThat(actualCheckUrl.getStatusCode()).isEqualTo(200);
            assertThat(actualCheckUrl.getTitle())
                    .isEqualTo("Хекслет — онлайн-школа программирования, онлайн-обучение ИТ-профессиям");
            assertThat(actualCheckUrl.getH1())
                    .isEqualTo("Лучшая школа программирования по версии пользователей Хабра");
            assertThat(actualCheckUrl.getDescription())
                    .contains("Хекслет — лучшая школа программирования по версии пользователей Хабра. "
                            + "Авторские программы обучения с практикой и готовыми проектами в резюме. "
                            + "Помощь в трудоустройстве после успешного окончания обучения");
        });
    }


}
