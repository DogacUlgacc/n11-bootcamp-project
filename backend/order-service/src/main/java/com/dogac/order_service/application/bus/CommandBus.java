package com.dogac.order_service.application.bus;

import com.dogac.order_service.application.core.Command;

public interface CommandBus {
    <R, C extends Command<R>> R send(C command);
}
