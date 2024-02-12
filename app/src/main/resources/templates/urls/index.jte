@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.utils.NamedRoutes
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <main>
        <h1>Сайты</h1>
        @if(page.getUrls().isEmpty())
            <p>Пока не добавлено ни одного сайта</p>
        @else
        <table>
            <tr>
                <th>ID</th>
                <th>Имя</th>
            </tr>
            @for(var url : page.getUrls())
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="${NamedRoutes.urlsPath()}/${url.getId()}">${url.getName()}</a></td>
                </tr>
            @endfor
        </table>
        @endif
    </main>
`)