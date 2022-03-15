# Transcriptorium

Transcriptorium is a webapp for transcribing text on images by hand.
The image (of a text) is shown side by side with the text input area.

## Development

Run: Run file "Application.java", http://localhost:10000/edit

Verzeichnis der Digitalisate (jpg-files):

`/home/ralf/DEV/SOURCES/de.alexandria--parent/docs/Achleitner_Arthur/Bayern_wie_es_war_und_ist-1/`

Path to html-files: Enter into input field

Path to image-files: FIXME: fixed path to images:
- ImageController: "/home/ralf/DEV/SOURCES/de.alexandria--parent/docs/Achleitner_Arthur/Bayern_wie_es_war_und_ist-1/image-" + identifier + ".jpg"

## References

- https://spring.io/guides/gs/spring-boot/
- http://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-first-application.html
- http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-customizing-embedded-containers
- http://www.opencredo.com/2014/02/24/experiences-with-spring-boot/

src/main/resources/application.properties - server.port=10000
supports application-{profile}.properties via --spring.profiles.active=DEV etc.

or

java -jar spring-boot-example-0.0.1-SNAPSHOT.jar --server.port=12000


    /health – returns “ok” as text/plain content which is useful for simple service monitoring
    /env – check environment configuration, property file and command line argument overrides, active profiles
    /metrics – basic statistics on your service endpoints (e.g. hit count, error count)
    /dump – thread dump
    /trace – the latest HTTP request/response pairs

- http://blog.codeleak.pl/2014/04/how-to-spring-boot-and-thymeleaf-with-maven.html

Where to put Thymeleaf templates?
The default place for templates is ... templates available in classpath.
So we need to put at least one template into src/main/resources/templates directory.


mvn spring-boot:run

- http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-static-content

in src/main/resources/resources/css / .../resources/js:
(really two times resources! (or public/static)

<link href="/css/bootstrap.min.css" th:href="@{/css/bootstrap.min.css}" rel="stylesheet" media="screen" />
<script src="/js/bootstrap.min.js" th:src="@{/js/bootstrap.min.js}"></script>

- http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#common-application-properties
application.properties in src/main/resources directory
spring.thymeleaf.cache=false

split-pane: http://www.dreamchain.com/split-pane/

- admin panel: https://blog.codecentric.de/en/2014/08/spring-boot-admin-2/, https://github.com/codecentric/spring-boot-admin

slide-show:
http://stackoverflow.com/questions/5278374/ajax-request-help-for-next-previous-page + http://jsfiddle.net/Jaybles/MawSB/

- HTML
<input id="next" type="button" value="Next" />
<input id="prev" type="button" value="Previous" /> 
<div id="displayResults" name="displayResults"></div>

- JS
var currentPage=1;
loadCurrentPage();

$("#next, #prev").click(function(){
    currentPage= ($(this).attr('id')=='next') ? currentPage + 1 : currentPage - 1;

    if (currentPage==0) 
        currentPage=1;
    else if (currentPage==101) 
        currentPage=100;
    else
        loadCurrentPage();
});

function loadCurrentPage(){
    $('input').attr('disabled','disabled');
    $('#displayResults').html('<img src="http://blog-well.com/wp-content/uploads/2007/06/indicator-big-2.gif" />');
    
    $.ajax({
        url: '/echo/html/',
        data: 'html=Current Page: ' + currentPage+'&delay=1',
        type: 'POST',
        success: function (data) {
            $('input').attr('disabled','');
            $('#displayResults').html(data);
        }
    });
}


WYSIWYG:
http://hackerwins.github.io/summernote

Crop image with javascript:
- http://odyniec.net/projects/imgareaselect/
