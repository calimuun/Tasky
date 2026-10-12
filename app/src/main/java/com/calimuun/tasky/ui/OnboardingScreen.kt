package com.calimuun.tasky.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calimuun.tasky.R

@Composable
fun OnboardingScreen(
	onOnboardingFinish: (String) -> Unit,
	onRequestAlarmPermission: () -> Unit,
	onRequestNotificationPermission: () -> Unit
) {
	val pagerState = rememberPagerState(pageCount = { 6 })
	var username by remember { mutableStateOf("") }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		HorizontalPager(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth(),
			state = pagerState
		) { page ->
			when (page) {
				0 -> NormalOnboardingPage(
					title = stringResource(R.string.app_name),
					description = stringResource(R.string.onboard_1_description),
					iconResId = R.drawable.notification_icon
				)

				1 -> NormalOnboardingPage(
					title = stringResource(R.string.onboard_2_title),
					description = stringResource(R.string.onboard_2_description),
					iconResId = R.drawable.ic_task
				)

				2 -> NormalOnboardingPage(
					title = stringResource(R.string.onboard_3_title),
					description = stringResource(R.string.onboard_3_description),
					iconResId = R.drawable.ic_routine
				)

				3 -> PermissionOnboardingPage(
					onRequestNotificationPermission,
					onRequestAlarmPermission
				)

				4 -> FinalOnboardingPage(
					onOnboardingFinish = { onOnboardingFinish(username) },
					onUsernameChange = { username = it },
					username = username
				)

				5 -> NormalOnboardingPage(
					title = stringResource(R.string.app_name),
					description = stringResource(R.string.onboard_1_description),
					iconResId = R.drawable.notification_icon
				)
			}
		}
	}
}

@Composable
fun NormalOnboardingPage(
	title: String,
	description: String,
	iconResId: Int
) {
	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			painter = painterResource(id = iconResId),
			contentDescription = null,
			colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
			modifier = Modifier
				.width(200.dp)
				.height(200.dp)
		)

		Text(
			text = title,
			style = MaterialTheme.typography.displaySmall,
			fontWeight = FontWeight.Bold
		)

		Text(
			text = description,
			style = MaterialTheme.typography.bodySmall
		)
	}
}

@Composable
fun PermissionOnboardingPage(
	onRequestNotificationPermission: () -> Unit,
	onRequestAlarmPermission: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = stringResource(R.string.onboard_permission_page_text),
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.padding(bottom = 15.dp)
		)

		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceContainer
			),
			border = null
		) {
			Column {
				OptionRow(
					mainIconResId = R.drawable.ic_notifications,
					name = stringResource(id = R.string.notifications_permission),
					hasAsideIcon = true,
					onClick = onRequestNotificationPermission
				)

				HorizontalDivider(
					color = MaterialTheme.colorScheme.surface
				)

				OptionRow(
					mainIconResId = R.drawable.ic_alarm_clock,
					name = stringResource(id = R.string.alarms_permission),
					hasAsideIcon = true,
					onClick = onRequestAlarmPermission
				)
			}
		}
	}
}

@Composable
fun FinalOnboardingPage(
	onOnboardingFinish: () -> Unit,
	onUsernameChange: (String) -> Unit,
	username: String
) {
	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = stringResource(R.string.onboard_last_page_text),
			style = MaterialTheme.typography.displaySmall,
			modifier = Modifier.padding(bottom = 16.dp),
			color = MaterialTheme.colorScheme.onBackground
		)

		TextField(
			value = username,
			onValueChange = onUsernameChange,
			placeholder = { Text(text = stringResource(R.string.onboard_last_page_input_hint)) },
			modifier = Modifier.fillMaxWidth()
		)

		Spacer(Modifier.height(32.dp))

		Button(
			onClick = onOnboardingFinish
		) {
			Text(text = stringResource(R.string.onboard_last_page_button_text))
		}
	}
}