package logicalguess.domain;

/**
 * Created by logicalguess on 6/24/17.
 */
public class Item implements Identifiable {
    public final String id;
    public final String value;

    private Item(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public static Item create(String id, String name) {
        return new Item(id, name);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return id.equals(item.id) && value.equals(item.value);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %s]", getClass().getSimpleName(), id, value);
    }
}
