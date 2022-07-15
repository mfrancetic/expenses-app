package com.mfrancetic.expensesapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mfrancetic.expensesapp.ExpensesViewModel
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpensesSideEffect
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

@Composable
fun ExpensesDetailScreen(
    viewModel: ExpensesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onUpButtonClicked: () -> Unit,
    onSaveButtonClicked: (Expense) -> Unit,

    ) {
    LaunchedEffect(true) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                ExpensesSideEffect.NavigateFromExpensesDetailsToExpensesList ->
                    onUpButtonClicked.invoke()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar {
                Icon(imageVector =
                Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.expenses_details_up_button),
                    modifier = Modifier.clickable {
                        onUpButtonClicked.invoke()
                    })
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(id = R.string.expenses_details_header)
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                var title by rememberSaveable {
                    mutableStateOf("")
                }
                TextField(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                    value = title, onValueChange = {
                        title = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.expenses_details_title))
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.expenses_details_title_placeholder))
                    }
                )

                var amount by rememberSaveable {
                    mutableStateOf("")
                }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    value = amount, onValueChange = {
                        val decimalPlaces = it.substringAfter(",", "").length
                        if (decimalPlaces <= 2) {
                            amount = it
                        }
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

                var category by rememberSaveable {
                    mutableStateOf(ExpenseCategory.Rent)
                }
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
                            category = ExpenseCategory.valueOf(it)
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
                                category = expenseCategory
                                expanded = false
                            }) {
                                Text(expenseCategory.name)
                            }
                        }
                    }
                }

                val currentDate = Calendar.getInstance().time
                var date by rememberSaveable {
                    mutableStateOf("")
                }
                TextField(
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    value = date, onValueChange = {
                        date = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.expenses_details_date))
                    },
                    placeholder = {
                        Text(text = SimpleDateFormat.getDateInstance().format(currentDate))
                    }
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSaveButtonClicked.invoke(
                            Expense(
                                id = UUID.randomUUID().toString(),
                                title = title, amount = amount, category = category,
                                date = date
                            )
                        )
                    }) {
                    Text(text = stringResource(id = R.string.expenses_details_save))
                }
            }
        }
    }
}

// region Preview

@Preview("ExpensesDetailScreen Preview", showBackground = true)
@Preview("ExpensesDetailScreen Preview", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpensesDetailScreenPreview() {
    ExpensesAppTheme {
        ExpensesDetailScreen(onUpButtonClicked = {}, onSaveButtonClicked = {})
    }
}

// endregion