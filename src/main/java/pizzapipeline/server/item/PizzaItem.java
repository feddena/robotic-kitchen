package pizzapipeline.server.item;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import pizzapipeline.server.recipe.Recipe;

public class PizzaItem extends Item {

    private final Recipe recipe; // TODO move to item ?

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
