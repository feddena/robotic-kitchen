package pizzapipeline.server.item;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import pizzapipeline.server.Location;
import pizzapipeline.server.device.InterractionResult;

public class ItemState {
    private final static int ACTION_START_ID = -1;

    private final int lastAppliedActionId;
    private final InterractionResult lastAppliedActionResult;
    private final Location location;

    public ItemState() {
        this.lastAppliedActionId = ACTION_START_ID;
        lastAppliedActionResult = InterractionResult.SUCCESS;
        location = Location.TABLE_BEFORE_OVEN;
    }

    public ItemState(int lastAppliedActionId,
                     @NotNull InterractionResult lastAppliedActionResult,
                     @NotNull Location location) {
        Validate.notNull(lastAppliedActionResult);
        Validate.notNull(location);

        this.lastAppliedActionId = lastAppliedActionId;
        this.lastAppliedActionResult = lastAppliedActionResult;
        this.location = location;
    }

    public int getLastAppliedActionId() {
        return lastAppliedActionId;
    }

    public InterractionResult getLastAppliedActionResult() {
        return lastAppliedActionResult;
    }

    public Location getLocation() {
        return location;
    }
}
