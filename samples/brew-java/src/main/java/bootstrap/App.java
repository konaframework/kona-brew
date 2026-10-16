package bootstrap;

import io.konaframework.brew.BrewServiceProvider;
import io.konaframework.core.Application;
import io.konaframework.core.Env;
import io.konaframework.core.Kona;
import io.konaframework.crema.CremaServiceProvider;
import io.konaframework.http.HttpKernel;
import io.konaframework.http.HttpMiddleware;
import io.konaframework.http.HttpServiceProvider;
import io.konaframework.http.KonaHttpServer;
import io.konaframework.http.Router;
import io.konaframework.session.SessionManager;
import io.konaframework.session.SessionServiceProvider;
import io.konaframework.session.StartSession;
import io.konaframework.session.VerifyCsrfToken;
import io.konaframework.http.Middleware;
import java.util.List;
import java.util.function.Supplier;
import routes.WebRoutes;

public final class App {
    private App() {}

    public static Application create(String[] args) {
        Application app = Kona.create(args)
            .configure(config -> {
                config.set("app.key", Env.getOrDefault("APP_KEY", "brew-sample-java-key"));
                config.set("app.debug", true);
                config.set("session.driver", "cookie");
            })
            .providers(new HttpServiceProvider(), new SessionServiceProvider(), new CremaServiceProvider(), new BrewServiceProvider())
            .afterBoot(App::middleware)
            .boot();
        WebRoutes.register(app.resolve(Router.class));
        return app;
    }

    private static void middleware(Application app) {
        SessionManager sessions = app.resolve(SessionManager.class);
        new HttpMiddleware()
            .alias("start.session", (Supplier<Middleware>) () -> new StartSession(sessions))
            .alias("csrf", (Supplier<Middleware>) VerifyCsrfToken::new)
            .web(List.of("start.session", "csrf"))
            .apply(app);
    }

    public static void main(String[] args) {
        Application app = create(args);
        String host = Env.getOrDefault("HOST", "127.0.0.1");
        int port = Integer.parseInt(Env.getOrDefault("PORT", "4000"));
        new KonaHttpServer(app.resolve(HttpKernel.class), host, port).start();
    }
}
