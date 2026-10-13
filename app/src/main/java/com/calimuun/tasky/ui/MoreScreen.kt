package com.calimuun.tasky.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calimuun.tasky.App
import com.calimuun.tasky.BuildConfig
import com.calimuun.tasky.R
import com.calimuun.tasky.database.TaskRepository
import com.calimuun.tasky.ui.utils.OptionRow
import com.calimuun.tasky.utils.AppDataStore
import com.calimuun.tasky.viewmodel.RoutineViewModel
import com.calimuun.tasky.viewmodel.RoutineViewModelFactory
import com.calimuun.tasky.viewmodel.TaskViewModel
import com.calimuun.tasky.viewmodel.TaskViewModelFactory

@Composable
fun MoreScreen() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.padding(
				end = 10.dp,
				start = 10.dp
			)
	) {
		val context = LocalContext.current
		val app = context.applicationContext as App
		val db = app.database
		val coroutineScope = rememberCoroutineScope()
		val dataStore = AppDataStore(context)

		val taskViewModel: TaskViewModel = viewModel(
			factory = TaskViewModelFactory(
				db.taskDao(),
				TaskRepository(
					db,
					db.taskDao(),
					db.taskCompletedDao()
				)
			)
		)

		val routineViewModel: RoutineViewModel = viewModel(
			factory = RoutineViewModelFactory(db.routineDao())
		)

		// TODO: parte lógica dos botões, datastore, etc

		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 10.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceContainer
			),
			border = null
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(10.dp)
			) {
				Image(
					painter = painterResource(id = R.drawable.notification_icon),
					contentDescription = null,
					colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inverseOnSurface),
					modifier = Modifier
						.width(90.dp)
						.height(70.dp)
						.padding(end = 20.dp)
						.background(
							color = MaterialTheme.colorScheme.onSurface,
							shape = CircleShape
						)
						.padding(10.dp)
				)

				Column() {
					Text(
						text = stringResource(R.string.app_name),
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(bottom = 2.dp)
					)

					Text(
						text = stringResource(R.string.app_description),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(bottom = 1.dp)
					)

					Text(
						text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurface
					)
				}
			}
		}

		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceContainer
			),
			border = null
		) {
			Column {
				OptionRow(
					mainIconResId = R.drawable.ic_done,
					name = stringResource(id = R.string.completed_tasks_option),
					hasAsideIcon = true,
					onClick = {}
				)

				HorizontalDivider(
					color = MaterialTheme.colorScheme.surface
				)

				OptionRow(
					mainIconResId = R.drawable.ic_person,
					name = stringResource(id = R.string.change_username_option),
					hasAsideIcon = true,
					onClick = {}
				)

				HorizontalDivider(
					color = MaterialTheme.colorScheme.surface
				)

				OptionRow(
					mainIconResId = R.drawable.ic_clock,
					name = stringResource(id = R.string.change_default_notification_time_option),
					hasAsideIcon = true,
					onClick = {}
				)

				HorizontalDivider(
					color = MaterialTheme.colorScheme.surface
				)

				OptionRow(
					mainIconResId = R.drawable.ic_notifications,
					name = stringResource(id = R.string.receive_notifications_option),
					hasAsideIcon = true,
					onClick = {}
				)

				HorizontalDivider(
					color = MaterialTheme.colorScheme.surface,
				)

				OptionRow(
					mainIconResId = R.drawable.ic_delete_forever,
					name = stringResource(id = R.string.delete_all_tasks_option),
					onClick = {}
				)

				HorizontalDivider(
					color = MaterialTheme.colorScheme.surface
				)

				OptionRow(
					mainIconResId = R.drawable.ic_delete_forever,
					name = stringResource(id = R.string.delete_all_routines_option),
					onClick = {}
				)
			}
		}
	}
}

@Preview(
	showBackground = true
)
@Composable
fun PreviewMoreScreen() {
	MaterialTheme {
		MoreScreen()
	}
}