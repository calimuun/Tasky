package com.calimuun.tasky.ui.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun AlertDialogSimp(
	title: String,
	icon: Painter? = null,
	positiveButtonText: String,
	positiveButtonAction: () -> Unit,
	negativeButtonText: String,
	negativeButtonAction: () -> Unit,
	onDismissRequest: () -> Unit,
	content: @Composable () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismissRequest,
		icon = {
			if (icon != null) Icon(painter = icon, contentDescription = null)
		},
		title = { Text(title) },
		text = content,
		confirmButton = {
			TextButton(
				onClick = positiveButtonAction
			) {
				Text(positiveButtonText)
			}
		},
		dismissButton = {
			TextButton(onClick = negativeButtonAction) {
				Text(negativeButtonText)
			}
		}
	)
}