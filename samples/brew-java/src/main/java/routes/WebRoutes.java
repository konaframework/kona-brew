package routes;

import io.konaframework.core.Kona;
import io.konaframework.crema.CremaEngine;
import io.konaframework.http.Response;
import io.konaframework.http.Router;
import java.util.Map;

public final class WebRoutes {
    private WebRoutes() {}

    public static void register(Router router) {
        router.get("/", request -> {
            CremaEngine crema = Kona.app().resolve(CremaEngine.class);
            return Response.html(crema.render("welcome", Map.of("title", "Brew sample (Java)")));
        }).name("welcome");
    }
}
