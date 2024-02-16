package hexlet.code.controller;

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
import java.sql.SQLException;
import java.util.Objects;

import static hexlet.code.utils.Time.getTime;

public class UrlCheckController {

    public static void create(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        int statusCode;
        String body;
        String title;
        String h1;
        Element descriptionTag;
        try {
            HttpResponse<String> httpResponse
                    = Unirest.get(UrlRepository.find(urlId).get().getName())
                    .asString();
            statusCode = httpResponse.getStatus();
            body = httpResponse.getBody();
            Document doc = Jsoup.parse(body);
            title = doc.title();
            Element h1Element = doc.selectFirst("h1");
            h1 = Objects.isNull(h1Element) ? "" : h1Element.text();
            descriptionTag = doc.select("meta[name=description]").first();
        } catch (Exception e) {
            return;
        }
        String description = Objects.isNull(descriptionTag) ? "" : descriptionTag.attr("content");
        var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
        UrlChecksRepository.save(urlCheck);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flashType", "success");
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
