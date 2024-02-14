package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;

import static hexlet.code.utils.Time.getTime;

public class UrlCheckController {

    public static void create(Context ctx) throws SQLException {
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
    }
}
