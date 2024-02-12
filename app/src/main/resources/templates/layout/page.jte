@import gg.jte.Content
@import hexlet.code.utils.NamedRoutes
@import hexlet.code.dto.BasePage
@param Content content
@param BasePage page = null

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <title>Hexlet Javalin Example</title>
</head>
<body>
<header>
    <p>Анализатор страниц</p>>
    <a href="${NamedRoutes.rootPath()}">Home</a>
    <a href="${NamedRoutes.urlsPath()}">Сайты</a>
</header>
@if(page != null && page.getFlash() != null)
    @if(page.getFlashType().equals("error"))
        <div class="alert alert-danger" role="alert">
            ${page.getFlash()}
        </div>
    @elseif(page.getFlashType().equals("warning"))
        <div class="alert alert-warning" role="alert">
            ${page.getFlash()}
        </div>
    @else
        <div class="alert alert-success" role="alert">
            ${page.getFlash()}
        </div>
    @endif
@endif
${content}
<footer>
    <a href="https://github.com/a88217">Github</a>
</footer>
</body>
</html>