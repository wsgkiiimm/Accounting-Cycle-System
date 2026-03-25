package Model;

import java.time.LocalDate;

public class GeneralLedger {
    private int ledgerId;
    private int companyId;
    private String accountName;
    private LocalDate transactionDate;
    private double debit;
    private double credit;
    private double balance;

    public GeneralLedger() {}

    public GeneralLedger(int ledgerId, int companyId, String accountName, LocalDate transactionDate,
                         double debit, double credit, double balance) {
        this.ledgerId = ledgerId;
        this.companyId = companyId;
        this.accountName = accountName;
        this.transactionDate = transactionDate;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
    }

    public int getLedgerId() { return ledgerId; }
    public void setLedgerId(int ledgerId) { this.ledgerId = ledgerId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public double getDebit() { return debit; }
    public void setDebit(double debit) { this.debit = debit; }

    public double getCredit() { return credit; }
    public void setCredit(double credit) { this.credit = credit; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
