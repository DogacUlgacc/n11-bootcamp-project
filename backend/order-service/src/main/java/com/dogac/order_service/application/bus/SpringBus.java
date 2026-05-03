package com.dogac.order_service.application.bus;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.dogac.order_service.application.core.Command;
import com.dogac.order_service.application.core.CommandHandler;
import com.dogac.order_service.application.core.Query;
import com.dogac.order_service.application.core.QueryHandler;

@Component
public class SpringBus implements CommandBus, QueryBus {

    private final ApplicationContext context;

    public SpringBus(ApplicationContext context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, C extends Command<R>> R send(C command) {
        ResolvableType commandInterface = ResolvableType.forClass(command.getClass()).as(Command.class);
        Class<?> responseType = commandInterface.getGeneric(0).resolve();
        String[] beanNames = context.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(CommandHandler.class, command.getClass(), responseType));
        if (beanNames.length == 0) {
            throw new IllegalStateException("No CommandHandler found for " + command.getClass().getSimpleName());
        }
        CommandHandler<C, R> handler = (CommandHandler<C, R>) context.getBean(beanNames[0]);
        return handler.handle(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, Q extends Query<R>> R execute(Q query) {
        ResolvableType queryInterface = ResolvableType.forClass(query.getClass()).as(Query.class);
        Class<?> responseType = queryInterface.getGeneric(0).resolve();
        String[] beanNames = context.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(QueryHandler.class, query.getClass(), responseType));
        if (beanNames.length == 0) {
            throw new IllegalStateException("No QueryHandler found for " + query.getClass().getSimpleName());
        }
        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) context.getBean(beanNames[0]);
        return handler.handle(query);
    }
}
