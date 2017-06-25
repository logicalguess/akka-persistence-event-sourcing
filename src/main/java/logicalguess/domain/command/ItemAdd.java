package logicalguess.domain.command;

import java.io.Serializable;

/**
 * Created by logicalguess on 6/24/17.
 */
public class ItemAdd implements Serializable {
    public final String id;
    public final String value;

    private ItemAdd(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public static ItemAdd create(String id, String name) {
        return new ItemAdd(id, name);
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %s]", getClass().getSimpleName(), id, value);
    }
}
