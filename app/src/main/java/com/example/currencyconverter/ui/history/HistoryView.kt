package com.example.currencyconverter.ui.history

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.currencyconverter.R
import com.example.currencyconverter.domain.Currency
import com.example.currencyconverter.domain.ErrorType
import com.example.currencyconverter.domain.ExchangeRateTime
import com.example.currencyconverter.domain.topFrankfurterWorldCurrencies
import com.example.currencyconverter.ui.common.GenericErrorView
import com.example.currencyconverter.ui.common.ScreenContent
import com.example.currencyconverter.ui.home.CurrencyInfoRow
import com.example.currencyconverter.ui.home.CurrencySelectionDialog
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.legend.LegendItem
import com.patrykandpatrick.vico.core.legend.VerticalLegend
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

private const val GRAPH_LINES_NUMBER = 7

@Composable
fun Screen(content: @Composable () -> Unit) {
    CurrencyConverterTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}


fun getFormattedDate(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(date)
}

@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val exchangeRates = remember { mutableStateListOf<Pair<Date, Float>>() }

    LaunchedEffect(viewModel.exchangeRateTime) {
        exchangeRates.apply {
            clear()
            addAll(processExchangeRates(viewModel.exchangeRateTime))
        }
    }
    val showCurrencyDialog = remember { mutableStateOf(false) }
    val selectedCurrencyIndex = remember { mutableIntStateOf(0) }
    val fromCurrency = remember { mutableStateOf<Currency?>(null) }
    val toCurrency = remember { mutableStateOf<Currency?>(null) }
    var showError by remember { mutableStateOf(true) }

    val startDate = remember {
        mutableStateOf(
            getFormattedDate(
                Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time
            )
        )
    }
    val endDate = remember { mutableStateOf(getFormattedDate(Date())) }

    Screen {
        ScreenContent(navController) { padding ->
            // Use viewModel.showError to control error view visibility
            if (showError) {
                GenericErrorView(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth(),
                    errorType = ErrorType.Network,
                    errorMessage = "Could not fetch historical data",
                    onRetry = {
                        val fromCode = fromCurrency.value?.code ?: return@GenericErrorView
                        val toCode = toCurrency.value?.code ?: return@GenericErrorView
                        viewModel.getHistoricalConversion(startDate.value, endDate.value, fromCode, toCode)
                    },
                    onClose = { showError = false }
                )
            } else {
                HistoryScreenContent(
                    viewModel = viewModel,
                    context = context,
                    exchangeRates = exchangeRates,
                    showCurrencyDialog = showCurrencyDialog,
                    selectedCurrencyIndex = selectedCurrencyIndex,
                    fromCurrency = fromCurrency,
                    toCurrency = toCurrency,
                    startDate = startDate,
                    endDate = endDate,
                    paddingValues = padding
                )
            }
        }
    }
}

@Composable
fun HistoryScreenContent(
    viewModel: HistoryViewModel, context: Context,
    exchangeRates: MutableList<Pair<Date, Float>>,
    showCurrencyDialog: MutableState<Boolean>,
    selectedCurrencyIndex: MutableState<Int>,
    fromCurrency: MutableState<Currency?>,
    toCurrency: MutableState<Currency?>,
    startDate: MutableState<String>,
    endDate: MutableState<String>,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
    ) {
        // Currency Selection
        CurrencyInfoRow(
            modifier = Modifier.fillMaxWidth(),
            selectedCurrency = fromCurrency.value,
            isSelectable = true,
            toggleDialog = {
                selectedCurrencyIndex.value = 0
                showCurrencyDialog.value = true
            },
            index = 0
        )
        CurrencyInfoRow(
            modifier = Modifier.fillMaxWidth(),
            selectedCurrency = toCurrency.value,
            isSelectable = true,
            toggleDialog = {
                selectedCurrencyIndex.value = 1
                showCurrencyDialog.value = true
            },
            index = 1
        )

        // Date Pickers
        SimpleDatePicker(context, startDate.value) { startDate.value = it }
        SimpleDatePicker(context, endDate.value) { endDate.value = it }

        // Chart
        ExchangeRateChartScreen(
            modifier = Modifier.weight(1f),
            exchangeRates,
            isLoading = viewModel.isLoading
        )

        Button(
            onClick = {
                val fromCode = fromCurrency.value?.code ?: return@Button
                val toCode = toCurrency.value?.code ?: return@Button
                viewModel.getHistoricalConversion(startDate.value, endDate.value, fromCode, toCode)
            },
            enabled = fromCurrency.value != null && toCurrency.value != null
        ) {
            Text(stringResource(R.string.get_history))
        }
    }

    CurrencySelectionDialog(
        showDialog = showCurrencyDialog.value,
        onDismiss = { showCurrencyDialog.value = false },
        currencies = topFrankfurterWorldCurrencies,
        onCurrencySelected = { currency ->
            if (selectedCurrencyIndex.value == 0) {
                fromCurrency.value = currency
            } else {
                toCurrency.value = currency
            }
        }
    )
}

// Placeholder for Date Picker (Replace with your actual implementation)
@Composable
fun SimpleDatePicker(context: Context, date: String, onDateSelected: (String) -> Unit) {
    val calendar = remember { Calendar.getInstance() }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(
            // You'll need to provide a Context here.
            // You can get it using LocalContext.current in your actual implementation.
            context, // Replace with your Context
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                onDateSelected("$year-${month + 1}-$dayOfMonth") // Format the date
            },
            year,
            month,
            day
        )
    }

    // Button to trigger the DatePickerDialog
    Button(onClick = { datePickerDialog.show() }) {
        Text("Select Date")
    }

    // Display the selected date
    Text("Selected Date: $date")
}

fun processExchangeRates(exchangeRateTime: ExchangeRateTime?, currency: String = "MXN"): List<Pair<Date, Float>> {
    val dataPoints = mutableListOf<Pair<Date, Float>>()
    exchangeRateTime?.rates?.forEach { (date, currencies) ->
        val rate = currencies[currency]?.toFloat() ?: 0f
        dataPoints.add(Pair(date, rate))
    }
    return dataPoints
}

@Composable
fun ExchangeRateChartScreen(
    modifier: Modifier = Modifier,
    exchangeRates: List<Pair<Date, Float>>,
    isLoading: Boolean
) {

    Column(modifier = modifier.fillMaxWidth()) {
        ExchangeRateChart(
            exchangeRates = exchangeRates,
            isLoading = isLoading,
            onPointClick = { point -> Log.d("clicked", ("Punto clickeado: ${point.first} - ${point.second}")) })
    }
}

@Composable
fun ExchangeRateChart(
    modifier: Modifier = Modifier,
    exchangeRates: List<Pair<Date, Float>>,
    graphColor: Color = Color.Green,
    isLoading: Boolean = false,
    onPointClick: (Pair<Date, Float>) -> Unit
) {
    val spacing = 10f
    var showPopup by remember { mutableStateOf(false) }
    var selectedPoint by remember { mutableStateOf<Pair<Date, Float>?>(null) }
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val minValue = exchangeRates.minOfOrNull { it.second } ?: 0f
    val maxValue = exchangeRates.maxOfOrNull { it.second } ?: 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        if (isLoading) {
            // Show loading indicator while data is loading
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Canvas(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.DarkGray)
                    .padding(10.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Encuentra el punto más cercano al toque
                            val closestPointIndex = exchangeRates.indices.minByOrNull { i ->
                                val x = spacing + i * ((size.width - spacing) / exchangeRates.size)
                                val normalizedValue = normalizeValue(exchangeRates[i].second, exchangeRates, size.height.toFloat())
                                val y = size.height - normalizedValue
                                val distance = sqrt((offset.x - x) * (offset.x - x) + (offset.y - y) * (offset.y - y))
                                distance
                            }
                            // Si el punto más cercano está dentro de un radio razonable, llama a onPointClick
                            closestPointIndex?.let {
                                val point = exchangeRates[it]
                                val x = spacing + it * ((size.width - spacing) / exchangeRates.size)
                                val normalizedValue = normalizeValue(point.second, exchangeRates, size.height.toFloat())
                                val y = size.height - normalizedValue
                                val distance = sqrt((offset.x - x) * (offset.x - x) + (offset.y - y) * (offset.y - y))
                                if (distance <= 10.dp.toPx()) {
                                    onPointClick(point)
                                    selectedPoint = point
                                    showPopup = true
                                }
                            }
                        }
                    }
            ) {
                // Draw horizontal lines and labels
                for (i in 0..GRAPH_LINES_NUMBER) {
                    val y = i * (size.height / GRAPH_LINES_NUMBER)
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 0.5.dp.toPx()
                    )

                    // Calculate and display the label value
                    val labelValue = String.format("%.2f", maxValue - (i * (maxValue - minValue) / GRAPH_LINES_NUMBER))
                    drawContext.canvas.nativeCanvas.drawText(
                        labelValue,
                        0f,
                        y - 5f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 10.sp.toPx()
                        }
                    )
                }

                drawRect(
                    color = Color.Black,
                    topLeft = Offset.Zero,
                    size = Size(
                        width = size.width,
                        height = size.height
                    ),
                    style = Stroke()
                )

                val spacePerPoint = (size.width - spacing) / exchangeRates.size

                val strokePath = Path().apply {
                    exchangeRates.forEachIndexed { i, (date, value) ->
                        val currentX = spacing + i * spacePerPoint
                        val normalizedValue = normalizeValue(value, exchangeRates, size.height)
                        val currentY = size.height - normalizedValue

                        if (i == 0) {
                            moveTo(currentX, currentY)
                        } else {
                            val previousX = spacing + (i - 1) * spacePerPoint
                            val previousNormalizedValue = normalizeValue(exchangeRates[i - 1].second, exchangeRates, size.height)
                            val previousY = size.height - previousNormalizedValue

                            val conX1 = (previousX + currentX) / 2f
                            val conX2 = (previousX + currentX) / 2f

                            val conY1 = previousY
                            val conY2 = currentY

                            cubicTo(
                                x1 = conX1,
                                y1 = conY1,
                                x2 = conX2,
                                y2 = conY2,
                                x3 = currentX,
                                y3 = currentY
                            )
                        }
                    }
                }

                drawPath(
                    path = strokePath,
                    color = graphColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                exchangeRates.forEachIndexed { i, (date, value) ->
                    val x = spacing + i * spacePerPoint
                    val normalizedValue = normalizeValue(value, exchangeRates, size.height)
                    val y = size.height - normalizedValue
                    drawCircle(
                        color = Color.Black,
                        radius = 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                    // Resalta el punto seleccionado
                    if (selectedPoint == (date to value)) {
                        drawCircle(
                            color = Color.White,
                            radius = 8.dp.toPx(), // Círculo blanco más grande
                            center = Offset(x, y)
                        )
                    }
                }
            }

            if (showPopup && selectedPoint != null) {
                DropdownMenu(
                    expanded = showPopup,
                    onDismissRequest = { showPopup = false }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        val date = selectedPoint?.first?.let { dateFormat.format(it) }.orEmpty()
                        val currencyValue = selectedPoint?.second ?: 0.0f

                        Text(stringResource(R.string.date, date))
                        Text(stringResource(R.string.currency_value, currencyValue))
                    }
                }
            }
        }
    }
}

private fun normalizeValue(value: Float, exchangeRates: List<Pair<Date, Float>>, canvasHeight: Float): Float {
    val minValue = exchangeRates.minOf { it.second }
    val maxValue = exchangeRates.maxOf { it.second }
    val valueRange = maxValue - minValue
    return if (valueRange == 0f) {
        0f
    } else {
        (value - minValue) / valueRange * canvasHeight
    }
}


@Composable
fun ComplexLineChart() {
    val chartEntryModel = entryModelOf(Pair(12, 3f), Pair(13, 4f), Pair(14, 5f), Pair(15, 6f))

    Column {
        Chart(
            chart = lineChart(),
            model = chartEntryModel,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        )
    }
}

val customPointShape = ShapeComponent(
    shape = Shapes.pillShape,
    color = Color.Red.toArgb(),
    strokeWidthDp = 1f,
    strokeColor = Color.Black.toArgb()
)

@Composable
fun CurrencyLineChart(currencyData: List<Pair<Date, Float>>) {

    // Create the ChartEntryModel directly
    val chartEntryModel = remember(currencyData) {
        entryModelOf(
            *currencyData.mapIndexed { index, (date, value) ->
                // Create Pair<Number, Number>
                index.toFloat() to value
            }.toTypedArray()
        )
    }

    val customDataLabel = textComponent {
        color = Color.Red.toArgb()
        textSizeSp = 14f
        typeface = Typeface.DEFAULT_BOLD
    }

    Column {
        Chart(
            chart = lineChart(
                lines = listOf(
                    LineSpec(
                        lineColor = Color.Green.toArgb(),
                        lineThicknessDp = 2.0f,
                        point = ShapeComponent(shape = Shapes.pillShape, strokeWidthDp = 0.5f, color = Color.DarkGray.toArgb()),
                        pointSizeDp = 8f,
                    )
                ),
                spacing = 10.dp
            ),
            model = chartEntryModel,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            startAxis = rememberStartAxis(
                label = axisLabelComponent()
            ),
            bottomAxis = rememberBottomAxis(
                label = axisLabelComponent()
            ),
            isZoomEnabled = true,
            autoScaleUp = AutoScaleUp.Full,
            legend = VerticalLegend(
                iconPaddingDp = 10.0f,
                iconSizeDp = 10.0f,
                items = listOf(LegendItem(icon = customPointShape, label = customDataLabel, labelText = "Currency"))
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyLineChartPreview() {
    val calendar = Calendar.getInstance()
    val sampleData = listOf(
        Pair(calendar.time, 19.5f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 19.8f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 19.2f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 20.1f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 19.7f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 19.8f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 18.2f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 21.5f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 20.0f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 19.7f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 19.3f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 20.8f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 19.8f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 18.2f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 15.5f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 12.0f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 8.7f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 5.1f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 2.3f),
        Pair(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time, 0.0f)
    )
    CurrencyLineChart(currencyData = sampleData)
}


@Preview(showBackground = true)
@Composable
fun ComplexLineChartPreview() {
    MaterialTheme {
        ComplexLineChart()
    }
}