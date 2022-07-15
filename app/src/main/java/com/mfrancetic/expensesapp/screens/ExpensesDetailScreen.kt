package com.mfrancetic.expensesapp.screens

import android.app.DatePickerDialog
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ExpensesDetailScreen(
    expense: Expense,
    onExpenseUpdated: (Expense) -> Unit,
    onUpButtonClicked: () -> Unit,
    onSaveButtonClicked: (Expense) -> Unit,
) {
    Scaffold(
        topBar = {
            ExpensesDetailScreenTopAppBar(
                onUpButtonClicked =
                { onUpButtonClicked.invoke() }
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                var newExpense by rememberSaveable {
                    mutableStateOf(expense)
                }

                ExpensesDetailTitleTextField(
                    title = newExpense.title,
                    onTitleUpdated = { newTitle ->
                        newExpense = newExpense.copy(title = newTitle)
                    })

                ExpensesDetailAmountTextField(
                    amount = newExpense.amount,
                    onAmountUpdated = { newAmount ->
                        val decimalPlaces = newAmount.substringAfter(",", "").length
                        if (decimalPlaces <= 2) {
                            newExpense = newExpense.copy(amount = newAmount)
                        }
                    })

                ExpensesDetailCategoryTextField(
                    category = newExpense.category,
                    onCategoryUpdated = { newCategory ->
                        newExpense = newExpense.copy(category = newCategory)
                    })

                ExpensesDetailDateTextField(
                    date = newExpense.date,
                    onDateUpdated = { newDate ->
                        newExpense = newExpense.copy(
                            date = newDate
                        )
                    })

                ExpensesDetailScreenSaveButton(onSaveButtonClicked = {
                    onSaveButtonClicked.invoke(newExpense)
                })
            }
        }
    }
}

@Composable
fun ExpensesDetailScreenTopAppBar(onUpButtonClicked: () -> Unit) {
    TopAppBar {
        Icon(imageVector =
        Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.expenses_details_up_button),
            modifier = Modifier.clickable {
                onUpButtonClicked()
            })
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(id = R.string.expenses_details_header)
        )
    }
}

@Composable
fun ExpensesDetailTitleTextField(title: String, onTitleUpdated: (String) -> Unit) {
    TextField(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
        value = title, onValueChange = {
            onTitleUpdated(it)
        },
        label = {
            Text(text = stringResource(id = R.string.expenses_details_title))
        },
        placeholder = {
            Text(text = stringResource(id = R.string.expenses_details_title_placeholder))
        }
    )
}

@Composable
fun ExpensesDetailAmountTextField(amount: String, onAmountUpdated: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        value = amount, onValueChange = {
            onAmountUpdated(it)
        },
        label = {
            Text(text = stringResource(id = R.string.expenses_details_amount))
        },
        placeholder = {
            Text(text = stringResource(id = R.string.expenses_details_amount_placeholder))
        },
        trailingIcon = {
            Text(text = "€")
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun ExpensesDetailCategoryTextField(
    category: ExpenseCategory,
    onCategoryUpdated: (ExpenseCategory) -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    val icon =
        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val items = ExpenseCategory.values()

    Column {
        TextField(
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            value = category.name, onValueChange = {
                onCategoryUpdated(ExpenseCategory.valueOf(it))
            },
            label = { Text(text = stringResource(id = R.string.expenses_details_category)) },
            trailingIcon = {
                Icon(imageVector = icon, contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded })
            }
        )

        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { expenseCategory ->
                DropdownMenuItem(onClick = {
                    onCategoryUpdated(expenseCategory)
                    expanded = false
                }) {
                    Text(expenseCategory.name)
                }
            }
        }
    }
}

@Composable
fun ExpensesDetailDateTextField(date: Long, onDateUpdated: (Long) -> Unit) {
    val context = LocalContext.current
    val currentDate = System.currentTimeMillis()
    val calendar = Calendar.getInstance()

    TextField(
        readOnly = true,
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val datePicker = DatePickerDialog(context)
                datePicker
                    .setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        calendar.set(year, monthOfYear, dayOfMonth)
                        onDateUpdated(calendar.timeInMillis)
                    }
                datePicker.show()
            },
        value = SimpleDateFormat.getDateInstance().format(date), onValueChange = {
        },
        label = {
            Text(text = stringResource(id = R.string.expenses_details_date))
        },
        placeholder = {
            Text(
                text = SimpleDateFormat
                    .getDateInstance()
                    .format(currentDate)
            )
        },
        trailingIcon = {
            Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = null)
        }
    )
}

@Composable
fun ExpensesDetailScreenSaveButton(onSaveButtonClicked: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onSaveButtonClicked()
        }) {
        Text(text = stringResource(id = R.string.expenses_details_save))
    }
}

// region Preview

@Preview("ExpensesDetailScreen Preview", showBackground = true)
@Preview("ExpensesDetailScreen Preview", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpensesDetailScreenPreview() {
    ExpensesAppTheme {
        ExpensesDetailScreen(expense = Expense(
            "0", "title", "10.00", ExpenseCategory.Rent,
            100L
        ),
            onExpenseUpdated = {}, onUpButtonClicked = {}, onSaveButtonClicked = {})
    }
}

// endregion