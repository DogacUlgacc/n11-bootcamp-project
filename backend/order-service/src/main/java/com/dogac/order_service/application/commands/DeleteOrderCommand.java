package com.dogac.order_service.application.commands;

import java.util.UUID;

import com.dogac.order_service.application.core.Command;

import jakarta.validation.constraints.NotNull;

public record DeleteOrderCommand(@NotNull UUID id) implements Command<Void> {
}
