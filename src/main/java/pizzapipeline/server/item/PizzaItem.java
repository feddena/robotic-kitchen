package pizzapipeline.server.item;

import org.apache.commons.lang3.Validate;

import com.sun.istack.internal.NotNull;

import pizzapipeline.server.recipe.Recipe;

public class PizzaItem extends Item {

    private final Recipe recipe;

    public PizzaItem(@NotNull Recipe recipe) {
        Validate.notNull(recipe);

        this.recipe = recipe;
    }

    public ItemType getType() {
        return ItemType.PIZZA;
    }

    @NotNull
    @Override
    public Recipe getRecipe() {
        return recipe;
    }

}
