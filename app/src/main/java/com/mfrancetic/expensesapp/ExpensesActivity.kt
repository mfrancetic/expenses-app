package com.mfrancetic.expensesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mfrancetic.expensesapp.db.Expense
import com.mfrancetic.expensesapp.screens.ExpensesDetailScreen
import com.mfrancetic.expensesapp.screens.ExpensesListScreen
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import com.mfrancetic.expensesapp.utils.NavigationConstants.EXPENSE_KEY
import com.mfrancetic.expensesapp.utils.NavigationDestination
import com.mfrancetic.expensesapp.utils.NavigationUtils.navigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpensesActivity : ComponentActivity() {

    // region Life-cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

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
        val navController = rememberNavController()

        NavigationNavHost(
            navController = navController
        )
    }

    @Composable
    fun NavigationNavHost(
        navController: NavHostController
    ) {
        NavHost(
            navController = navController,
            startDestination = NavigationDestination.ExpensesListScreen.name
        ) {
            composable(route = NavigationDestination.ExpensesListScreen.name) {
                val expensesListViewModel = hiltViewModel<ExpensesListViewModel>()
                ExpensesListScreen(viewModel = expensesListViewModel,
                    onEditExpenseButtonClicked = { expense ->
                        navController.navigate(
                            route = NavigationDestination.ExpensesDetailScreen.name,
                            args = bundleOf(EXPENSE_KEY to expense)
                        )
                    },
                    onDeleteExpenseButtonClicked = { expense ->
                        expensesListViewModel.deleteExpense(expense)
                    },
                    onSortModeUpdated = { sortMode ->
                           expensesListViewModel.updateSortMode(sortMode)
                    },
                    navigateToExpensesDetailScreen = {
                        navController.navigate(NavigationDestination.ExpensesDetailScreen.name)
                    })
            }

            composable(NavigationDestination.ExpensesDetailScreen.name) {
                val expensesDetailViewModel = hiltViewModel<ExpensesDetailViewModel>()
                val expense = it.arguments?.getParcelable<Expense>(EXPENSE_KEY)
                expensesDetailViewModel.initWithExpense(expense)

                ExpensesDetailScreen(
                    viewModel = expensesDetailViewModel,
                    onExpenseUpdated = { newExpense ->
                        expensesDetailViewModel.onExpenseUpdated(newExpense)
                    },
                    onUpButtonClicked = {
                        navController.navigateUp()
                    },
                    onSaveButtonClicked = { newExpense ->
                        expensesDetailViewModel.onSaveButtonClicked(newExpense)
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }

    // endregion
}