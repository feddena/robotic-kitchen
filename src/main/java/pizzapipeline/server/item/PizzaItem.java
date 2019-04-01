package pizzapipeline.server.item;

import javax.validation.constraints.NotNull;

import pizzapipeline.server.recipe.Recipe;

public class PizzaItem extends Item {

    public PizzaItem(@NotNull Recipe recipe, long id) {
        super(id, recipe);
    }

    public ItemType getType() {
        return ItemType.PIZZA;
    }
}
