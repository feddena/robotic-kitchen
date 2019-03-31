package pizzapipeline.server.device;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.item.Item;
import pizzapipeline.server.item.ItemState;

public abstract class Device {

    public void apply(Item item, Action action) {
        InterractionResult result = interact(item, action);
        item.setItemState(new ItemState(action.getActionOrdinalNumber(), result, item.getItemState().getLocation()));
    }

    abstract InterractionResult interact(Item item, Action action);
}
