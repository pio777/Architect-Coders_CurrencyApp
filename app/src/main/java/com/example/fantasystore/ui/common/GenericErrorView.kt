package com.example.fantasystore.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fantasystore.R
import com.example.fantasystore.domain.ErrorType

@Composable
fun GenericErrorView(
    modifier: Modifier = Modifier,
    errorType: ErrorType,
    errorMessage: String? = null,
    errorCode: Int? = null,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier
            //   .fillMaxSize()
            .clickable(enabled = false, onClick = {})
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {// Close button at the top right
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.Gray // Or any color you prefer
                    )
                }
            }

            // Rest of the content centered
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp), // Add top padding to account for close button
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = stringResource(R.string.error_icon_description),
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when (errorType) {
                        ErrorType.Network -> stringResource(R.string.no_internet_connection_error)
                        ErrorType.Timeout -> stringResource(R.string.request_timed_out_error)
                        ErrorType.Unknown -> stringResource(R.string.unknown_error)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                // Display optional error message and code
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.message, errorMessage),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
                if (errorCode != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.error_code, errorCode),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Push button to bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GenericErrorViewPreview() {
    GenericErrorView(
        errorType = ErrorType.Network,
        onRetry = {},
        onClose = {})
}

@Preview(showBackground = true)
@Composable
fun GenericErrorViewWithParamsPreview() {
    GenericErrorView(
        errorType = ErrorType.Timeout,
        errorMessage = "Could not connect to server",
        errorCode = 500,
        onRetry = {},
        onClose = {})
}