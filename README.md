Commands:

    ActorRef items = actorSystem.actorOf(ItemsCommandProcessor.props("1"), "items");

    items.tell(ItemAdd.create("101", "first"), null);
    items.tell(ItemAdd.create("102", "first"), null);
    
Query:
    
    ActorRef items = actorSystem.actorOf(ItemsQueryProcessor.props("1"), "items");
    
    //items.tell(ItemsGet.create("1"), null);
    
    CompletionStage<Object> f = ask(items, ItemsGet.create("1"), 3000L);
    Thread.sleep(3000);
    System.out.println(f.toCompletableFuture().get());
    