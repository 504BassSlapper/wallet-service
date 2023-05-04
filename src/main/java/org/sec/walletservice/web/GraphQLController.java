package org.sec.walletservice.web;

import org.sec.walletservice.WalletService;
import org.sec.walletservice.dto.AddWalletRequestDto;
import org.sec.walletservice.entities.Wallet;
import org.sec.walletservice.entities.WalletTransaction;
import org.sec.walletservice.enums.WalletTransactionType;
import org.sec.walletservice.repositories.CurrencyRepository;
import org.sec.walletservice.repositories.WalletRepository;
import org.sec.walletservice.repositories.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GraphQLController {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @QueryMapping
    public List<Wallet> userWallets() {
        return walletRepository.findAll();
    }

    @QueryMapping
    public Wallet getWalletById(@Argument String id) {
        return walletRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("no id %s found ", id)));
    }

    @MutationMapping
    public Wallet addWallet(@Argument AddWalletRequestDto addWalletRequestDto) {
        return walletService.saveWallet(addWalletRequestDto);

    }

    public void transferToWallet(String sourceWalletId, String destinationWalletId, Double amount) {
        Wallet sourceWallet = walletRepository.findById(sourceWalletId).orElseThrow(() ->
                new RuntimeException(String.format("Wallet %s not found", sourceWalletId)));
        Wallet destinationWallet = walletRepository.findById(destinationWalletId).orElseThrow(() ->
                new RuntimeException(String.format("Wallet %s not found", destinationWalletId)));
        WalletTransaction sourceWalletTransaction = WalletTransaction.builder()
                .timestamp(System.currentTimeMillis())
                .walletTransactionType(WalletTransactionType.DEBIT)
                .currencyPurchasePrice(sourceWallet.getCurrency().getPurchasePrice())
                .currencySellPrice(sourceWallet.getCurrency().getSellPrice())
                .amount(amount)
                .wallet(sourceWallet)
                .build();
        walletTransactionRepository.save(sourceWalletTransaction);
        sourceWallet.setBalance(sourceWallet.getBalance() - amount);

        double destinationAmount = destinationWallet.getCurrency().getPurchasePrice() != 0 ?
                amount * (sourceWallet.getCurrency().getSellPrice()
                        / destinationWallet.getCurrency().getPurchasePrice()) : amount;

        WalletTransaction destinationWalletTransaction = WalletTransaction.builder()
                .amount(destinationAmount)
                .timestamp(System.currentTimeMillis())
                .currencyPurchasePrice(destinationWallet.getCurrency().getPurchasePrice())
                .currencySellPrice(destinationWallet.getCurrency().getSellPrice())
                .wallet(destinationWallet)
                .walletTransactionType(WalletTransactionType.CREDIT)
                .build();
        walletTransactionRepository.save(destinationWalletTransaction);
        destinationWallet.setBalance(destinationWallet.getBalance() + destinationAmount);

    }

}
