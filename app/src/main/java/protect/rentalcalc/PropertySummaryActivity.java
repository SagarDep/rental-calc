package protect.rentalcalc;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Map;

public class PropertySummaryActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_summary_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DBHelper db = new DBHelper(this);

        final Bundle b = getIntent().getExtras();

        if (b == null || b.containsKey("id") == false)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final long propertyId = b.getLong("id");
        Property property = db.getProperty(propertyId);

        if (property == null)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView priceValue = (TextView)findViewById(R.id.priceValue);
        TextView financedValue = (TextView)findViewById(R.id.financedValue);
        TextView downPaymentValue = (TextView)findViewById(R.id.downPaymentValue);
        TextView purchaseCostsValue = (TextView)findViewById(R.id.purchaseCostsValue);
        TextView repairRemodelCostsValue = (TextView)findViewById(R.id.repairRemodelCostsValue);
        TextView totalCashNeededValue = (TextView)findViewById(R.id.totalCashNeededValue);
        TextView pricePerSizeValue = (TextView)findViewById(R.id.pricePerSizeValue);
        TextView rentValue = (TextView)findViewById(R.id.rentValue);
        TextView vancancyValue = (TextView)findViewById(R.id.vancancyValue);
        TextView operatingIncomeValue = (TextView)findViewById(R.id.operatingIncomeValue);
        TextView operatingExpensesValue = (TextView)findViewById(R.id.operatingExpensesValue);
        TextView netOperatingIncomeValue = (TextView)findViewById(R.id.netOperatingIncomeValue);
        TextView mortgageValue = (TextView)findViewById(R.id.mortgageValue);
        TextView cashFlowValue = (TextView)findViewById(R.id.cashFlowValue);
        TextView capitalizationRateValue = (TextView)findViewById(R.id.capitalizationRateValue);
        TextView cashOnCashValue = (TextView)findViewById(R.id.cashOnCashValue);
        TextView rentToValueValue = (TextView)findViewById(R.id.rentToValueValue);
        TextView grossRentMultiplierValue = (TextView)findViewById(R.id.grossRentMultiplierValue);


        // PURCAHSE COST

        priceValue.setText(String.format(Locale.US, "%d", property.purchasePrice));

        double downPercent;
        if(property.useLoan)
        {
            downPercent = ((double)property.downPayment)/100.0;
        }
        else
        {
            downPercent = 1.0;
        }

        double financedPercent = 1.0 - downPercent;
        double financed = property.purchasePrice * financedPercent;
        financedValue.setText(String.format(Locale.US, "%d", Math.round(financed)));

        double downPayment = downPercent * (double)property.purchasePrice;
        downPaymentValue.setText(String.format(Locale.US, "%d", Math.round(downPayment)));

        double purchaseCostPercent = ((double)property.purchaseCosts/100.0);
        double purchaseCost = purchaseCostPercent * (double)property.purchasePrice;
        purchaseCostsValue.setText(String.format(Locale.US, "%d", Math.round(purchaseCost)));

        repairRemodelCostsValue.setText(String.format(Locale.US, "%d", property.repairRemodelCosts));

        double totalCashNeeded = downPayment + purchaseCost + property.repairRemodelCosts;
        totalCashNeededValue.setText(String.format(Locale.US, "%d", Math.round(totalCashNeeded)));


        // VALUATION

        double pricePerSqft = 0;
        if(property.propertySqft > 0)
        {
            pricePerSqft = (double)property.purchasePrice / (double)property.propertySqft;
        }
        pricePerSizeValue.setText(String.format(Locale.US, "%d", Math.round(pricePerSqft)));


        // OPERATION

        rentValue.setText(String.format(Locale.US, "%d", property.grossRent));

        double vacancyRate = (double)property.vacancy / 100.0;
        double vacancy = property.grossRent * vacancyRate;
        vancancyValue.setText(String.format(Locale.US, "%d", Math.round(vacancy)));

        double operatingIncome = property.grossRent - vacancy;
        operatingIncomeValue.setText(String.format(Locale.US, "%d", Math.round(operatingIncome)));

        double expensesPercent = (double)property.expenses / 100.0;
        double operatingExpenses = property.grossRent * expensesPercent;
        operatingExpensesValue.setText(String.format(Locale.US, "%d", Math.round(operatingExpenses)));

        double netOperatingIncome = operatingIncome - operatingExpenses;
        netOperatingIncomeValue.setText(String.format(Locale.US, "%d", Math.round(netOperatingIncome)));

        double monthlyInterestRate = property.interestRate / 100.0 / 12;
        int paymentMonths = property.loanDuration * 12;
        double onePlusRateRaised = Math.pow(1 + monthlyInterestRate, paymentMonths);
        double mortgage = financed * (monthlyInterestRate * onePlusRateRaised) / (onePlusRateRaised - 1);
        mortgageValue.setText(String.format(Locale.US, "%d", Math.round(mortgage)));

        double cashFlow = netOperatingIncome - mortgage;
        cashFlowValue.setText(String.format(Locale.US, "%d", Math.round(cashFlow)));


        // RETURNS

        double yearlyNetOperatingIncome = netOperatingIncome * 12;
        double capitalizationRate = 0;
        if(property.purchasePrice > 0)
        {
            capitalizationRate = yearlyNetOperatingIncome * 100.0 / (double)property.purchasePrice;
        }
        capitalizationRateValue.setText(String.format(Locale.US, "%.1f", capitalizationRate));

        double yearlyCashFlow = cashFlow * 12;
        double cashOnCashRate = 0;
        if(totalCashNeeded > 0)
        {
            cashOnCashRate = yearlyCashFlow * 100.0 / (double)totalCashNeeded;
        }
        cashOnCashValue.setText(String.format(Locale.US, "%.1f", cashOnCashRate));

        double rentToValue = 0;
        if(property.purchasePrice > 0)
        {
            rentToValue = property.grossRent * 100.0 / property.purchasePrice;
        }
        rentToValueValue.setText(String.format(Locale.US, "%.1f", rentToValue));

        double grossRentMultiplier = 0;
        if(property.grossRent > 0)
        {
            grossRentMultiplier = (double)property.purchasePrice / (property.grossRent * 12.0);
        }
        grossRentMultiplierValue.setText(String.format(Locale.US, "%.1f", grossRentMultiplier));


        // Setup help texts
        Map<Integer, DictionaryItem> dictionaryLookups = new ImmutableMap.Builder<Integer, DictionaryItem>()
            .put(R.id.purchasePriceHelp, new DictionaryItem(R.string.purchasePriceHelpTitle, R.string.purchasePriceDefinition))
            .put(R.id.purchaseCostsHelp, new DictionaryItem(R.string.purchaseCostsHelpTitle, R.string.purchaseCostsDefinition))
            .put(R.id.repairRemodelCostsHelp, new DictionaryItem(R.string.repairRemodelHelpTitle, R.string.repairRemodelDefinition))
            .put(R.id.totalCashNeededHelp, new DictionaryItem(R.string.totalCashNeededTitle, R.string.totalCashNeededDefinition, R.string.totalCashNeededFormula))
            .put(R.id.grossRentHelp, new DictionaryItem(R.string.grossRentHelpTitle, R.string.grossRentDefinition))
            .put(R.id.vacancyHelp, new DictionaryItem(R.string.vacancyHelpTitle, R.string.vacancyDefinition, R.string.vacancyFormula))
            .put(R.id.operatingIncomeHelp, new DictionaryItem(R.string.operatingIncomeHelpTitle, R.string.operatingIncomeDefinition, R.string.operatingIncomeFormula))
            .put(R.id.operatingExpensesHelp, new DictionaryItem(R.string.operatingExpensesHelpTitle, R.string.operatingExpensesDefinition))
            .put(R.id.netOperatingIncomeHelp, new DictionaryItem(R.string.netOperatingIncomeHelpTitle, R.string.netOperatingIncomeDefinition, R.string.netOperatingIncomeFormula))
            .put(R.id.cashFlowHelp, new DictionaryItem(R.string.cashFlowHelpTitle, R.string.cashFlowDefinition, R.string.cashFlowFormula))
            .put(R.id.capitalizationRateHelp, new DictionaryItem(R.string.capitalizationRateHelpTitle, R.string.capitalizationRateDefinition, R.string.capitalizationRateFormula))
            .put(R.id.cashOnCashHelp, new DictionaryItem(R.string.cashOnCashHelpTitle, R.string.cashOnCashDefinition, R.string.cashOnCashFormula))
            .put(R.id.rentToValueHelp, new DictionaryItem(R.string.rentToValueHelpTitle, R.string.rentToValueDefinition, R.string.rentToValueFormula))
            .put(R.id.grossRentMultiplierHelp, new DictionaryItem(R.string.grossRentMultiplierHelpTitle, R.string.grossRentMultiplierDefinition, R.string.grossRentMultiplierFormula))
            .build();

        for(final Map.Entry<Integer, DictionaryItem> entry : dictionaryLookups.entrySet())
        {
            View view = findViewById(entry.getKey());
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DictionaryItem info = entry.getValue();
                    final Bundle bundle = new Bundle();
                    bundle.putInt("title", info.titleId);
                    bundle.putInt("definition", info.definitionId);
                    if(info.formulaId != null)
                    {
                        bundle.putInt("formula", info.formulaId);
                    }
                    Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
