package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.MoneyTransfer;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transferMoney(MoneyTransfer moneyTransfer) {
    Account accountFrom = this.getAccount(moneyTransfer.getAccountFromId());
    Account accountTo = this.getAccount(moneyTransfer.getAccountToId());
    BigDecimal amount = moneyTransfer.getAmount();

    BigDecimal accountFromFinalBalance = accountFrom.getBalance().subtract(amount);
    BigDecimal accountToFinalBalance = accountTo.getBalance().add(amount);

    if (accountFromFinalBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new InsufficientBalanceException(
              String.format(
                      "Account with id %s has insufficient balance to perform this operation!",
                      moneyTransfer.getAccountFromId())
      );
    }

    accountFrom.setBalance(accountFromFinalBalance);
    accountTo.setBalance(accountToFinalBalance);
  }
}
