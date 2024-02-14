@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.utils.NamedRoutes
@import hexlet.code.utils.Time
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <section>

        <div class="container-lg mt-5">
            <h1>Сайты</h1>
            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th>Имя</th>
                    <th class="col-2">Последняя проверка</th>
                    <th class="col-1">Код ответа</th>
                </tr>
                </thead>

                <tbody>
        @for(var entry : page.getUrls().entrySet())
                <tr>
                    <td>
                        ${entry.getKey().getId()}
                    </td>
                    <td>
                        <a href="${NamedRoutes.urlsPath()}/${entry.getKey().getId()}">${entry.getKey().getName()}</a>
                    </td>
                    <td>
                        @if(entry.getValue().isPresent())
                        ${Time.timeString(entry.getValue().get().getCreatedAt())}
                        @endif
                    </td>
                    <td>
                        @if(entry.getValue().isPresent())
                        ${entry.getValue().get().getStatusCode()}
                        @endif
                    </td>

                </tr>
        @endfor
                </tbody>
            </table>
        </div>

    </section>

`)