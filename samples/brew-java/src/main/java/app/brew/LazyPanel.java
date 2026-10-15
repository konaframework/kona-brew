package app.brew;

import dev.kona.brew.Component;
import dev.kona.brew.Lazy;

/** Lazy-loaded panel nested under {@link Dashboard}. */
@Lazy
public class LazyPanel extends Component {
    @Override
    public String placeholder() {
        return "<em>Loading panel…</em>";
    }

    @Override
    public String template() {
        return "<div class=\"card\">Lazy ready</div>";
    }
}
