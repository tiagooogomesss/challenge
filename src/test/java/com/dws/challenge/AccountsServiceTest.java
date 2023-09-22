package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Set;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.MoneyTransfer;
import com.dws.challenge.exception.AccountDoesNotExistException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @AfterEach
  void clearData() {
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  void getAccount_failsOnNonExistentAccountId() {
    String nonExistentAccountId = "nonExistentAccountId";

    try {
      this.accountsService.getAccount(nonExistentAccountId);
      fail("Should have failed when getting a non existent account");
    } catch (AccountDoesNotExistException e) {
      assertThat(e.getMessage()).isEqualTo("Account with id " + nonExistentAccountId + " does not exist!");
    }
  }

  @Test
  void transferMoney() {
    String accountFromId = "1";
    String accountToId = "2";
    BigDecimal accountFromBalance = BigDecimal.valueOf(10);
    BigDecimal accountToBalance = BigDecimal.valueOf(10);
    Account accountFrom = new Account(accountFromId, accountFromBalance);
    Account accountTo = new Account(accountToId, accountToBalance);
    BigDecimal amount = BigDecimal.valueOf(10);
    MoneyTransfer moneyTransfer = new MoneyTransfer(
            accountFromId, accountToId, amount
    );

    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);

    this.accountsService.transferMoney(moneyTransfer);

    assertThat(this.accountsService.getAccount(accountFromId).getBalance()).isEqualTo(BigDecimal.valueOf(0));
    assertThat(this.accountsService.getAccount(accountToId).getBalance()).isEqualTo(BigDecimal.valueOf(20));
  }

  @Test
  void transferMoney_failsOnNonExistentAccountId() {
    String nonExistentAccountId = "nonExistentAccountId";
    String accountToId = "1";
    Account accountTo = new Account(accountToId, BigDecimal.valueOf(10));
    BigDecimal amount = BigDecimal.valueOf(10);
    MoneyTransfer moneyTransfer = new MoneyTransfer(
            nonExistentAccountId, accountToId, amount
    );

    this.accountsService.createAccount(accountTo);

    try {
      this.accountsService.transferMoney(moneyTransfer);
      fail("Should have failed when getting a non existent account");
    } catch (AccountDoesNotExistException e) {
      assertThat(e.getMessage()).isEqualTo("Account with id " + nonExistentAccountId + " does not exist!");
    }
  }

  @Test
  void transferMoney_failsOnInsufficientAccountFromBalance() {
    String accountFromId = "1";
    String accountToId = "2";
    BigDecimal accountFromBalance = BigDecimal.valueOf(0);
    BigDecimal accountToBalance = BigDecimal.valueOf(10);
    Account accountFrom = new Account(accountFromId, accountFromBalance);
    Account accountTo = new Account(accountToId, accountToBalance);
    BigDecimal amount = BigDecimal.valueOf(10);
    MoneyTransfer moneyTransfer = new MoneyTransfer(
            accountFromId, accountToId, amount
    );

    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);

    try {
      this.accountsService.transferMoney(moneyTransfer);
      fail("Should have failed when getting a non existent account");
    } catch (InsufficientBalanceException e) {
      assertThat(e.getMessage()).isEqualTo("Account with id " + accountFromId + " has insufficient balance to perform this operation!");
    }
  }
}
