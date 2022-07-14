package com.mfrancetic.expensesapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mfrancetic.expensesapp.ui.theme.ExpensesAppTheme

@Composable
fun ExpensesDetailScreen() {
    Text("Details screen")
}

// region Preview

@Preview("ExpensesDetailScreen Preview", showBackground = true)
@Preview("ExpensesDetailScreen Preview", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpensesDetailScreenPreview() {
    ExpensesAppTheme {
        ExpensesDetailScreen()
    }
}

// endregion