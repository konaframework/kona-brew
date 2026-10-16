package app.brew;

import io.konaframework.brew.Component;
import io.konaframework.brew.Modelable;
import io.konaframework.brew.Reactive;
import java.util.Map;

/** Nested child used by {@link Dashboard} — reactive title, modelable value. */
public class Panel extends Component {
    @Reactive
    public String title = "Panel";

    @Modelable
    public String value = "";

    public void save() {
        dispatch("saved", Map.of("label", title));
    }

    @Override
    public String template() {
        return """
            <div class="card">
                <h3>{{ title }}</h3>
                <input wire:model="value" />
                <button type="button" wire:click="save">Save</button>
                <p>value={{ value }}</p>
            </div>
            """;
    }
}
