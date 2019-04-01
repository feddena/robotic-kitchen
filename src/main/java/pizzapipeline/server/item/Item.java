package pizzapipeline.server.item;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import pizzapipeline.server.recipe.Recipe;

public abstract class Item {

    private final long id;

    private final Recipe recipe;

    private volatile ItemState itemState = new ItemState();

    public Item(long id, @NotNull Recipe recipe) {
        Validate.notNull(recipe);

        this.id = id;
        this.recipe = recipe;
    }

    public abstract ItemType getType();

    public long getId() {
        return id;
    }

    @NotNull
    public Recipe getRecipe() {
        return recipe;
    }

    public void setItemState(@NotNull ItemState itemState) {
        Validate.notNull(itemState);

        this.itemState = itemState;
    }

    @NotNull
    public ItemState getItemState() {
        return itemState;
    }
}
