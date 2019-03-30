package pizzapipeline.server.action;

import java.util.Objects;

public class CookInOvenAction extends Action {

    private final int secondsRequired;

    private final int ovenTempRequired;

    public CookInOvenAction(int ordinalNumber, int secondsRequired, int ovenTempRequired) {
        super(ordinalNumber);
        this.secondsRequired = secondsRequired;
        this.ovenTempRequired = ovenTempRequired;
    }

    public int getSecondsRequired() {
        return secondsRequired;
    }

    public int getOvenTempRequired() {
        return ovenTempRequired;
    }

    @Override
    public ActionType getType() {
        return ActionType.COOK_IN_OVEN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CookInOvenAction that = (CookInOvenAction) o;
        return secondsRequired == that.secondsRequired &&
                ovenTempRequired == that.ovenTempRequired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), secondsRequired, ovenTempRequired);
    }

    @Override
    public String toString() {
        return "CookInOvenAction{" +
                "secondsRequired=" + secondsRequired +
                ", ovenTempRequired=" + ovenTempRequired +
                '}';
    }
}
