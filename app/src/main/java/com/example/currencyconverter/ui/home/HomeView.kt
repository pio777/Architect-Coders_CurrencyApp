package com.example.currencyconverter.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.currencyconverter.R
import com.example.currencyconverter.domain.Currency
import com.example.currencyconverter.domain.currenciesList
import com.example.currencyconverter.domain.frankfurterCurrenciesList
import com.example.currencyconverter.ui.common.ScreenContent
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme

private const val EMPTY_STRING = ""
private const val POINT_CHARACTER = '.'
private const val NO_CURRENCY_SELECTED = -1
private const val FIRST_QTY_INPUT = 0
private const val SECOND_QTY_INPUT = 1

@Composable
fun Screen(content: @Composable () -> Unit) {
    CurrencyConverterTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))

    var showDialog by remember { mutableStateOf(false) }
    var selectedRowIndex by remember { mutableIntStateOf(NO_CURRENCY_SELECTED) }

    Screen {
        ScreenContent(navController) { padding ->
            CurrencySelectionDialog(
                modifier = Modifier.padding(padding),
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                currencies = if (selectedRowIndex == 0) viewModel.getAvailableCurrencies(FIRST_QTY_INPUT) else viewModel.getAvailableCurrencies(
                    SECOND_QTY_INPUT
                ),
                onCurrencySelected = { currency ->
                    if (selectedRowIndex >= 0) {
                        when (selectedRowIndex) {
                            0 -> viewModel.onCurrencySelected(viewModel.input1, currency)
                            else -> viewModel.onCurrencySelected(viewModel.input2, currency)
                        }
                    }
                }
            )

            Column(modifier = Modifier.padding(padding)) {
                ConversionForm(
                    toggleDialog = { index ->
                        selectedRowIndex = index
                        showDialog = true
                    },
                    viewModel = viewModel
                )

                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = { viewModel.addFavoritePair() },
                    enabled = viewModel.input1.currency.collectAsState().value != null && viewModel.input2.currency.collectAsState().value != null
                ) {
                    Text(stringResource(R.string.save_as_favorite))
                }

                FavoriteCurrenciesList(viewModel)
            }

        }
    }
}


@Composable
fun FavoriteCurrenciesList(viewModel: HomeViewModel) { // TODO Pio, this could show multiple favorites in a LazyColumn
    val favoritePairs = viewModel.favoriteCurrencies.collectAsState().value
    LazyColumn(
        modifier = Modifier.wrapContentWidth(),
        userScrollEnabled = false
    ) {
        items(favoritePairs) { pair ->
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                FavoriteCurrencyRow(pair) { currency1, currency2 ->
                    viewModel.onCurrencySelected(viewModel.input1, currency1)
                    viewModel.onCurrencySelected(viewModel.input2, currency2)
                }
            }
        }
    }
}

@Composable
fun FavoriteCurrencyRow(
    pair: Pair<Currency, Currency>,
    onClick: (Currency, Currency) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick(pair.first, pair.second) }
            .padding(11.dp)
    ) {
        with(pair.first) { Text("$flag$code") }
        Image(
            imageVector = Icons.Filled.Repeat,
            contentDescription = stringResource(R.string.favorite_currency_selected),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        with(pair.second) { Text("$flag$code") }
    }
}

@Composable
fun ConversionForm(
    toggleDialog: (Int) -> Unit,
    viewModel: HomeViewModel
) {
    val result by viewModel.result.collectAsState()
    val input1Amount by viewModel.input1.amount.collectAsState()
    val input2Amount by viewModel.input2.amount.collectAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CurrencyRow(
            index = 0,
            toggleDialog = toggleDialog,
            selectedCurrency = viewModel.input1.currency.collectAsState().value,
            onAmountChange = { newAmount ->
                viewModel.onAmountChange(FIRST_QTY_INPUT, newAmount)
                if (newAmount.isEmpty()) viewModel.onAmountChange(SECOND_QTY_INPUT, EMPTY_STRING)
            },
            amount = if (viewModel.updateSource == HomeViewModel.UpdateSource.INPUT2 && result != null) {
                result.toString()
            } else {
                input1Amount
            }
        )
        CurrencyRow(
            index = 1,
            toggleDialog = toggleDialog,
            selectedCurrency = viewModel.input2.currency.collectAsState().value,
            onAmountChange = { newAmount ->
                viewModel.onAmountChange(SECOND_QTY_INPUT, newAmount)
                if (newAmount.isEmpty()) viewModel.onAmountChange(FIRST_QTY_INPUT, EMPTY_STRING)
            },
            amount = if (viewModel.updateSource == HomeViewModel.UpdateSource.INPUT1 && result != null) {
                result.toString()
            } else {
                input2Amount
            }
        )

    }
}

@Composable
fun CurrencyRow(
    modifier: Modifier = Modifier,
    index: Int,
    toggleDialog: (Int) -> Unit,
    selectedCurrency: Currency?,
    onAmountChange: (String) -> Unit,
    amount: String
) {
    var textFieldValue by remember { mutableStateOf(amount) }
    LaunchedEffect(amount) { textFieldValue = amount }

    Row(modifier = modifier.padding(bottom = 18.dp)) {
        Row(
            modifier = Modifier
                .clickable { toggleDialog.invoke(index) }
                .padding(8.dp)
                .weight(1f)
        ) {
            if (selectedCurrency == null) {
                Text(
                    text = stringResource(R.string.select_currency), modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                CurrencyInfoRow(selectedCurrency = selectedCurrency, toggleDialog = toggleDialog, index = index)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .padding(end = 4.dp)
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                maxLines = 1,
                singleLine = true,
                value = textFieldValue,
                onValueChange = { newText ->
                    var pointEncountered = false
                    val filteredText = newText.filter {
                        if (it == POINT_CHARACTER) {
                            if (pointEncountered) {
                                false
                            } else {
                                pointEncountered = true
                                true
                            }
                        } else {
                            it.isDigit()
                        }
                    }
                    textFieldValue = filteredText
                    onAmountChange(filteredText)
                },
                label = { Text(stringResource(R.string.enter_units)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = { text ->
                    TransformedText(
                        text = text,
                        offsetMapping = OffsetMapping.Identity
                    )
                }
            )
            IconButton(onClick = { onAmountChange(EMPTY_STRING) }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
            }
        }
    }
}

@Composable
fun CurrencyInfoRow(
    modifier: Modifier = Modifier,
    selectedCurrency: Currency?,
    isSelectable: Boolean = true,
    toggleDialog: (Int) -> Unit,
    index: Int
) {
    Row(
        modifier = modifier.clickable(enabled = isSelectable) { toggleDialog(index) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectedCurrency == null) {
            Text(
                text = stringResource(R.string.select_currency),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                modifier = Modifier.padding(end = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                text = selectedCurrency.flag
            )
            Column(
                verticalArrangement = Arrangement.spacedBy((-5).dp)
            ) {
                Text(fontSize = 13.sp, fontWeight = FontWeight.Bold, text = selectedCurrency.code)
                Text(
                    fontSize = 11.sp,
                    text = if (selectedCurrency in frankfurterCurrenciesList) {
                        selectedCurrency.name
                    } else {
                        "${selectedCurrency.name} *"
                    }
                )
            }
        }
    }
}

@Composable
fun CurrencySelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currencies: List<Currency>,
    onCurrencySelected: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        var searchText by remember { mutableStateOf(EMPTY_STRING) }
        val filteredCurrencies = if (searchText.isBlank()) {
            currencies
        } else {
            currencies.filter {
                it.name.contains(searchText, ignoreCase = true)
                    || it.code.contains(searchText, ignoreCase = true)
                    || it.country.contains(searchText, ignoreCase = true)
            }
        }

        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.select_currency)) },
            text = {
                Column {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text(stringResource(R.string.search)) }
                    )
                    LazyColumn {
                        items(filteredCurrencies) { currency ->
                            CurrencyCard(
                                currency = currency,
                                modifier = Modifier
                                    .clickable {
                                        onCurrencySelected(currency)
                                        onDismiss()
                                    }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun CurrencyCard(currency: Currency, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(3.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currency.flag,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(modifier = Modifier.padding(end = 10.dp), text = currency.code, fontWeight = FontWeight.Bold)
            Text(
                text = if (currency in frankfurterCurrenciesList) {
                    "${currency.name} "
                } else {
                    "${currency.name} *"
                },
                color = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true, device = "id:pixel_xl")
@Composable
fun BottomSheetScaffoldSamplePreview() {
    CurrencyConverterTheme {
        CurrencySelectionDialog(true, {}, currenciesList, {}, Modifier.padding(10.dp))
    }
}

@Preview(showBackground = true, device = "id:pixel_7_pro")
@Composable
fun ConversionFormPreview() {
    CurrencyConverterTheme {
        ConversionForm(
            toggleDialog = { },
            HomeViewModel(LocalContext.current)
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7_pro")
@Composable
fun CurrencyCardPreview() {
    CurrencyConverterTheme {
        CurrencyCard(currency = Currency("MXN", "Mexico", "MX", ""))
    }
}

@Preview(showBackground = true, device = "id:pixel_7_pro")
@Composable
fun CurrencyRowPreview() {
    CurrencyConverterTheme {
        CurrencyRow(
            index = 0,
            toggleDialog = {},
            selectedCurrency = Currency("MXN", "Mexican Peso", "MX", ""),
            onAmountChange = {},
            amount = "0.0"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteCurrencyRowPreview() {
    val pair = Pair(
        Currency("USD", "US Dollar", "US", ""),
        Currency("EUR", "Euro", "EU", "")
    )
    CurrencyConverterTheme {
        FavoriteCurrencyRow(pair) { _, _ -> }
    }
}
