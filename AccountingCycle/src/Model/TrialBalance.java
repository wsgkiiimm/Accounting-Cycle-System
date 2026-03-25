package Model;

public class TrialBalance {
    private int trialId;
    private int companyId;
    private String accountName;
    private double debitTotal;
    private double creditTotal;

    public TrialBalance() {}

    public TrialBalance(int trialId, int companyId, String accountName, double debitTotal, double creditTotal) {
        this.trialId = trialId;
        this.companyId = companyId;
        this.accountName = accountName;
        this.debitTotal = debitTotal;
        this.creditTotal = creditTotal;
    }

    // Getters and Setters
    public int getTrialId() { return trialId; }
    public void setTrialId(int trialId) { this.trialId = trialId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public double getDebitTotal() { return debitTotal; }
    public void setDebitTotal(double debitTotal) { this.debitTotal = debitTotal; }

    public double getCreditTotal() { return creditTotal; }
    public void setCreditTotal(double creditTotal) { this.creditTotal = creditTotal; }
}
