package com.mfrancetic.expensesapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mfrancetic.expensesapp.ExpensesViewModel
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme

// region UI

@Composable
fun ExpensesListScreen(
    viewModel: ExpensesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navigateToExpensesDetailScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar {
                Text(
                    text = stringResource(id = R.string.expenses_list_header)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToExpensesDetailScreen.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.expenses_list_add_expense_button)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        println(innerPadding)
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
// endregion