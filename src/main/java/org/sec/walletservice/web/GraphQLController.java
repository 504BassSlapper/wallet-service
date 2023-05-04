package org.sec.walletservice.web;

import org.sec.walletservice.WalletService;
import org.sec.walletservice.dto.AddWalletRequestDto;
import org.sec.walletservice.entities.Wallet;
import org.sec.walletservice.entities.WalletTransaction;
import org.sec.walletservice.repositories.WalletRepository;
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

    @MutationMapping
    public List<WalletTransaction> transfertToWallet(@Argument String sourceWalletId,
                                                     @Argument String destinationWalletId,
                                                     @Argument Double amount) {
        return walletService.transferToWallet(sourceWalletId, destinationWalletId, amount);
    }

}
