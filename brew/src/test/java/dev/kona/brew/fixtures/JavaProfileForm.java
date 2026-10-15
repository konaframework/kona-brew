package dev.kona.brew.fixtures;

import dev.kona.brew.Form;
import dev.kona.brew.Rule;
import dev.kona.brew.Validate;

/** Java form so {@code @Rule} lands on the public field for FormMeta. */
public class JavaProfileForm extends Form {
    @Validate({"required", "min:2"})
    public String name = "";

    @Rule({"max:40"})
    public String nickname = "";
}
