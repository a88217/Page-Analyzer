@import hexlet.code.dto.MainPage
@param MainPage page

@template.layout.page(
page = page,
content = @`
    <main>
        <h1>Анализатор страниц</h1>
        <h3>Бесплатно проверяйте сайты на SEO пригодность</h3>
        <form action="/urls" method="post">
            <input type="text" required name="url">
            <input type="submit" value="Проверить">
        </form>
    </main>
`)
