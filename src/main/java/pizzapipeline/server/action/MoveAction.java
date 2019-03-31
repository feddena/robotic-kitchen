package pizzapipeline.server.action;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;

import pizzapipeline.server.Location;

public abstract class MoveAction extends Action {

    private final Location from;

    private final Location to;

    public MoveAction(int ordinalNumber, @NotNull Location from, @NotNull Location to) {
        super(ordinalNumber);
        Validate.notNull(from);
        Validate.notNull(to);
        Validate.isTrue(from != to);

        this.from = from;
        this.to = to;
    }

    @NotNull
    public Location getFrom() {
        return from;
    }

    @NotNull
    public Location getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MoveAction that = (MoveAction) o;
        return from == that.from &&
                to == that.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }

    @Override
    public String toString() {
        return "MoveAction{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
