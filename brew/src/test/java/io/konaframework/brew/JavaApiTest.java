package io.konaframework.brew;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.konaframework.brew.features.InMemoryComputedCache;
import io.konaframework.brew.features.SupportComputed;
import io.konaframework.brew.hydration.Coercion;
import io.konaframework.brew.hydration.Synth;
import io.konaframework.brew.hydration.Synthesizers;
import io.konaframework.brew.meta.ComponentMeta;
import io.konaframework.brew.testing.Testable;
import io.konaframework.core.Config;
import io.konaframework.crema.CremaEngine;
import io.konaframework.crema.MapViewFinder;
import io.konaframework.http.Router;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Java-callable surface: static helpers, {@code @JvmOverloads} variants and fluent testing. */
class JavaApiTest {
    public static class Clicker extends Component {
        public int clicks = 0;
        public String label = "go";

        public void click() {
            clicks++;
        }

        public void hush() {
            skipRender();
        }

        @Computed
        public String shout() {
            return label.toUpperCase();
        }

        @Override
        public String template() {
            return "<button>{{ label }} {{ clicks }} a b c</button>";
        }
    }

    @Test
    void javaOverloadsAndStatics() {
        BrewManager brew = new BrewManager(new BrewConfig("k"), new CremaEngine(new MapViewFinder()));
        brew.component("clicker", Clicker.class);
        Component fresh = brew.newComponent("clicker");
        assertTrue(fresh instanceof Clicker);

        Testable t = Testable.create(brew, "clicker", Map.of());
        t.call("click").assertNotSet("clicks", 0).assertSeeInOrder(List.of("a", "b", "c")).assertDontSee("zzz");
        t.call("hush").assertRenderSkipped();
        assertEquals("GO", SupportComputed.value(brew, t.instance(), "shout"));
        SupportComputed.forget(brew, t.instance(), "shout");

        assertSame(ComponentMeta.of(Clicker.class), ComponentMeta.of(t.instance()));
        assertEquals(20, BrewConfig.from(new Config()).getMaxComponents());
        BrewServiceProvider.routes(brew, new Router());

        assertEquals("raw", BrewView.raw("raw").getHtml());
        assertEquals("v", BrewView.named("v").getName());
        assertEquals("<p/>", BrewView.inline("<p/>").getSource());

        assertEquals("CreatePost", ComponentRegistry.studly("create-post"));
        assertEquals("create-post", ComponentRegistry.kebab("CreatePost"));
        assertEquals("clicker", ComponentRegistry.nameFor(Clicker.class, null));
        assertTrue(new ComponentRegistry().discover("io.konaframework.brew.fixtures.admin_area").contains("user-list"));

        new InMemoryComputedCache().put("a", 1, 1);
        assertEquals(5, Coercion.toType("5", int.class));
        assertNull(Synth.typeArgument(String.class, 0));
        assertEquals(String.class, Synth.rawClass(String.class));
        assertTrue(Synthesizers.isScalar("x"));
        assertFalse(Synthesizers.isTuple("x"));
    }
}
