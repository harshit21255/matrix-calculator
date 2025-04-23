package com.example.matrixcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

val DarkBackground = Color(0xFF0F1316)
val DarkText = Color(0xFF122026)
val LightText = Color(0xFFD9EDDF)
val LightGreen = Color(0xFF23E09C)
val LightBlue = Color(0xFF27AAF4)
val RedColor = Color(0xFFDE5753)
val White = Color(0xFFFFFFFF)

class MainActivity : ComponentActivity() {
    init {
        System.loadLibrary("matrixoperations")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = LightGreen,
                    onPrimary = DarkText,
                    secondary = LightText,
                    onSecondary = DarkText,
                    tertiary = LightBlue,
                    background = DarkBackground,
                    surface = DarkText,
                    onSurface = White,
                    error = RedColor,
                    onError = White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    MatrixCalculatorApp()
                }
            }
        }
    }
}

class MatrixViewModel : ViewModel() {
    var rowsA by mutableStateOf("2")
    var colsA by mutableStateOf("2")
    var rowsB by mutableStateOf("2")
    var colsB by mutableStateOf("2")

    private val _matrixA = mutableStateOf(createEmptyMatrix(2, 2))
    val matrixA: List<List<String>> get() = _matrixA.value

    private val _matrixB = mutableStateOf(createEmptyMatrix(2, 2))
    val matrixB: List<List<String>> get() = _matrixB.value

    var resultMatrix by mutableStateOf<Array<Array<String>>>(Array(2) { Array(2) { "0" } })

    var selectedOperation by mutableStateOf(MatrixOperation.ADD)
    var calculationPerformed by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private fun createEmptyMatrix(rows: Int, cols: Int): List<List<String>> {
        return List(rows) { List(cols) { "" } }
    }

    fun updateMatrixADimensions(rows: Int, cols: Int) {
        val newMatrix = List(rows) { i ->
            List(cols) { j ->
                if (i < matrixA.size && j < matrixA[0].size) matrixA[i][j] else ""
            }
        }
        _matrixA.value = newMatrix
    }

    fun updateMatrixBDimensions(rows: Int, cols: Int) {
        val newMatrix = List(rows) { i ->
            List(cols) { j ->
                if (i < matrixB.size && j < matrixB[0].size) matrixB[i][j] else ""
            }
        }
        _matrixB.value = newMatrix
    }

    fun updateMatrixAValue(row: Int, col: Int, value: String) {
        if (row < matrixA.size && col < matrixA[0].size) {
            val newMatrix = matrixA.mapIndexed { i, rowList ->
                rowList.mapIndexed { j, cellValue ->
                    if (i == row && j == col) value else cellValue
                }
            }
            _matrixA.value = newMatrix
        }
    }

    fun updateMatrixBValue(row: Int, col: Int, value: String) {
        if (row < matrixB.size && col < matrixB[0].size) {
            val newMatrix = matrixB.mapIndexed { i, rowList ->
                rowList.mapIndexed { j, cellValue ->
                    if (i == row && j == col) value else cellValue
                }
            }
            _matrixB.value = newMatrix
        }
    }

    fun performCalculation() {
        calculationPerformed = true
        errorMessage = ""

        try {
            val matrixADouble = matrixA.map { row ->
                row.map {
                    it.takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 0.0
                }.toDoubleArray()
            }.toTypedArray()

            val matrixBDouble = matrixB.map { row ->
                row.map {
                    it.takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 0.0
                }.toDoubleArray()
            }.toTypedArray()

            when (selectedOperation) {
                MatrixOperation.ADD -> {
                    if (matrixA.size != matrixB.size || matrixA[0].size != matrixB[0].size) {
                        errorMessage = "⚠\uFE0F Matrices must have the same dimensions for addition"
                        return
                    }
                    val result = addMatrices(matrixADouble, matrixBDouble)
                    resultMatrix = Array(result.size) { i ->
                        Array(result[0].size) { j ->
                            result[i][j].toString()
                        }
                    }
                }
                MatrixOperation.SUBTRACT -> {
                    if (matrixA.size != matrixB.size || matrixA[0].size != matrixB[0].size) {
                        errorMessage = "⚠\uFE0F Matrices must have the same dimensions for subtraction"
                        return
                    }
                    val result = subtractMatrices(matrixADouble, matrixBDouble)
                    resultMatrix = Array(result.size) { i ->
                        Array(result[0].size) { j ->
                            result[i][j].toString()
                        }
                    }
                }
                MatrixOperation.MULTIPLY -> {
                    if (matrixA[0].size != matrixB.size) {
                        errorMessage = "⚠\uFE0F Number of columns in first matrix must equal number of rows in second matrix for multiplication"
                        return
                    }
                    val result = multiplyMatrices(matrixADouble, matrixBDouble)
                    resultMatrix = Array(result.size) { i ->
                        Array(result[0].size) { j ->
                            result[i][j].toString()
                        }
                    }
                }
                MatrixOperation.DIVIDE -> {
                    if (matrixA[0].size != matrixB.size || matrixB.size != matrixB[0].size) {
                        errorMessage = "⚠\uFE0FSecond matrix must be square and its rows must match columns of first matrix, for division"
                        return
                    }
                    try {
                        val result = divideMatrices(matrixADouble, matrixBDouble)
                        resultMatrix = Array(result.size) { i ->
                            Array(result[0].size) { j ->
                                result[i][j].toString()
                            }
                        }
                    } catch (e: Exception) {
                        errorMessage = "Matrix division error: ${e.message ?: "Second matrix might be singular"}"
                    }
                }
            }
        } catch (e: Exception) {
            errorMessage = "Calculation error: ${e.message}"
        }
    }

    external fun addMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
    external fun subtractMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
    external fun multiplyMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
    external fun divideMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
}

enum class MatrixOperation {
    ADD, SUBTRACT, MULTIPLY, DIVIDE
}

@Composable
fun MatrixCalculatorApp(viewModel: MatrixViewModel = viewModel()) {
    var currentStep by remember { mutableStateOf(Step.DIMENSIONS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Matrix Calculator",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = LightGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (currentStep) {
            Step.DIMENSIONS -> DimensionsInputScreen(viewModel) {
                currentStep = Step.MATRIX_A_INPUT
            }
            Step.MATRIX_A_INPUT -> MatrixInputScreen(
                title = "Enter Matrix A",
                rows = viewModel.rowsA.toIntOrNull() ?: 2,
                cols = viewModel.colsA.toIntOrNull() ?: 2,
                matrix = viewModel.matrixA,
                onMatrixValueChange = { row, col, value ->
                    viewModel.updateMatrixAValue(row, col, value)
                },
                onNext = { currentStep = Step.MATRIX_B_INPUT }
            )
            Step.MATRIX_B_INPUT -> MatrixInputScreen(
                title = "Enter Matrix B",
                rows = viewModel.rowsB.toIntOrNull() ?: 2,
                cols = viewModel.colsB.toIntOrNull() ?: 2,
                matrix = viewModel.matrixB,
                onMatrixValueChange = { row, col, value ->
                    viewModel.updateMatrixBValue(row, col, value)
                },
                onNext = { currentStep = Step.OPERATION_SELECTION }
            )
            Step.OPERATION_SELECTION -> OperationSelectionScreen(viewModel) {
                currentStep = Step.RESULT
            }
            Step.RESULT -> ResultScreen(viewModel) {
                viewModel.calculationPerformed = false
                currentStep = Step.DIMENSIONS
            }
        }
    }
}

enum class Step {
    DIMENSIONS, MATRIX_A_INPUT, MATRIX_B_INPUT, OPERATION_SELECTION, RESULT
}

@Composable
fun DimensionsInputScreen(viewModel: MatrixViewModel, onNext: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter Matrix Dimensions",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("Matrix A Dimensions", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.rowsA,
                    onValueChange = {
                        val newValue = it.filter { c -> c.isDigit() }
                        if (newValue.isNotEmpty()) {
                            val rows = newValue.toIntOrNull() ?: 2
                            if (rows > 0) {
                                viewModel.rowsA = newValue
                                val cols = viewModel.colsA.toIntOrNull() ?: 2
                                viewModel.updateMatrixADimensions(rows, cols)
                            }
                        }
                    },
                    label = { Text("Rows") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )

                Text("×", style = MaterialTheme.typography.bodyLarge)

                OutlinedTextField(
                    value = viewModel.colsA,
                    onValueChange = {
                        val newValue = it.filter { c -> c.isDigit() }
                        if (newValue.isNotEmpty()) {
                            val cols = newValue.toIntOrNull() ?: 2
                            if (cols > 0) {
                                viewModel.colsA = newValue
                                val rows = viewModel.rowsA.toIntOrNull() ?: 2
                                viewModel.updateMatrixADimensions(rows, cols)
                            }
                        }
                    },
                    label = { Text("Columns") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Matrix B Dimensions", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.rowsB,
                    onValueChange = {
                        val newValue = it.filter { c -> c.isDigit() }
                        if (newValue.isNotEmpty()) {
                            val rows = newValue.toIntOrNull() ?: 2
                            if (rows > 0) {
                                viewModel.rowsB = newValue
                                val cols = viewModel.colsB.toIntOrNull() ?: 2
                                viewModel.updateMatrixBDimensions(rows, cols)
                            }
                        }
                    },
                    label = { Text("Rows") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )

                Text("×", style = MaterialTheme.typography.bodyLarge)

                OutlinedTextField(
                    value = viewModel.colsB,
                    onValueChange = {
                        val newValue = it.filter { c -> c.isDigit() }
                        if (newValue.isNotEmpty()) {
                            val cols = newValue.toIntOrNull() ?: 2
                            if (cols > 0) {
                                viewModel.colsB = newValue
                                val rows = viewModel.rowsB.toIntOrNull() ?: 2
                                viewModel.updateMatrixBDimensions(rows, cols)
                            }
                        }
                    },
                    label = { Text("Columns") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    "Next: Enter Matrix Values",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun MatrixInputScreen(
    title: String,
    rows: Int,
    cols: Int,
    matrix: List<List<String>>,
    onMatrixValueChange: (row: Int, col: Int, value: String) -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Spacer(modifier = Modifier.height(32.dp))

            for (i in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (j in 0 until cols) {
                        val cellValue = if (i < matrix.size && j < matrix[i].size) matrix[i][j] else ""

                        OutlinedTextField(
                            value = cellValue,
                            onValueChange = { newValue -> onMatrixValueChange(i, j, newValue) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    "Next",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun OperationSelectionScreen(viewModel: MatrixViewModel, onNext: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Operation",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.selectedOperation = MatrixOperation.ADD },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedOperation == MatrixOperation.ADD)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            "Add",
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Button(
                        onClick = { viewModel.selectedOperation = MatrixOperation.SUBTRACT },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedOperation == MatrixOperation.SUBTRACT)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            "Subtract",
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.selectedOperation = MatrixOperation.MULTIPLY },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedOperation == MatrixOperation.MULTIPLY)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            "Multiply",
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Button(
                        onClick = { viewModel.selectedOperation = MatrixOperation.DIVIDE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedOperation == MatrixOperation.DIVIDE)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            "Divide",
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.performCalculation()
                    onNext()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    "Calculate",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun ResultScreen(viewModel: MatrixViewModel, onNewCalculation: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Result",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (viewModel.errorMessage.isNotEmpty()) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                for (i in viewModel.resultMatrix.indices) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in viewModel.resultMatrix[0].indices) {
                            val formattedValue = formatNumberResult(viewModel.resultMatrix[i][j])
                            Text(
                                text = formattedValue,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNewCalculation,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    "New Calculation",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}


fun formatNumberResult(numberString: String): String {
    return try {
        val number = numberString.toDouble()
        if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            String.format("%.2f", number)
        }
    } catch (e: NumberFormatException) {
        numberString
    }
}