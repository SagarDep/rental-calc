package protect.rentalcalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PropertyWorksheetActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";
    private DBHelper _db;

    private EditText _price;
    private EditText _afterRepairsValue;
    private ToggleButton _financing;
    private EditText _downPayment;
    private EditText _interestRate;
    private EditText _loanDuration;
    private EditText _purchaseCost;
    private EditText _repairCost;
    private EditText _rent;
    private EditText _otherIncome;
    private EditText _totalExpenses;
    private EditText _vacancy;
    private EditText _appreciation;
    private EditText _incomeIncrease;
    private EditText _expensesIncrease;
    private EditText _sellingCosts;
    private EditText _landValue;
    private EditText _incomeTaxRate;

    private Property _property;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_worksheet_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        _db = new DBHelper(this);

        final Bundle b = getIntent().getExtras();

        if(b == null || b.containsKey("id") == false)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final long propertyId = b.getLong("id");
        _property = _db.getProperty(propertyId);

        if(_property == null)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        _price = (EditText)findViewById(R.id.price);
        _afterRepairsValue = (EditText)findViewById(R.id.afterRepairsValue);
        _financing = (ToggleButton)findViewById(R.id.financing);
        _downPayment = (EditText)findViewById(R.id.downPayment);
        _interestRate = (EditText)findViewById(R.id.interestRate);
        _loanDuration = (EditText)findViewById(R.id.loanDuration);
        _purchaseCost = (EditText)findViewById(R.id.purchaseCost);
        _repairCost = (EditText)findViewById(R.id.repairCost);
        _rent = (EditText)findViewById(R.id.rent);
        _otherIncome = (EditText)findViewById(R.id.otherIncome);
        _totalExpenses = (EditText)findViewById(R.id.totalExpenses);
        _vacancy = (EditText)findViewById(R.id.vacancy);
        _appreciation = (EditText)findViewById(R.id.appreciation);
        _incomeIncrease = (EditText)findViewById(R.id.incomeIncrease);
        _expensesIncrease = (EditText)findViewById(R.id.expensesIncrease);
        _sellingCosts = (EditText)findViewById(R.id.sellingCosts);
        _landValue = (EditText)findViewById(R.id.landValue);
        _incomeTaxRate = (EditText)findViewById(R.id.incomeTaxRate);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                List<View> views = ImmutableList.of(
                    findViewById(R.id.loanBorder),
                    findViewById(R.id.loanRow),
                    findViewById(R.id.interestBorder),
                    findViewById(R.id.interestRow),
                    findViewById(R.id.downPaymentBorder),
                    findViewById(R.id.downPaymentRow)
                );

                for(View view : views)
                {
                    view.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
            }
        };
        _financing.setOnCheckedChangeListener(listener);



        _price.setText(String.format(Locale.US, "%d", _property.purchasePrice));
        _afterRepairsValue.setText(String.format(Locale.US, "%d", _property.afterRepairsValue));
        _financing.setChecked(_property.useLoan);
        listener.onCheckedChanged(_financing, _property.useLoan);
        _downPayment.setText(String.format(Locale.US, "%d", _property.downPayment));
        _interestRate.setText(String.format(Locale.US, "%.3f", _property.interestRate));
        _loanDuration.setText(String.format(Locale.US, "%d", _property.loanDuration));
        _purchaseCost.setText(String.format(Locale.US, "%d", _property.purchaseCosts));
        _repairCost.setText(String.format(Locale.US, "%d", _property.repairRemodelCosts));
        _rent.setText(String.format(Locale.US, "%d", _property.grossRent));
        _otherIncome.setText(String.format(Locale.US, "%d", _property.otherIncome));
        _totalExpenses.setText(String.format(Locale.US, "%d", _property.expenses));
        _vacancy.setText(String.format(Locale.US, "%d", _property.vacancy));
        _appreciation.setText(String.format(Locale.US, "%d", _property.appreciation));
        _incomeIncrease.setText(String.format(Locale.US, "%d", _property.incomeIncrease));
        _expensesIncrease.setText(String.format(Locale.US, "%d", _property.expenseIncrease));
        _sellingCosts.setText(String.format(Locale.US, "%d", _property.sellingCosts));
        _landValue.setText(String.format(Locale.US, "%d", _property.landValue));
        _incomeTaxRate.setText(String.format(Locale.US, "%d", _property.incomeTaxRate));

        // Setup help texts
        Map<Integer, DictionaryItem> dictionaryLookups = new ImmutableMap.Builder<Integer, DictionaryItem>()
            .put(R.id.purchasePriceHelp, new DictionaryItem(R.string.purchasePriceHelpTitle, R.string.purchasePriceDefinition))
            .put(R.id.afterRepairsHelp, new DictionaryItem(R.string.afterRepairsValueHelpTitle, R.string.afterRepairsValueDefinition))
            .put(R.id.purchaseCostsHelp, new DictionaryItem(R.string.purchaseCostsHelpTitle, R.string.purchaseCostsDefinition))
            .put(R.id.repairRemodelCostsHelp, new DictionaryItem(R.string.repairRemodelHelpTitle, R.string.repairRemodelDefinition))
            .put(R.id.grossRentHelp, new DictionaryItem(R.string.grossRentHelpTitle, R.string.grossRentDefinition))
            .put(R.id.otherIncomeHelp, new DictionaryItem(R.string.otherIncomeHelpTitle, R.string.otherIncomeDefinition))
            .put(R.id.expensesHelp, new DictionaryItem(R.string.operatingExpensesHelpTitle, R.string.operatingExpensesDefinition))
            .put(R.id.vacancyHelp, new DictionaryItem(R.string.vacancyHelpTitle, R.string.vacancyDefinition, R.string.vacancyFormula))
            .put(R.id.appreciationHelp, new DictionaryItem(R.string.appreciationHelpTitle, R.string.appreciationDefinition))
            .put(R.id.incomeIncreaseHelp, new DictionaryItem(R.string.incomeIncreaseHelpTitle, R.string.incomeIncreaseDefinition))
            .put(R.id.expensesIncreaseHelp, new DictionaryItem(R.string.expensesIncreaseHelpTitle, R.string.expensesIncreaseDefinition))
            .put(R.id.sellingCostsHelp, new DictionaryItem(R.string.sellingCostsHelpTitle, R.string.sellingCostsDefinition))
            .put(R.id.landValueHelp, new DictionaryItem(R.string.landValueHelpTitle, R.string.landValueDefinition))
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

    private void doSave()
    {
        Property updatedProperty = new Property(_property);
        updatedProperty.purchasePrice = extractInt(_price, 0);
        updatedProperty.afterRepairsValue = extractInt(_afterRepairsValue, 0);
        updatedProperty.useLoan = _financing.isChecked();
        updatedProperty.downPayment = extractInt(_downPayment, 0);
        updatedProperty.interestRate = extractDouble(_interestRate, 0);
        updatedProperty.loanDuration = extractInt(_loanDuration, 0);
        updatedProperty.purchaseCosts = extractInt(_purchaseCost, 0);
        updatedProperty.repairRemodelCosts = extractInt(_repairCost, 0);
        updatedProperty.grossRent = extractInt(_rent, 0);
        updatedProperty.otherIncome = extractInt(_otherIncome, 0);
        updatedProperty.expenses = extractInt(_totalExpenses, 0);
        updatedProperty.vacancy = extractInt(_vacancy, 0);
        updatedProperty.appreciation = extractInt(_appreciation, 0);
        updatedProperty.incomeIncrease = extractInt(_incomeIncrease, 0);
        updatedProperty.expenseIncrease = extractInt(_expensesIncrease, 0);
        updatedProperty.sellingCosts = extractInt(_sellingCosts, 0);
        updatedProperty.landValue = extractInt(_landValue, 0);
        updatedProperty.incomeTaxRate = extractInt(_incomeTaxRate, 0);

        Log.i(TAG, "Updating property " + updatedProperty.id);
        _db.updateProperty(updatedProperty);

        finish();
    }

    private int extractInt(EditText view, int defaultValue)
    {
        String string = view.getText().toString();
        if(string.isEmpty() == false)
        {
            try
            {
                return Integer.parseInt(string);
            }
            catch(NumberFormatException e)
            {
                Log.w(TAG, "Failed to parse " + string, e);
            }
        }

        return defaultValue;
    }

    private double extractDouble(EditText view, double defaultValue)
    {
        String string = view.getText().toString();
        if(string.isEmpty() == false)
        {
            try
            {
                return Double.parseDouble(string);
            }
            catch(NumberFormatException e)
            {
                Log.w(TAG, "Failed to parse " + string, e);
            }
        }

        return defaultValue;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
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

        if(id == R.id.action_save)
        {
            doSave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        _db.close();
        super.onDestroy();
    }
}
