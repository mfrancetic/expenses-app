package com.mfrancetic.expensesapp.screens

import android.app.DatePickerDialog
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mfrancetic.expensesapp.ExpensesDetailViewModel
import com.mfrancetic.expensesapp.R
import com.mfrancetic.expensesapp.models.AmountError
import com.mfrancetic.expensesapp.models.ExpenseCategory
import com.mfrancetic.expensesapp.models.ExpenseCurrency
import com.mfrancetic.expensesapp.models.ExpenseViewData
import com.mfrancetic.expensesapp.models.ExpensesDetailSideEffect
import com.mfrancetic.expensesapp.models.TitleError
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme
import com.mfrancetic.expensesapp.utils.FormatUtils.name
import com.mfrancetic.expensesapp.utils.ValidationConstants.MAX_AMOUNT
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ExpensesDetailScreen(
    isEditMode: Boolean = false,
    viewModel: ExpensesDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onExpenseUpdated: (ExpenseViewData) -> Unit,
    onUpButtonClicked: () -> Unit,
    onSaveButtonClicked: (ExpenseViewData) -> Unit,
    onNavigateBack: () -> Unit
) {
    val expensesDetailState =
        viewModel.container.stateFlow.collectAsState().value
    val expense = expensesDetailState.expense
    val isSaveButtonEnabled = expensesDetailState.isSaveExpenseEnabled
    val hasEditingStarted = viewModel.container.stateFlow.collectAsState().value.hasEditingStarted
    val titleError = expensesDetailState.titleError
    val amountError = expensesDetailState.amountError
    var showDiscardDraftDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(true) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                ExpensesDetailSideEffect.NavigateBack ->
                    onNavigateBack.invoke()
            }
        }
    }

    BackHandler(enabled = true, onBack = {
        if (hasEditingStarted) {
            showDiscardDraftDialog = true
        } else {
            onUpButtonClicked.invoke()
        }
    })
    Scaffold(
        topBar = {
            ExpensesDetailScreenTopAppBar(
                isEditMode = isEditMode,
                onUpButtonClicked =
                {
                    if (hasEditingStarted) {
                        showDiscardDraftDialog = true
                    } else {
                        onUpButtonClicked.invoke()
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                ExpensesDetailTitleTextField(
                    title = expense.title,
                    titleError = titleError,
                    onTitleUpdated = { newTitle ->
                        onExpenseUpdated(expense.copy(title = newTitle))
                    })

                ExpensesDetailAmountTextField(
                    amount = expense.amountString,
                    currency = expense.currency,
                    amountError = amountError,
                    isSaveButtonEnabled = isSaveButtonEnabled,
                    onAmountUpdated = { newAmount ->
                        onExpenseUpdated(
                            expense.copy(
                                amount = newAmount.toDoubleOrNull(),
                                amountString = newAmount
                            )
                        )
                    },
                    onCurrencyUpdated = { newCurrency ->
                        onExpenseUpdated(expense.copy(currency = newCurrency))
                    },
                    onSaveButtonClicked = { onSaveButtonClicked.invoke(expense) }
                )

                ExpensesDetailCategoryTextField(
                    category = expense.category
                ) { newCategory ->
                    onExpenseUpdated(expense.copy(category = newCategory))
                }

                ExpensesDetailDateTextField(
                    date = expense.date,
                    onDateUpdated = { newDate ->
                        onExpenseUpdated(
                            expense.copy(
                                date = newDate
                            )
                        )
                    }
                )

                ExpensesDetailScreenSaveButton(
                    isSaveButtonEnabled = isSaveButtonEnabled, onSaveButtonClicked = {
                        onSaveButtonClicked.invoke(expense)
                    })
            }
        }
    }

    if (showDiscardDraftDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDraftDialog = false },
            title = {
                Text(stringResource(id = R.string.expenses_details_discard_draft_dialog_title))
            },
            text = {
                Text(stringResource(id = R.string.expenses_details_discard_draft_dialog_text))
            },
            confirmButton = {
                TextButton(onClick = { onUpButtonClicked.invoke() }) {
                    Text(stringResource(id = R.string.expenses_details_discard_draft_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDraftDialog = false }) {
                    Text(stringResource(id = R.string.expenses_details_discard_draft_dialog_dismiss))
                }
            }
        )
    }
}

@Composable
fun ExpensesDetailScreenTopAppBar(isEditMode: Boolean = false, onUpButtonClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                color = colorResource(id = R.color.white),
                text = stringResource(
                    id = if (isEditMode) R.string.expenses_details_header_edit_expense
                    else R.string.expenses_details_header_new_expense
                )
            )
        },
        backgroundColor = MaterialTheme.colors.primaryVariant,
        navigationIcon = {
            Icon(
                imageVector =
                Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.expenses_details_up_button_content_description),
                modifier = Modifier.clickable {
                    onUpButtonClicked()
                },
                tint = colorResource(id = R.color.white)
            )
        }
    )
}

@Composable
fun ExpensesDetailTitleTextField(
    title: String,
    titleError: TitleError?,
    onTitleUpdated: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        value = title, onValueChange = {
            onTitleUpdated(it)
        },
        isError = titleError != null,
        label = {
            Text(text = stringResource(id = R.string.expenses_details_title))
        },
        placeholder = {
            Text(text = stringResource(id = R.string.expenses_details_title_placeholder))
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        })
    )
    if (titleError == TitleError.TitleEmpty) {
        Text(
            text = stringResource(id = R.string.expenses_details_title_error_title_empty),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun ExpensesDetailAmountTextField(
    amount: String,
    currency: ExpenseCurrency,
    amountError: AmountError?,
    isSaveButtonEnabled: Boolean,
    onAmountUpdated: (String) -> Unit,
    onCurrencyUpdated: (ExpenseCurrency) -> Unit,
    onSaveButtonClicked: () -> Unit
) {
    val keyboardController = LocalFocusManager.current

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    val icon =
        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Column {
        TextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            value = amount, onValueChange = {
                if (it.isBlank() || it.toDoubleOrNull() != null
                ) {
                    onAmountUpdated(it)
                }
            },
            isError = amountError != null,
            label = {
                Text(text = stringResource(id = R.string.expenses_details_amount))
            },
            placeholder = {
                Text(text = stringResource(id = R.string.expenses_details_amount_placeholder))
            },
            trailingIcon = {
                Row {
                    Text(text = currency.symbol)
                    Icon(imageVector = icon,
                        contentDescription = stringResource(id = R.string.expenses_details_currency_content_description),
                        modifier = Modifier.clickable { expanded = !expanded })
                }

            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                if (isSaveButtonEnabled) {
                    onSaveButtonClicked()
                } else {
                    keyboardController.clearFocus()
                }
            })
        )

        if (amountError != null) {
            Text(
                text =
                if (amountError == AmountError.AmountTooLow)
                    stringResource(id = R.string.expenses_detail_amount_error_amount_too_low)
                else stringResource(id = R.string.expenses_detail_amount_error_invalid_format),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            ExpenseCurrency.values().forEach { currency ->
                DropdownMenuItem(onClick = {
                    onCurrencyUpdated(currency)
                    expanded = false
                }) {
                    Text(currency.name)
                }
            }
        }
    }

}

@Composable
fun ExpensesDetailCategoryTextField(
    category: ExpenseCategory,
    onCategoryUpdated: (ExpenseCategory) -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    val icon =
        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val items = ExpenseCategory.values()

    Column {
        TextField(
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(
                    LocalContentAlpha.current
                )
            ),
            value = category.name(LocalContext.current), onValueChange = {
                onCategoryUpdated(ExpenseCategory.valueOf(it))
            },
            label = { Text(text = stringResource(id = R.string.expenses_details_category)) },
            trailingIcon = {
                Icon(imageVector = icon,
                    contentDescription = stringResource(id = R.string.expenses_details_category_content_description),
                    modifier = Modifier.clickable { expanded = !expanded })
            }
        )

        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { expenseCategory ->
                DropdownMenuItem(onClick = {
                    onCategoryUpdated(expenseCategory)
                    expanded = false
                }) {
                    Text(expenseCategory.name(LocalContext.current))
                }
            }
        }
    }
}

@Composable
fun ExpensesDetailDateTextField(
    date: Long,
    onDateUpdated: (Long) -> Unit,
) {
    val context = LocalContext.current
    val currentDate = System.currentTimeMillis()
    val calendar = Calendar.getInstance()

    TextField(
        readOnly = true,
        enabled = false,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            disabledTextColor = LocalContentColor.current.copy(
                LocalContentAlpha.current
            )
        ),
        value = SimpleDateFormat.getDateInstance().format(date), onValueChange = {
        },
        label = {
            Text(text = stringResource(id = R.string.expenses_details_date))
        },
        placeholder = {
            Text(
                text = SimpleDateFormat
                    .getDateInstance()
                    .format(currentDate)
            )
        },
        trailingIcon = {
            Icon(imageVector = Icons.Filled.CalendarToday,
                contentDescription = stringResource(id = R.string.expenses_details_date_content_description),
                modifier = Modifier.clickable {
                    val datePicker = DatePickerDialog(context)
                    datePicker
                        .setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                            calendar.set(year, monthOfYear, dayOfMonth)
                            onDateUpdated(calendar.timeInMillis)
                        }
                    datePicker.show()
                })
        }
    )
}

@Composable
fun ExpensesDetailScreenSaveButton(isSaveButtonEnabled: Boolean, onSaveButtonClicked: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = isSaveButtonEnabled,
        onClick = {
            onSaveButtonClicked()
        }) {
        Text(text = stringResource(id = R.string.expenses_details_save))
    }
}

// region Preview

@Preview("ExpensesDetailScreen Preview", showBackground = true)
@Preview("ExpensesDetailScreen Preview", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpensesDetailScreenPreview() {
    ExpensesAppTheme {
        ExpensesDetailScreen(
            onExpenseUpdated = {},
            onUpButtonClicked = {},
            onSaveButtonClicked = {},
            onNavigateBack = {},
        )
    }
}

// endregion