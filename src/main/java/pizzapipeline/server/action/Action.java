package pizzapipeline.server.action;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public abstract class Action {

    private final int ordinalNumber;

    public Action(int ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    @NotNull
    public abstract ActionType getType();

    public int getActionOrdinalNumber() {
        return ordinalNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return ordinalNumber == action.ordinalNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordinalNumber);
    }

    @Override
    public String toString() {
        return "Action{" +
                "ordinalNumber=" + ordinalNumber +
                '}';
    }
}
