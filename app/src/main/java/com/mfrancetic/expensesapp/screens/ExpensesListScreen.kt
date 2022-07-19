package com.mfrancetic.expensesapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mfrancetic.expensesapp.ExpensesListViewModel
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpensesListSideEffect
import com.mfrancetic.expensesapp.models.SortMode
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import java.text.SimpleDateFormat
import java.util.*

// region UI

@Composable
fun ExpensesListScreen(
    viewModel: ExpensesListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onEditExpenseButtonClicked: (Expense) -> Unit,
    onDeleteExpenseButtonClicked: (Expense) -> Unit,
    onSortModeUpdated: (SortMode) -> Unit,
    navigateToExpensesDetailScreen: () -> Unit
) {
    val context = LocalContext.current
    val expenses = viewModel.container.stateFlow.collectAsState().value.expenses
    val expenseDeletedSuccessMessage =
        stringResource(id = R.string.expenses_list_expense_deleted_success)
    val expenseDeletedFailureMessage =
        stringResource(id = R.string.expenses_list_expense_deleted_failure)

    LaunchedEffect(true) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is ExpensesListSideEffect.DisplayExpensesDeletedSuccess ->
                    Toast.makeText(context, expenseDeletedSuccessMessage, Toast.LENGTH_SHORT).show()
                is ExpensesListSideEffect.DisplayExpensesDeletedFailure ->
                    Toast.makeText(context, expenseDeletedFailureMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(topBar = {
        ExpensesListTopAppBar(onSortModeUpdated = { sortMode ->
            onSortModeUpdated.invoke(sortMode)
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navigateToExpensesDetailScreen.invoke() }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.expenses_list_add_expense_button_content_description)
            )
        }
    }, floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        ExpenseList(
            modifier = Modifier.padding(innerPadding),
            expenses = expenses,
            onEditExpenseButtonClicked = { expense ->
                onEditExpenseButtonClicked.invoke(expense)
            },
            onDeleteExpenseButtonClicked =  { expense ->
                onDeleteExpenseButtonClicked.invoke(expense)
            }
        )
    }
}

@Composable
fun ExpensesListTopAppBar(onSortModeUpdated: (SortMode) -> Unit) {
    var showMenu by rememberSaveable {
        mutableStateOf(false)
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        title = {
            Text(
                color = colorResource(id = R.color.white),
                text = stringResource(id = R.string.expenses_list_header)
            )
        },
        actions = {
            IconButton(
                onClick = { showMenu = !showMenu }) {
                Icon(
                    tint = colorResource(id = R.color.white),
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(id = R.string.expenses_list_display_menu_content_description)
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = !showMenu }) {
                DropdownMenuItem(onClick = {
                    onSortModeUpdated(SortMode.ExpenseDateDescending)
                    showMenu = !showMenu
                }) {
                    Text(text = stringResource(id = R.string.expenses_list_menu_item_sort_by_expense_date_descending))
                }
                DropdownMenuItem(onClick = {
                    onSortModeUpdated(SortMode.ExpenseDateAscending)
                    showMenu = !showMenu
                }
                ) {
                    Text(text = stringResource(id = R.string.expenses_list_menu_item_sort_by_expense_date_ascending))
                }
            }
        }
    )
}

@Composable
fun ExpenseList(
    modifier: Modifier, expenses: List<Expense>,
    onEditExpenseButtonClicked: (Expense) -> Unit,
    onDeleteExpenseButtonClicked: (Expense) -> Unit
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(expenses) { index, expense ->
            var simpleDateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val month = simpleDateFormat.format(expense.date)
            val previousMonth = expenses.getOrNull(index - 1)?.let { previousExpense ->
                simpleDateFormat.format(previousExpense.date)
            }

            simpleDateFormat = SimpleDateFormat("YYYY", Locale.getDefault())
            val year = simpleDateFormat.format(expense.date)

            Column {
                if (previousMonth != month) {
                    ExpenseHeader(title = "${month.uppercase()} $year")
                }
                ExpenseCard(
                    expense = expense,
                    onEditExpenseButtonClicked = { editedExpense ->
                        onEditExpenseButtonClicked(editedExpense)
                    },
                    onDeleteExpenseButtonClicked = { deletedExpense ->
                        onDeleteExpenseButtonClicked(deletedExpense)
                    })
            }
        }
    }
}

@Composable
fun ExpenseHeader(title: String) {
    Text(
        modifier = Modifier.padding(
            horizontal = 8.dp,
            vertical = 4.dp
        ),
        style = MaterialTheme.typography.h6,
        text = title
    )
}

@Composable
fun ExpenseCard(
    expense: Expense, modifier: Modifier = Modifier,
    onEditExpenseButtonClicked: (Expense) -> Unit,
    onDeleteExpenseButtonClicked: (Expense) -> Unit
) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
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
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.End,
        ) {
            IconButton(
                onClick = { onEditExpenseButtonClicked(expense) }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(id = R.string.expense_list_edit_expense_button_content_description)
                )
            }
            IconButton(
                onClick = { onDeleteExpenseButtonClicked(expense) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.expenses_list_delete_expense_button_content_description)
                )
            }
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
        ExpensesListScreen(
            onEditExpenseButtonClicked = {},
            onDeleteExpenseButtonClicked = {},
            onSortModeUpdated = {},
            navigateToExpensesDetailScreen = {})
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
            ),
            onEditExpenseButtonClicked = {},
            onDeleteExpenseButtonClicked = {}
        )
    }
}

// endregion