package logicalguess.domain.event;

import java.io.Serializable;

/**
 * Created by logicalguess on 6/24/17.
 */
public class ItemAdded implements Serializable {
    public final String id;
    public final String value;

    private ItemAdded(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public static ItemAdded create(String id, String name) {
        return new ItemAdded(id, name);
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %s]", getClass().getSimpleName(), id, value);
    }
}
