package org.sec.walletservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sec.walletservice.enums.WalletTransactionType;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WalletTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long timestamp;
    private double amount;
    private WalletTransactionType walletTransactionType;
    @ManyToOne
    private Wallet wallet;
}
