package org.sec.walletservice.web;

import org.sec.walletservice.entities.Wallet;
import org.sec.walletservice.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GraphQLController {
    @Autowired
    private WalletRepository walletRepository;

    @QueryMapping
    public List<Wallet> userWallets(){
        return walletRepository.findAll();
    }
}
