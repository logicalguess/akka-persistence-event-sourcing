package logicalguess.operation;

import logicalguess.domain.Item;
import logicalguess.domain.Items;

import java.util.function.Function;

/**
 * Created by logicalguess on 6/24/17.
 */
public class ItemAddOperation implements Function<Item, Void> {
    private final Items items;

    public ItemAddOperation(Items items) {
        this.items = items;
    }

    @Override
    public Void apply(Item item) {
        return null;
    }
}
