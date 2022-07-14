package com.mfrancetic.expensesapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.mfrancetic.expensesapp.models.ExpensesSideEffect
import com.mfrancetic.expensesapp.screens.ExpensesListScreen
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpensesActivity : ComponentActivity() {

    // region State

    private val viewModel by viewModels<ExpensesViewModel>()

    // endregion

    // region Life-cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(viewModel) {
                viewModel.container.sideEffectFlow.collect { sideEffect ->
                    when(sideEffect){
                        is ExpensesSideEffect.NavigateToExpensesDetailsScreen -> {
                            Toast.makeText(baseContext, "Navigate to Expenses Details Screen", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            ExpensesAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Surface {
                        ExpensesListScreen(onAddExpenseButtonClicked = {
                            viewModel.onAddExpenseButtonClicked()
                        })
                    }
                }
            }
        }
    }

    // endregion
}