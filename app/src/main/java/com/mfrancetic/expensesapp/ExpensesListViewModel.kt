package com.mfrancetic.expensesapp

import androidx.lifecycle.ViewModel
import com.mfrancetic.expensesapp.models.Expense
import com.mfrancetic.expensesapp.models.ExpensesListSideEffect
import com.mfrancetic.expensesapp.models.ExpensesListState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExpensesListViewModel @Inject constructor() : ViewModel(),
    ContainerHost<ExpensesListState, ExpensesListSideEffect> {

    private var expenses = mutableListOf<Expense>()

    override val container =
        container<ExpensesListState, ExpensesListSideEffect>(ExpensesListState(expenses = emptyList()))

    // region Public Interface

    fun onSaveButtonClicked(expense: Expense) = intent {
        expenses.add(expense)

        reduce {
            state.copy(expenses = expenses.toList())
        }

        postSideEffect(ExpensesListSideEffect.NavigateBack)
    }

    // endregion
}