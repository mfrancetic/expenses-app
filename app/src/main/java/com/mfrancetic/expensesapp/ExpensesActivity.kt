package com.mfrancetic.expensesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpensesListSideEffect
import com.mfrancetic.expensesapp.screens.ExpensesDetailScreen
import com.mfrancetic.expensesapp.screens.ExpensesListScreen
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import com.mfrancetic.expensesapp.utils.NavigationDestination
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.UUID

@AndroidEntryPoint
class ExpensesActivity : ComponentActivity() {

    // region Life-cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpensesAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ExpensesApp()
                }
            }
        }
    }

    // endregion

    // region Composables

    @Composable
    fun ExpensesApp() {
        val expensesListViewModel = hiltViewModel<ExpensesListViewModel>()
        val expensesDetailViewModel = hiltViewModel<ExpensesDetailViewModel>()
        val navController = rememberNavController()

        LaunchedEffect(true) {
            expensesListViewModel.container.sideEffectFlow.collect { sideEffect ->
                when (sideEffect) {
                    ExpensesListSideEffect.NavigateBack ->
                        navController.navigateUp()
                }
            }
        }

        NavigationNavHost(
            navController = navController, expensesListViewModel = expensesListViewModel,
            expensesDetailViewModel = expensesDetailViewModel
        )
    }

    @Composable
    fun NavigationNavHost(
        navController: NavHostController,
        expensesListViewModel: ExpensesListViewModel = viewModel(),
        expensesDetailViewModel: ExpensesDetailViewModel = viewModel()
    ) {
        NavHost(
            navController = navController,
            startDestination = NavigationDestination.ExpensesListScreen.name
        ) {
            composable(route = NavigationDestination.ExpensesListScreen.name) {
                ExpensesListScreen(viewModel = expensesListViewModel,
                    navigateToExpensesDetailScreen = {
                        navController.navigate(NavigationDestination.ExpensesDetailScreen.name)
                    })
            }
            composable(NavigationDestination.ExpensesDetailScreen.name) {
                ExpensesDetailScreen(
                    expense = Expense(id = UUID.randomUUID().toString(),
                    "", "", ExpenseCategory.Rent, System.currentTimeMillis()),
                    onExpenseUpdated = {},
                    onUpButtonClicked = {
                        navController.navigateUp()
                    },
                    onSaveButtonClicked = { expense ->
                        expensesListViewModel.onSaveButtonClicked(expense)
                    }
                )
            }
        }
    }

    // endregion
}