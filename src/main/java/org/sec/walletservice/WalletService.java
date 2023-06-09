package org.sec.walletservice;

import org.sec.walletservice.dto.AddWalletRequestDto;
import org.sec.walletservice.entities.Currency;
import org.sec.walletservice.entities.Wallet;
import org.sec.walletservice.entities.WalletTransaction;
import org.sec.walletservice.enums.WalletTransactionType;
import org.sec.walletservice.repositories.CurrencyRepository;
import org.sec.walletservice.repositories.WalletRepository;
import org.sec.walletservice.repositories.WalletTransactionRepository;
import org.sec.walletservice.utils.RandomBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;



@Service
@Transactional
public class WalletService {
    public static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletTransactionRepository walletTransactionRepository;

    @Autowired
    RandomBuilder randomBuilder;

    Random random = new Random();

    public void loadData() throws IOException {
        Path path = Paths.get(new ClassPathResource("currencies.csv").getURI());

        List<String> lines = Files.readAllLines(path);
        IntStream.range(1, lines.size()).forEach(range -> {
            String[] line = lines.get(range).split(";");
            Currency currency = Currency.builder()
                    .code(line[0])
                    .name(line[1])
                    .sellPrice(Double.parseDouble(line[6]))
                    .purchasePrice(Double.parseDouble(line[6]) * 0.9)
                    .build();
            currencyRepository.save(currency);
        });
        Stream.of("MAD", "ROU", "EUR", "USD").forEach(currencyCode -> {
            Currency currencyByCode = currencyRepository.findByCode(currencyCode).orElseThrow(() ->
                    new RuntimeException(String.format("Currency %s not found", currencyCode)));
            //TODO: get userIds from csv
            Wallet wallet = Wallet.builder().build();
            wallet.setCurrency(currencyByCode);
            // generate random balance
            wallet.setBalance(randomBuilder.buildRandomBalance(random));
            wallet.setId(UUID.randomUUID().toString());
            byte[] username = "SAMUEL PEDRO".getBytes(StandardCharsets.UTF_8);
            LOGGER.info("username samuel pedro byte value: {} " , Arrays.toString(username));
            StringBuilder userIdBuilder = new StringBuilder().append("WLT-")
                    .append(UUID.nameUUIDFromBytes(username));
            wallet.setUserId(userIdBuilder.toString());
            walletRepository.save(wallet);
        });
        walletRepository.findAll().forEach(wallet ->
                IntStream.range(0, 10).forEach(i -> {
                    WalletTransaction debitWalletTransaction = WalletTransaction.builder()
                            .amount(random.nextDouble(10) * 1000)
                            .wallet(wallet)
                            .type(WalletTransactionType.DEBIT)
                            .timestamp(System.currentTimeMillis())
                            .build();
                    walletTransactionRepository.save(debitWalletTransaction);
                    wallet.setBalance(wallet.getBalance() - debitWalletTransaction.getAmount());

                    WalletTransaction creditWalletTransaction = WalletTransaction.builder()
                            .amount(random.nextDouble(10) * 1000)
                            .wallet(wallet)
                            .type(WalletTransactionType.CREDIT)
                            .timestamp(System.currentTimeMillis())
                            .build();
                    walletTransactionRepository.save(creditWalletTransaction);
                    wallet.setBalance(wallet.getBalance() + creditWalletTransaction.getAmount());
                    walletRepository.save(wallet);
                }));
    }

    public Wallet saveWallet(AddWalletRequestDto walletRequestDto) {
        Currency currency = currencyRepository.findByCode(walletRequestDto.currencyCode()).orElseThrow(() ->
                new RuntimeException(String.format("no currency %s found", walletRequestDto.currencyCode()))
        );
        Wallet wallet = Wallet.builder().balance(walletRequestDto.balance())
                .id(UUID.randomUUID().toString())
                .userId("userId1")
                .createdAt(System.currentTimeMillis())
                .currency(currency)
                .build();
        return walletRepository.save(wallet);
    }


    public List<WalletTransaction> transferToWallet(String sourceWalletId, String destinationWalletId, Double amount) {
        Wallet sourceWallet = walletRepository.findById(sourceWalletId).orElseThrow(() ->
                new RuntimeException(String.format("Wallet %s not found", sourceWalletId)));
        Wallet destinationWallet = walletRepository.findById(destinationWalletId).orElseThrow(() ->
                new RuntimeException(String.format("Wallet %s not found", destinationWalletId)));
        WalletTransaction sourceWalletTransaction = WalletTransaction.builder()
                .timestamp(System.currentTimeMillis())
                .type(WalletTransactionType.DEBIT)
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
                .type(WalletTransactionType.CREDIT)
                .build();
        walletTransactionRepository.save(destinationWalletTransaction);
        destinationWallet.setBalance(destinationWallet.getBalance() + destinationAmount);
        return Arrays.asList(sourceWalletTransaction, destinationWalletTransaction);
    }
}
