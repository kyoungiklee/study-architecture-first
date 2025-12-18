package org.opennuri.study.architecture.remittance.application.port.out;

public interface ReserveMoneyPort {
    MoneyReservation reserveMoney(String membershipId, Long amount, String idempotencyKey);
    
    record MoneyReservation(String reservationId, boolean isSuccess) {}
}
