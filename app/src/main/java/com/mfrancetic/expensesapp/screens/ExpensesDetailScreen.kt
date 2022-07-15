package com.mfrancetic.expensesapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mfrancetic.expensesapp.ExpensesViewModel
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpensesSideEffect
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import java.text.SimpleDateFormat
import java.util.*

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            val calendar = Calendar.getInstance()
            val currentDate = calendar.time

            val expense = Expense(
                id = UUID.randomUUID().toString(),
                title = "",
                amount = "0.00",
                category = "",
                date = SimpleDateFormat.getDateInstance().format(currentDate)
            )

            var title by rememberSaveable {
                mutableStateOf(expense.title)
            }
            TextField(
                value = title, onValueChange = {
                    title = it
                },
                label = {
                    Text(text = stringResource(id = R.string.expenses_details_title))
                }
            )

            var amount by rememberSaveable {
                mutableStateOf(expense.amount)
            }
            TextField(
                value = amount, onValueChange = {
                    amount = it
                },
                label = {
                    Text(text = stringResource(id = R.string.expenses_details_amount))
                },
                trailingIcon = {
                    Text(text = "€")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            var category by rememberSaveable {
                mutableStateOf(expense.category)
            }
            TextField(
                value = category, onValueChange = {
                    category = it
                },
                label = {
                    Text(text = stringResource(id = R.string.expenses_details_category))
                }
            )

            var date by rememberSaveable {
                mutableStateOf(
                    SimpleDateFormat.getDateInstance().format(currentDate)

                )
            }
            TextField(
                value = date, onValueChange = {
                    date = it
                },
                label = {
                    Text(text = stringResource(id = R.string.expenses_details_date))
                }
            )
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                onSaveButtonClicked.invoke(
                    expense.copy(
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