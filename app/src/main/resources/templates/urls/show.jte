@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.utils.NamedRoutes
@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <main>
        <h1>Сайт: ${page.getUrl().getName()}</h1>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Имя</th>
                    <th>Дата создания</th>
                </tr>
                <tr>
                    <td>${page.getUrl().getId()}</td>
                    <td>${page.getUrl().getName()}</td>
                    <td>${page.getUrl().getCreatedAt().toString()}</td>
                </tr>
            </table>
    </main>
`)