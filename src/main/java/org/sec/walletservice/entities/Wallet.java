package org.sec.walletservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Wallet {
    @Id
    private String id;
    private long createdAt;
    private String userId;
    private double balance;
    @ManyToOne
    private Currency currency;

    @OneToMany(mappedBy = "wallet")
    private List<WalletTransaction> walletTransactionList;


}
