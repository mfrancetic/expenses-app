package com.mfrancetic.expensesapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mfrancetic.expensesapp.ExpensesListViewModel
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import java.text.SimpleDateFormat
import java.util.*

// region UI

@Composable
fun ExpensesListScreen(
    viewModel: ExpensesListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navigateToExpensesDetailScreen: () -> Unit
) {
    val expenses = viewModel.container.stateFlow.collectAsState().value.expenses

    Scaffold(topBar = {
        TopAppBar {
            Text(
                text = stringResource(id = R.string.expenses_list_header)
            )
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navigateToExpensesDetailScreen.invoke() }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.expenses_list_add_expense_button)
            )
        }
    }, floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(expenses) { expense ->
                ExpenseCard(expense = expense)
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense, modifier: Modifier = Modifier) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    imageVector = Icons.Filled.ShoppingCart,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onSurface),
                    contentDescription = null
                )
                Text(modifier = Modifier.padding(horizontal = 8.dp), text = expense.title)
                Text(text = expense.amount)
            }
            Text(text = expense.category.name)
            Text(text = SimpleDateFormat.getDateInstance().format(expense.date))
        }
    }
}

// endregion

// region Preview

@Preview("ExpensesListScreen light mode", showBackground = true)
@Preview("ExpensesListScreen dark mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpensesListScreenPreview() {
    ExpensesAppTheme {
        ExpensesListScreen(navigateToExpensesDetailScreen = {})
    }
}

@Preview("ExpenseCard light mode", showBackground = true)
@Preview("ExpensesCard dark mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpenseCardPreview() {
    ExpensesAppTheme {
        ExpenseCard(
            expense = Expense(
                id = "1",
                title = "Groceries",
                amount = "12.24€",
                category = ExpenseCategory.Entertainment,
                date = Calendar.getInstance().timeInMillis
            )
        )
    }
}

// endregion