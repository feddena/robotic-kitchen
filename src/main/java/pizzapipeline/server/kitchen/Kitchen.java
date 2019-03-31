package pizzapipeline.server.kitchen;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pizzapipeline.server.action.Action;
import pizzapipeline.server.action.ActionType;
import pizzapipeline.server.device.Device;
import pizzapipeline.server.device.InterractionResult;
import pizzapipeline.server.item.Item;

public class Kitchen {
    private static final Logger log = LoggerFactory.getLogger(Kitchen.class);
    private final Map<ActionType, Device> tools;

    public Kitchen(@NotNull Map<ActionType, Device> tools) {
        Validate.notNull(tools);

        this.tools = Collections.unmodifiableMap(tools);
    }

    public boolean cook(@NotNull Item item) {
        Validate.notNull(item);

        List<ActionType> kitchenUnableDoActions =
                item.getRecipe()
                .getActions().stream()
                .map(Action::getType)
                .filter(action -> !tools.containsKey(action))
                .collect(Collectors.toList());

        if (!kitchenUnableDoActions.isEmpty()) {
            log.warn("Kitchen unable to cook it dude because tools for {} unavailable", kitchenUnableDoActions);
            return false;
        }

        log.warn("Kitchen starting to cook your {} dude", item.getType());

        for (Action action : item.getRecipe().getActions()) {
            tools.get(action.getType()).apply(item, action);
            if (item.getItemState().getLastAppliedActionResult() != InterractionResult.SUCCESS) {
                log.warn("Something goes wrong - fix me bro - your {} will not be cooked", item.getType());
                return false;
            }
        }

        return true;
    }
}
