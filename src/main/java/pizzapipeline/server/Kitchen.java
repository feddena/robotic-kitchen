package pizzapipeline.server;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.sun.istack.internal.NotNull;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.device.Device;
import pizzapipeline.server.device.InterractionResult;
import pizzapipeline.server.item.Item;

public class Kitchen {
    private final Map<ActionType, Device> tools;

    public Kitchen(@NotNull Map<ActionType, Device> tools) {
        Validate.notNull(tools);

        this.tools = Collections.unmodifiableMap(tools);
    }

    public boolean cook(@NotNull Item item) {
        Validate.notNull(item);

        List<ActionType> kitchenUnableToCookItem =
                item.getRecipe()
                .getActions().stream()
                .map(Action::getType)
                .filter(action -> !tools.containsKey(action))
                .collect(Collectors.toList());

        if (!kitchenUnableToCookItem.isEmpty()) {
            System.out.println("Kitchen unable to cook it dude because tools for " + kitchenUnableToCookItem + " unavailable");
            return false;
        }

        System.out.println("Kitchen starting to cook your " + item.getType() + " dude");

        for (Action action : item.getRecipe().getActions()) {
            tools.get(action.getType()).apply(item, action);
            if (item.getItemState().getLastAppliedActionResult() != InterractionResult.SUCCESS) {
                System.out.println("Something goes wrong - fix me bro - your " + item.getType() + " will not be cooked");
                return false;
            }
        }

        return true;
    }
}
