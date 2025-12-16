package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.BankAccountHistory;

/**
 * Command port for explicitly recording bank account history.
 *
 * Note: As of now, history is recorded internally within persistence operations
 * (create/update/delete) and this port is not actively used. The unused warning
 * is intentionally suppressed to keep the API available for potential future use
 * or alternative adapters (e.g., outbox/event-sourcing).
 */
@SuppressWarnings("unused")
public interface CommandBankAccountHistoryPort {
    BankAccountHistory record(BankAccountHistory history);
}
