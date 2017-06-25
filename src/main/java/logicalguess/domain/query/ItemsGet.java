package logicalguess.domain.query;

import java.io.Serializable;

/**
 * Created by logicalguess on 6/24/17.
 */
public class ItemsGet implements Serializable {
    public final String id;

    private ItemsGet(String id) {
        this.id = id;
    }

    public static ItemsGet create(String id) {
        return new ItemsGet(id);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), id);
    }
}
