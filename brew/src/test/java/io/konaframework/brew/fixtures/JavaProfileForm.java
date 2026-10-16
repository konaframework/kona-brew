package io.konaframework.brew.fixtures;

import io.konaframework.brew.Form;
import io.konaframework.brew.Rule;
import io.konaframework.brew.Validate;

/** Java form so {@code @Rule} lands on the public field for FormMeta. */
public class JavaProfileForm extends Form {
    @Validate({"required", "min:2"})
    public String name = "";

    @Rule({"max:40"})
    public String nickname = "";
}
