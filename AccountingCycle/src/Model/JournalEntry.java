package Model;

public class JournalEntry {
    private int journalId;
    private int companyId;
    private String entryDate;
    private String description;
    private String debitAccount;
    private String creditAccount;
    private double debitAmount;
    private double creditAmount;

    public JournalEntry() {}

    public JournalEntry(int journalId, int companyId, String entryDate, String description,
                        String debitAccount, String creditAccount, double debitAmount, double creditAmount) {
        this.journalId = journalId;
        this.companyId = companyId;
        this.entryDate = entryDate;
        this.description = description;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
    }

    // Getters and Setters
    public int getJournalId() { return journalId; }
    public void setJournalId(int journalId) { this.journalId = journalId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getEntryDate() { return entryDate; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDebitAccount() { return debitAccount; }
    public void setDebitAccount(String debitAccount) { this.debitAccount = debitAccount; }

    public String getCreditAccount() { return creditAccount; }
    public void setCreditAccount(String creditAccount) { this.creditAccount = creditAccount; }

    public double getDebitAmount() { return debitAmount; }
    public void setDebitAmount(double debitAmount) { this.debitAmount = debitAmount; }

    public double getCreditAmount() { return creditAmount; }
    public void setCreditAmount(double creditAmount) { this.creditAmount = creditAmount; }
}
