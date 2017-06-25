package logicalguess.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by logicalguess on 6/24/17.
 */
public class Items implements Identifiable {
    public final String id;
    public final List<Item> items;

    private Items(String id, List<Item> items) {
        this.id = id;
        this.items = items;
    }

    public static Items create(String id,  List<Item> items) {
        return new Items(id, items);
    }

    public static Items create(String id) {
        return new Items(id, new ArrayList<>());
    }

    @Override
    public String getId() {
        return id;
    }

    public Items add(Item item) {
        items.add(item);
        return create(id, items);
    }

    public Items remove(String id) {
        return Items.create(id, items.stream().filter(item -> item.id != id).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %s]", getClass().getSimpleName(), id, items);
    }
}
