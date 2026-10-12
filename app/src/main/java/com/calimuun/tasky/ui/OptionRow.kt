package com.calimuun.tasky.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.calimuun.tasky.R

@Composable
fun OptionRow(
	mainIconResId: Int,
	name: String,
	hasAsideIcon: Boolean = false,
	onClick: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.defaultMinSize(minHeight = 48.dp)
			.clickable(onClick = onClick)
			.padding(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			painter = painterResource(id = mainIconResId),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.padding(end = 10.dp)
		)

		Text(
			text = name,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.weight(1f)
		)

		if (hasAsideIcon) {
			Icon(
				painter = painterResource(id = R.drawable.ic_chevron_right), // TODO: remover e usar Icon
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}