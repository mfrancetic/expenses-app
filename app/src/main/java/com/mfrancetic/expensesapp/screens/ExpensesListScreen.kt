package com.mfrancetic.expensesapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
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
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.datepicker.MaterialDatePicker
import com.mfrancetic.expensesapp.ExpensesListViewModel
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.models.DateRange
import com.mfrancetic.expensesapp.models.DownloadFormat
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpenseCurrency
import com.mfrancetic.expensesapp.models.ExpensesListSideEffect
import com.mfrancetic.expensesapp.models.SortMode
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import com.mfrancetic.expensesapp.ui.theme.LightOrange
import com.mfrancetic.expensesapp.utils.FormatUtils.formatCurrency
import java.text.SimpleDateFormat
import java.util.*


// region UI

@Composable
fun ExpensesListScreen(
    viewModel: ExpensesListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onEditExpenseButtonClicked: (Expense) -> Unit,
    onDeleteExpenseButtonClicked: (Expense) -> Unit,
    onSortModeUpdated: (SortMode) -> Unit,
    onDownloadButtonClicked: (DownloadFormat) -> Unit,
    onDeleteAllExpensesButtonClicked: () -> Unit,
    onDateRangeUpdated: (DateRange) -> Unit,
    navigateToExpensesDetailScreen: () -> Unit
) {
    val context = LocalContext.current
    val expenses = viewModel.container.stateFlow.collectAsState().value.expenses
    val areExpensesEmpty = expenses.isEmpty()
    val expenseDeletedSuccessMessage =
        stringResource(id = R.string.expenses_list_expense_deleted_success)
    val expenseDeletedFailureMessage =
        stringResource(id = R.string.expenses_list_expense_deleted_failure)
    val allExpensesDeletedSuccessMessage =
        stringResource(id = R.string.expenses_list_all_expenses_deleted_success)
    val allExpensesDeletedFailureMessage =
        stringResource(id = R.string.expenses_list_all_expenses_deleted_failure)
    val expensesDataDownloadSuccessMessage =
        stringResource(id = R.string.expenses_list_expenses_data_download_success)
    val expensesDataDownloadFailureMessage =
        stringResource(id = R.string.expenses_list_expenses_data_download_failure)

    LaunchedEffect(true) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is ExpensesListSideEffect.DisplayExpenseDeletedSuccess ->
                    Toast.makeText(context, expenseDeletedSuccessMessage, Toast.LENGTH_SHORT).show()
                is ExpensesListSideEffect.DisplayExpenseDeletedFailure ->
                    Toast.makeText(context, expenseDeletedFailureMessage, Toast.LENGTH_SHORT).show()
                is ExpensesListSideEffect.DisplayExpensesDataDownloadSuccess ->
                    Toast.makeText(context, expensesDataDownloadSuccessMessage, Toast.LENGTH_SHORT)
                        .show()
                is ExpensesListSideEffect.DisplayExpensesDataDownloadFailure ->
                    Toast.makeText(context, expensesDataDownloadFailureMessage, Toast.LENGTH_SHORT)
                        .show()
                is ExpensesListSideEffect.DisplayAllExpensesDeletedSuccess ->
                    Toast.makeText(context, allExpensesDeletedSuccessMessage, Toast.LENGTH_SHORT)
                        .show()
                is ExpensesListSideEffect.DisplayAllExpensesDeletedFailure ->
                    Toast.makeText(context, allExpensesDeletedFailureMessage, Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    Scaffold(topBar = {
        ExpensesListTopAppBar(
            areExpensesEmpty = areExpensesEmpty, onSortModeUpdated = { sortMode ->
                onSortModeUpdated.invoke(sortMode)
            },
            onDownloadButtonClicked = { downloadFormat ->
                onDownloadButtonClicked.invoke(downloadFormat)
            },
            onDeleteAllExpensesButtonClicked = {
                onDeleteAllExpensesButtonClicked.invoke()
            },
            onDateRangeUpdated = { dateRange ->
                onDateRangeUpdated.invoke(dateRange)
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
        if (expenses.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    style = MaterialTheme.typography.h6,
                    text = stringResource(id = R.string.expenses_list_no_expenses_added)
                )
                Button(
                    onClick = {
                        navigateToExpensesDetailScreen()
                    },
                    content = {
                        Text(stringResource(id = R.string.expenses_list_add_expense_button_content_description))
                    }
                )
            }
        } else {
            Column {
                ExpenseHeader(
                    title = stringResource(id = R.string.expenses_list_total_expense_amount),
                    amount = expenses.sumOf { it.amount },
                    currency = ExpenseCurrency.HRK
                )
                ExpenseList(
                    modifier = Modifier.padding(innerPadding),
                    expenses = expenses,
                    onEditExpenseButtonClicked = { expense ->
                        onEditExpenseButtonClicked.invoke(expense)
                    },
                    onDeleteExpenseButtonClicked = { expense ->
                        onDeleteExpenseButtonClicked.invoke(expense)
                    }
                )
            }
        }
    }
}

@Composable
fun ExpensesListTopAppBar(
    areExpensesEmpty: Boolean = false,
    onSortModeUpdated: (SortMode) -> Unit,
    onDownloadButtonClicked: (DownloadFormat) -> Unit,
    onDeleteAllExpensesButtonClicked: () -> Unit,
    onDateRangeUpdated: (DateRange) -> Unit
) {
    val context = LocalContext.current
    val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
    var showSortMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var showDownloadMenu by rememberSaveable {
        mutableStateOf(false)
    }
    var showDeleteAllExpensesAlertDialog by rememberSaveable {
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
                enabled = !areExpensesEmpty,
                onClick = {
                    showDownloadMenu = !showDownloadMenu
                }) {
                Icon(
                    tint = if (areExpensesEmpty) LightOrange else Color.White,
                    imageVector = Icons.Filled.Download,
                    contentDescription = stringResource(id = R.string.expenses_list_display_download_menu_content_description)
                )
            }
            IconButton(
                onClick = {
                    fragmentManager?.let {
                        MaterialDatePicker.Builder.dateRangePicker().build()
                            .apply {
                                addOnPositiveButtonClickListener {
                                    onDateRangeUpdated(DateRange(Date(it.first), Date(it.second)))
                                }
                            }
                            .show(fragmentManager, this.javaClass.name)
                    }
                }) {
                Icon(
                    tint = colorResource(id = R.color.white),
                    imageVector = Icons.Filled.EditCalendar,
                    contentDescription = stringResource(id = R.string.expenses_list_display_filter_menu_content_description)
                )
            }
            DropdownMenu(
                expanded = showDownloadMenu,
                onDismissRequest = { showDownloadMenu = !showDownloadMenu }) {
                DropdownMenuItem(onClick = {
                    onDownloadButtonClicked(DownloadFormat.CSV)
                    showDownloadMenu = !showDownloadMenu
                }) {
                    Text(text = stringResource(id = R.string.expenses_list_menu_item_download_csv))
                }
                DropdownMenuItem(onClick = {
                    onDownloadButtonClicked(DownloadFormat.DB)
                    showDownloadMenu = !showDownloadMenu
                }
                ) {
                    Text(text = stringResource(id = R.string.expenses_list_menu_item_download_db))
                }
            }
            IconButton(
                onClick = { showSortMenu = !showSortMenu }) {
                Icon(
                    tint = colorResource(id = R.color.white),
                    imageVector = Icons.Filled.Sort,
                    contentDescription = stringResource(id = R.string.expenses_list_display_sort_menu_content_description)
                )
            }
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = !showSortMenu }) {
                DropdownMenuItem(onClick = {
                    onSortModeUpdated(SortMode.ExpenseDateDescending)
                    showSortMenu = !showSortMenu
                }) {
                    Text(text = stringResource(id = R.string.expenses_list_menu_item_sort_by_expense_date_descending))
                }
                DropdownMenuItem(onClick = {
                    onSortModeUpdated(SortMode.ExpenseDateAscending)
                    showSortMenu = !showSortMenu
                }
                ) {
                    Text(text = stringResource(id = R.string.expenses_list_menu_item_sort_by_expense_date_ascending))
                }
            }
            IconButton(
                enabled = !areExpensesEmpty,
                onClick = {
                    showDeleteAllExpensesAlertDialog = !showDeleteAllExpensesAlertDialog
                }) {
                Icon(
                    tint = if (areExpensesEmpty) LightOrange else Color.White,
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.expenses_list_menu_item_delete_all_expenses)
                )
            }
            if (showDeleteAllExpensesAlertDialog) {
                AlertDialog(onDismissRequest = {
                    showDeleteAllExpensesAlertDialog = false
                },
                    confirmButton = {
                        TextButton(onClick = {
                            onDeleteAllExpensesButtonClicked()
                            showDeleteAllExpensesAlertDialog = false
                        }) {
                            Text(stringResource(id = R.string.expenses_list_delete_all_expenses_dialog_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteAllExpensesAlertDialog = false
                            }) {
                            Text(text = stringResource(id = R.string.expenses_list_delete_all_expenses_dialog_dismiss))
                        }
                    },
                    title = {
                        Text(text = stringResource(id = R.string.expenses_list_delete_all_expenses_dialog_title))
                    },
                    text = { Text(text = stringResource(id = R.string.expenses_list_delete_all_expenses_dialog_text)) })
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
        itemsIndexed(items = expenses,
            key = { _, expense -> expense.id }
        ) { index, expense ->
            var simpleDateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val month = simpleDateFormat.format(expense.date)
            val previousMonth = expenses.getOrNull(index - 1)?.let { previousExpense ->
                simpleDateFormat.format(previousExpense.date)
            }

            val monthlyExpenses =
                expenses.filter { simpleDateFormat.format(it.date) == month }
            val monthlyExpensesSum = monthlyExpenses.sumOf { it.amount }

            simpleDateFormat = SimpleDateFormat("YYYY", Locale.getDefault())
            val year = simpleDateFormat.format(expense.date)

            Column {
                if (previousMonth != month) {
                    ExpenseHeader(
                        title = "${month.uppercase()} $year",
                        amount = monthlyExpensesSum,
                        currency = expense.currency
                    )
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
fun ExpenseHeader(title: String, amount: Double, currency: ExpenseCurrency) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
            )
            .padding(top = 16.dp, bottom = 4.dp)
    ) {
        Text(
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h6,
            text = title
        )

        Text(
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h6,
            text = "Σ ${amount.formatCurrency(currency)}"
        )
    }
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
            .clickable {
                onEditExpenseButtonClicked(expense)
            }
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                val categoryImage: ImageVector = when (expense.category) {
                    ExpenseCategory.Rent -> Icons.Filled.House
                    ExpenseCategory.Utilities -> Icons.Filled.Lightbulb
                    ExpenseCategory.Groceries -> Icons.Filled.ShoppingCart
                    ExpenseCategory.Restaurants -> Icons.Filled.Restaurant
                    ExpenseCategory.Entertainment -> Icons.Filled.MusicNote
                    ExpenseCategory.Travel -> Icons.Filled.Hotel
                    ExpenseCategory.Car -> Icons.Filled.ElectricCar
                    ExpenseCategory.Other -> Icons.Filled.Payments
                }

                Image(
                    imageVector = categoryImage,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onSurface),
                    contentDescription = null
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.expenses_list_expense_amount) + ": " +
                            expense.amount.formatCurrency(expense.currency)
                )
                Text(
                    text = stringResource(id = R.string.expenses_list_expense_date) + ": ${
                        SimpleDateFormat.getDateInstance().format(expense.date)
                    }"
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
            ) {
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
            onDownloadButtonClicked = {},
            onDeleteAllExpensesButtonClicked = {},
            onDateRangeUpdated = {},
            navigateToExpensesDetailScreen = {})
    }
}

@Preview("ExpenseCard light mode", showBackground = true)
@Preview("ExpensesCard dark mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpensesListTopAppBarPreview() {
    ExpensesAppTheme {
        ExpensesListTopAppBar(onSortModeUpdated = {},
            onDownloadButtonClicked = {},
            onDeleteAllExpensesButtonClicked = {},
            onDateRangeUpdated = {})
    }
}

@Preview("ExpenseCard light mode", showBackground = true)
@Preview("ExpensesCard dark mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpenseCardPreview() {
    ExpensesAppTheme {
        ExpenseCard(
            expense = Expense(title = "Groceries", amount = 1524.665),
            onEditExpenseButtonClicked = {},
            onDeleteExpenseButtonClicked = {}
        )
    }
}

@Preview("ExpenseHeader light mode", showBackground = true)
@Preview("ExpenseHeader dark mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpenseHeaderPreview() {
    ExpensesAppTheme {
        ExpenseHeader(
            title = "Groceries", amount = 1524.665,
            currency = ExpenseCurrency.HRK
        )
    }
}

// endregion