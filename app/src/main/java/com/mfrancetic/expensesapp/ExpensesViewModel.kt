package com.mfrancetic.expensesapp

import androidx.lifecycle.ViewModel
import com.mfrancetic.expensesapp.models.ExpensesSideEffect
import com.mfrancetic.expensesapp.models.ExpensesState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor() : ViewModel(),
    ContainerHost<ExpensesState, ExpensesSideEffect> {

    override val container =
        container<ExpensesState, ExpensesSideEffect>(ExpensesState(expenses = emptyList()))

    // region Public Interface

    fun onAddExpenseButtonClicked() = intent {
        postSideEffect(ExpensesSideEffect.NavigateToExpensesDetailsScreen)
    }

    // endregion
}