package pizzapipeline.server.recipe;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.sun.istack.internal.NotNull;

import pizzapipeline.server.action.Action;

public class Recipe {
    private final List<Action> actions;

    public Recipe(@NotNull List<Action> actions) {
        Validate.notNull(actions);

        this.actions = Collections.unmodifiableList(actions);
    }

    @NotNull
    public List<Action> getActions() {
        return actions;
    }
}
