package com.dogac.order_service.application.bus;

import com.dogac.order_service.application.core.Query;

public interface QueryBus {
    <R, Q extends Query<R>> R execute(Q query);
}
