package Model;

import java.time.LocalDate;

public class FinancialStatement {
    private int statementId;
    private int companyId;
    private String statementType;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    // existing fields
    private double totalRevenue;
    private double totalExpense;
    private double netIncome;
    private double assets;
    private double liabilities;
    private double equity;

    // new fields for detailed income statement
    private double costOfGoodsSold;
    private double salesReturns;
    private double salesDiscount;

    // derived
    private double grossProfit;

    // ---- No-arg constructor ----
    public FinancialStatement() {}

    // ---- Full constructor (with COGS / returns / discounts) ----
    public FinancialStatement(int statementId, int companyId, String statementType,
                              LocalDate periodStart, LocalDate periodEnd,
                              double totalRevenue, double totalExpense, double netIncome,
                              double assets, double liabilities, double equity,
                              double costOfGoodsSold, double salesReturns, double salesDiscount) {
        this.statementId = statementId;
        this.companyId = companyId;
        this.statementType = statementType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalRevenue = totalRevenue;
        this.totalExpense = totalExpense;
        this.netIncome = netIncome;
        this.assets = assets;
        this.liabilities = liabilities;
        this.equity = equity;
        this.costOfGoodsSold = costOfGoodsSold;
        this.salesReturns = salesReturns;
        this.salesDiscount = salesDiscount;
        this.grossProfit = calculateGrossProfit();
    }

    // ---- Backwards-compatible constructor (11-arg) ----
    // This is the exact signature the DAO uses in several places.
    public FinancialStatement(int statementId, int companyId, String statementType,
                              LocalDate periodStart, LocalDate periodEnd,
                              double totalRevenue, double totalExpense, double netIncome,
                              double assets, double liabilities, double equity) {
        // call the full constructor with zeroes for the new fields
        this(statementId, companyId, statementType, periodStart, periodEnd,
             totalRevenue, totalExpense, netIncome, assets, liabilities, equity,
             0.0, 0.0, 0.0);
    }

    // ---- Derived calculation ----
    public double calculateGrossProfit() {
        return this.totalRevenue - (this.costOfGoodsSold + this.salesReturns + this.salesDiscount);
    }

    // ---- Getters & Setters ----
    public int getStatementId() { return statementId; }
    public void setStatementId(int statementId) { this.statementId = statementId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getStatementType() { return statementType; }
    public void setStatementType(String statementType) { this.statementType = statementType; }

    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }

    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
        this.grossProfit = calculateGrossProfit();
    }

    public double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(double totalExpense) { this.totalExpense = totalExpense; }

    public double getNetIncome() { return netIncome; }
    public void setNetIncome(double netIncome) { this.netIncome = netIncome; }

    public double getAssets() { return assets; }
    public void setAssets(double assets) { this.assets = assets; }

    public double getLiabilities() { return liabilities; }
    public void setLiabilities(double liabilities) { this.liabilities = liabilities; }

    public double getEquity() { return equity; }
    public void setEquity(double equity) { this.equity = equity; }

    public double getCostOfGoodsSold() { return costOfGoodsSold; }
    public void setCostOfGoodsSold(double costOfGoodsSold) {
        this.costOfGoodsSold = costOfGoodsSold;
        this.grossProfit = calculateGrossProfit();
    }

    // plural and singular alias getters to match usages in DAO/Form
    public double getSalesReturns() { return salesReturns; }
    public double getSalesReturn() { return salesReturns; } // alias

    public void setSalesReturns(double salesReturns) {
        this.salesReturns = salesReturns;
        this.grossProfit = calculateGrossProfit();
    }

    public double getSalesDiscount() { return salesDiscount; }
    public double getSalesDiscounts() { return salesDiscount; } // alias

    public void setSalesDiscount(double salesDiscount) {
        this.salesDiscount = salesDiscount;
        this.grossProfit = calculateGrossProfit();
    }

    public double getGrossProfit() { return grossProfit; }
    public void setGrossProfit(double grossProfit) { this.grossProfit = grossProfit; } // rarely needed
}
