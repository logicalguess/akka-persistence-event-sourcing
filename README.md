Commands:

    ActorRef items = actorSystem.actorOf(ItemsCommandProcessor.props("1"), "items");

    items.tell(ItemAdd.create("101", "first"), null);
    items.tell(ItemAdd.create("102", "second"), null);
    
Query:
    
    ActorRef items = actorSystem.actorOf(ItemsQueryProcessor.props("1"), "items");
    
    //items.tell(ItemsGet.create("1"), null);
    
    CompletionStage<Object> f = ask(items, ItemsGet.create("1"), 3000L);
    Thread.sleep(3000);
    System.out.println(f.toCompletableFuture().get());
    
    ---------------------------------------------------------
    EVENT: ItemAdded[101, first]
    EVENT: ItemAdded[102, second]
    Items[1, [Item[101, first], Item[102, second]]]
    