package routes;

import dev.kona.core.Kona;
import dev.kona.crema.CremaEngine;
import dev.kona.http.Response;
import dev.kona.http.Router;
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
