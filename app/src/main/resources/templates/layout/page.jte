@import gg.jte.Content
@import hexlet.code.utils.NamedRoutes
@param Content content

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
    <a href="${NamedRoutes.rootPath()}">Home</a>
</header>
${content}
<footer>
    <a href="https://github.com/a88217">Github</a>
</footer>
</body>
</html>