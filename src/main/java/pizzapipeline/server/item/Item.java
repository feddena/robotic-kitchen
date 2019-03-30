package pizzapipeline.server.item;

import pizzapipeline.server.recipe.Recipe;

public abstract class Item {

    private volatile ItemState itemState = new ItemState();

    public abstract ItemType getType();

    public abstract Recipe getRecipe();

    public void setItemState(ItemState itemState) {
        this.itemState = itemState;
    }

    public ItemState getItemState() {
        return itemState;
    }
}
