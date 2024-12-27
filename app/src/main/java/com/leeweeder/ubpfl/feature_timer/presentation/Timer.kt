package com.leeweeder.ubpfl.feature_timer.presentation

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.leeweeder.ubpfl.R
import com.leeweeder.ubpfl.feature_timer.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScaffold(
    timerScaffoldState: TimerScaffoldState = rememberTimerScaffoldState(),
    timerState: TimerState,
    recentTimerDurationsState: RecentTimerDurationsState,
    onTimerStart: (duration: Int) -> Unit,
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val isTimerVisible = timerScaffoldState.isVisible
    LaunchedEffect(key1 = isTimerVisible) {
        // If the timer is hidden by user, hide the bottom sheet
        if (!isTimerVisible) {
            timerScaffoldState.hide()
        } else {
            timerScaffoldState.show()
        }
    }

    var bottomSheetHeight by remember {
        mutableStateOf(0.dp)
    }

    val density = LocalDensity.current

    BottomSheetScaffold(
        scaffoldState = timerScaffoldState.bottomSheetScaffoldState,
        topBar = topBar,
        sheetContent = {
            TimerBottomSheet(
                recentTimerDurationsState = recentTimerDurationsState,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        bottomSheetHeight = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    },
                timerScaffoldState = timerScaffoldState,
                onTimerStart = onTimerStart,
                timerState = timerState
            )
        },
        sheetDragHandle = null,
        sheetSwipeEnabled = false,
        sheetShape = RectangleShape,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) { paddingValues ->
        val bottomPadding by animateDpAsState(targetValue = if (timerScaffoldState.isVisible) bottomSheetHeight - with(
            density
        ) {
            WindowInsets.navigationBars.getBottom(density).toDp()
        } else 0.dp, label = "Bottom padding")
        val layoutDirection = LocalLayoutDirection.current
        val newPaddingValues = PaddingValues(
            top = paddingValues.calculateTopPadding(),
            start = paddingValues.calculateStartPadding(layoutDirection),
            bottom = paddingValues.calculateBottomPadding() + bottomPadding,
            end = paddingValues.calculateEndPadding(layoutDirection)
        )
        content(newPaddingValues)
    }
}

/**
 * The state for the timer.
 *
 * @property totalDurationSeconds The overall duration of the timer.
 * @property currentTimerValue The current value of the timer. Will change every second if the timer is running.
 * */
class TimerState(
    private val timerScaffoldState: TimerScaffoldState
) {
    private lateinit var timer: CountDownTimer
    val isTimerActive: Boolean
        get() = _isTimerActive.value

    val currentTimerValue: TimerValue
        get() = _timerValue.value

    val totalDurationSeconds: Int
        get() = _totalDurationSeconds.intValue

    fun setDuration(totalDurationSeconds: Int) {
        _totalDurationSeconds.intValue = totalDurationSeconds
    }

    fun startTimer() {
        timerScaffoldState.setTimerSheetState(TimerSheetState.Timer)
        timer = CountDownTimer(
            duration = totalDurationSeconds * 1000L,
            onTick = { secondsRemaining ->
                _timerValue.value = TimerValue(secondsRemaining / 60, secondsRemaining % 60)
            },
            onTimerFinish = {
                timerScaffoldState.setTimerSheetState(TimerSheetState.Configuration)
                _isTimerActive.value = false
            }
        )
        timer.start()
        _isTimerActive.value = true
    }

    fun startPreparationTimer() {
        timerScaffoldState.setTimerSheetState(TimerSheetState.Preparation)
        val totalDurationSecondsTemp = _totalDurationSeconds.intValue
        _totalDurationSeconds.intValue = 5
        Log.d("Total duration should be 5", "${_totalDurationSeconds.intValue}")
        timer = CountDownTimer(
            duration = 5 * 1000L,
            onTick = { secondsRemaining ->
                _timerValue.value = TimerValue(secondsRemaining / 60, secondsRemaining % 60)
            },
            onTimerFinish = {
                timerScaffoldState.setTimerSheetState(TimerSheetState.Timer)
                _totalDurationSeconds.intValue = totalDurationSecondsTemp
                startTimer()
            }
        )
        timer.start()
        _isTimerActive.value = true
    }

    fun stop() {
        timer.cancel()
        timerScaffoldState.setTimerSheetState(TimerSheetState.Configuration)
        _timerValue.value = TimerValue(totalDurationSeconds / 60, totalDurationSeconds % 60)
        _isTimerActive.value = false
    }

    private var _totalDurationSeconds = mutableIntStateOf(0)
    private var _timerValue =
        mutableStateOf(TimerValue(totalDurationSeconds / 60, totalDurationSeconds % 60))
    private var _isTimerActive = mutableStateOf(false)
}

@Composable
@ExperimentalMaterial3Api
fun rememberTimerState(timerScaffoldState: TimerScaffoldState): TimerState {
    return remember {
        TimerState(
            timerScaffoldState = timerScaffoldState
        )
    }
}

class TimerScaffoldState @OptIn(ExperimentalMaterial3Api::class) constructor(
    timerSheetVisibilityState: TimerSheetVisibilityState,
    private val scope: CoroutineScope,
    val bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    val timerSheetVisibilityState: TimerSheetVisibilityState
        get() = _timerSheetVisibilityState.value

    val isVisible: Boolean
        get() = timerSheetVisibilityState is TimerSheetVisibilityState.Shown

    val timerSheetState: TimerSheetState
        get() = _timerSheetState.value

    @OptIn(ExperimentalMaterial3Api::class)
    suspend fun show() {
        bottomSheetScaffoldState.bottomSheetState.expand()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    suspend fun hide() {
        bottomSheetScaffoldState.bottomSheetState.hide()
    }

    fun toggleVisibility(visible: Boolean) {
        _timerSheetVisibilityState.value =
            if (visible) TimerSheetVisibilityState.Shown(timerSheetState) else TimerSheetVisibilityState.Hidden
    }

    fun setTimerSheetState(state: TimerSheetState) {
        scope.launch {
            show()
        }

        _timerSheetState.value = state
    }


    private var _timerSheetVisibilityState = mutableStateOf(timerSheetVisibilityState)
    private var _timerSheetState = mutableStateOf(TimerSheetState.Configuration)
}

data class TimerValue(
    val minutes: Int,
    val seconds: Int
) {
    fun format() = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Composable
@ExperimentalMaterial3Api
fun rememberTimerScaffoldState(
    initialTimerStateValue: TimerSheetVisibilityState = TimerSheetVisibilityState.Hidden
): TimerScaffoldState {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false
        )
    )
    val scope = rememberCoroutineScope()
    return remember(initialTimerStateValue) {
        TimerScaffoldState(
            timerSheetVisibilityState = initialTimerStateValue,
            bottomSheetScaffoldState = bottomSheetScaffoldState,
            scope = scope
        )
    }
}


sealed class TimerSheetVisibilityState {
    data class Shown(val state: TimerSheetState = TimerSheetState.Configuration) :
        TimerSheetVisibilityState()

    data object Hidden : TimerSheetVisibilityState()
}

enum class TimerSheetState {
    Configuration,
    Preparation,
    Timer
}

@Composable
private fun TimerBottomSheet(
    recentTimerDurationsState: RecentTimerDurationsState,
    timerScaffoldState: TimerScaffoldState,
    timerState: TimerState,
    onTimerStart: (duration: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding()
    ) {
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        when (timerScaffoldState.timerSheetState) {
            TimerSheetState.Configuration -> {
                AnimatedContent(
                    targetState = recentTimerDurationsState is RecentTimerDurationsState.Success,
                    label = "Recent timer duration"
                ) {
                    if (it) {
                        if ((recentTimerDurationsState as RecentTimerDurationsState.Success).durations.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "Recent",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(
                                        start = 16.dp, end = 16.dp, bottom = 16.dp
                                    ), horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(recentTimerDurationsState.durations.toList()) {
                                        SuggestionChip(onClick = {
                                            timerState.setDuration(it)
                                        },
                                            label = { Text(text = it.toString() + "s") })
                                    }
                                }
                            }
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }

                var minutes by remember(timerState.totalDurationSeconds) {
                    mutableStateOf((timerState.totalDurationSeconds / 60).toString())
                }

                var seconds by remember(timerState.totalDurationSeconds) {
                    mutableStateOf(if (timerState.totalDurationSeconds == 0) "30" else (timerState.totalDurationSeconds % 60).toString())
                }

                var isAutomaticallyFocused by remember { mutableStateOf(false) }

                val focusRequester = remember {
                    FocusRequester()
                }

                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    TimerTextField(
                        value = minutes,
                        onValueChange = { newValue, isCursorChange ->
                            minutes = newValue
                            val minutesInt = minutes.toIntOrNull()
                            if (!isAutomaticallyFocused && !isCursorChange) {
                                minutesInt?.let {
                                    if (minutes.length == 1 && minutesInt > 6 || minutes.length == 2 || minutesInt == 0) {
                                        focusRequester.requestFocus()
                                        // Set the flag to indicate that the focus is already automatically set
                                        isAutomaticallyFocused = true
                                    }
                                }
                            } else {
                                isAutomaticallyFocused = false
                            }
                        },
                        max = 60,
                        supportingText = "Minute",
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequester.requestFocus()
                        })
                    )
                    Box(
                        modifier = Modifier.size(width = 24.dp, height = 72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            ":",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current
                    TimerTextField(
                        value = seconds,
                        onValueChange = { newValue, _ ->
                            if (!(minutes.toIntOrNull() == 0 && newValue.toIntOrNull() == 0)) {
                                seconds = newValue
                            }
                        },
                        max = 60 - 1,
                        supportingText = "Seconds",
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }),
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                }
                var shouldPreparationCountdown by remember {
                    mutableStateOf(true)
                }

                ListItem(headlineContent = {
                    Text("Preparation countdown")
                }, trailingContent = {
                    Switch(checked = shouldPreparationCountdown, onCheckedChange = {
                        shouldPreparationCountdown = it
                    })
                }, colors = ListItemDefaults.colors(containerColor = Color.Transparent))
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        if (minutes.isEmpty()) {
                            minutes = "0"

                            if (seconds.isEmpty()) {
                                return@Button
                            }
                        }

                        if (seconds.isEmpty()) {
                            seconds = "0"
                        }

                        val duration = minutes.toInt() * 60 + seconds.toInt()
                        if (duration > 0) {
                            timerState.setDuration(duration)
                            if (shouldPreparationCountdown) {
                                timerState.startPreparationTimer()
                            } else {
                                timerState.startTimer()
                            }
                            onTimerStart(duration)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Start")
                }
            }

            TimerSheetState.Preparation -> {
                TimerSheetContent(
                    timerDirection = TimerProgressDirection.FORWARD,
                    timerState = timerState
                )
            }

            TimerSheetState.Timer -> {
                TimerSheetContent(
                    timerDirection = TimerProgressDirection.BACKWARD,
                    timerState = timerState
                )
            }
        }
    }
}

@Composable
private fun TimerSheetContent(
    timerDirection: TimerProgressDirection,
    timerState: TimerState
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TimerProgressBar(timerState, timerDirection)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {

                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) { Text("Pause") }
            Button(
                onClick = {
                    timerState.stop()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) { Text("Stop") }
        }
    }
}

/**
 * Specify in what way the direction of the timer progress bar goes.
 * */
enum class TimerProgressDirection {
    /**
     * The progress bar will be filled as the timer goes.
     */
    FORWARD,

    /**
     * The progress bar will be emptied as the timer goes.
     * */
    BACKWARD
}

/**
 * A progress bar for timer
 *
 * @param timerState The state of the timer.
 * @param direction The direction of the timer.
 * */
@Composable
private fun TimerProgressBar(timerState: TimerState, direction: TimerProgressDirection) {
    val remainingTimeColor = MaterialTheme.colorScheme.tertiary
    val border = CardDefaults.outlinedCardBorder()
    val density = LocalDensity.current
    val cornerRadius = CornerRadius(with(density) {
        12.dp.toPx()
    })
    val totalDuration by remember(timerState.totalDurationSeconds) {
        mutableIntStateOf(timerState.totalDurationSeconds)
    }

    val currentTimerValueInSeconds by remember {
        derivedStateOf {
            timerState.currentTimerValue.minutes * 60 + timerState.currentTimerValue.seconds
        }
    }

    var isInitialization by remember {
        mutableStateOf(true)
    }

    val progress by remember {
        derivedStateOf {
            // If the the current timer is 1 and the total duration is 3, the progress should be 0.333333
            // Thus, increasing timer value means increasing progress fill.
            // Else, the progress should be 0.666666

            if (direction == TimerProgressDirection.BACKWARD)
                currentTimerValueInSeconds / totalDuration.toFloat()
            else {
                1f - currentTimerValueInSeconds / totalDuration.toFloat()
            }
        }
    }

    val progressAnimation by animateFloatAsState(
        targetValue = if (isInitialization) {
            isInitialization = false
            if (direction == TimerProgressDirection.FORWARD) 0f else 1f
        } else {
            progress
        },
        label = "Timer progress animation",
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    val onSurface = MaterialTheme.colorScheme.onSurface
    val onTertiary = MaterialTheme.colorScheme.onTertiary

    Box(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
            .clip(CardDefaults.outlinedShape)
            .drawBehind {
                drawRoundRect(
                    color = remainingTimeColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(
                        width = progressAnimation * size.width,
                        size.height
                    ),
                    cornerRadius = cornerRadius
                )
                drawRoundRect(
                    brush = border.brush,
                    style = Stroke(width = with(density) { border.width.toPx() }),
                    cornerRadius = cornerRadius
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        brush = Brush.horizontalGradient(
                            progressAnimation to onTertiary,
                            progressAnimation to onSurface
                        )
                    )
                ) {
                    append(timerState.currentTimerValue.format())
                }
            },
            style = MaterialTheme.typography.displayMedium.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.TimerTextField(
    value: String,
    onValueChange: (String, Boolean) -> Unit,
    max: Int,
    supportingText: String,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier
) {
    fun isValid(text: String): Boolean {
        if (text.isEmpty()) {
            return true
        }

        if (text.startsWith('0') && text.length > 1) {
            return false
        }

        if (text.toIntOrNull() == null) {
            return false
        }

        if (text.toIntOrNull()!! > max) {
            return false
        }

        return true
    }

    Column(modifier = Modifier.weight(1f)) {
        val style = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center)
        val textSelectionColors = TextSelectionColors(
            handleColor = TextFieldDefaults.colors().cursorColor,
            backgroundColor = Color.Transparent
        )
        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }

            LaunchedEffect(value) {
                textFieldValue = textFieldValue.copy(text = value)
            }

            var textFieldShouldReset by remember {
                mutableStateOf(true)
            }

            var isFirstFocus by remember { mutableStateOf(true) }

            var shouldClearAll by remember { mutableStateOf(true) }

            var isDefaultSelection by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(isFirstFocus) {
                textFieldValue = textFieldValue.copy(selection = TextRange.Zero)
            }

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    val newText = newValue.text
                    val oldText = textFieldValue.text

                    if (textFieldShouldReset) {
                        val newTextFieldValue = if (newText.length > oldText.length) {
                            textFieldShouldReset = false

                            TextFieldValue(
                                text = newText[textFieldValue.selection.start].toString(),
                                selection = TextRange(newText.length)
                            )
                        } else {
                            if (isDefaultSelection == false) {
                                textFieldShouldReset = false
                                shouldClearAll = false
                            }

                            newValue
                        }

                        if (isValid(newTextFieldValue.text)) {
                            textFieldValue = newTextFieldValue
                        }
                    } else {
                        if (isValid(newValue.text)) {
                            textFieldValue = newValue
                        }
                    }

                    if (isDefaultSelection == true) {
                        isDefaultSelection = false
                    }

                    onValueChange(textFieldValue.text, newText == oldText)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = modifier
                    .height(height = 72.dp)
                    .onFocusChanged {
                        if (isFirstFocus && it.isFocused) {
                            isDefaultSelection = true
                            isFirstFocus = false
                        }

                        if (!it.isFocused) {
                            isFirstFocus = true

                            textFieldShouldReset = true

                            if (textFieldValue.text.toIntOrNull() == null) {
                                textFieldValue = textFieldValue.copy(text = "0")
                            }
                        }
                    }
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyUp && textFieldShouldReset) {
                            when (keyEvent.key) {
                                Key.Backspace -> {
                                    textFieldValue = textFieldValue.copy(
                                        text = "",
                                        selection = TextRange.Zero
                                    )
                                    onValueChange(textFieldValue.text, false)
                                    true
                                }

                                else -> false
                            }
                        } else {
                            false
                        }
                    },
                textStyle = style,
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = imeAction
                ),
                keyboardActions = keyboardActions
            )
        }
        Spacer(Modifier.height(7.dp))
        Text(
            supportingText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ToggleTimerVisibilityIconButton(
    timerScaffoldState: TimerScaffoldState,
    timerState: TimerState
) {
    Box(contentAlignment = Alignment.BottomEnd) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        FilledIconToggleButton(checked = timerScaffoldState.isVisible, onCheckedChange = {
            timerScaffoldState.toggleVisibility(it)

            if (!it) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.av_timer_24px),
                contentDescription = "Toggle timer bottom sheet visibility"
            )
        }
        AnimatedVisibility(visible = timerState.isTimerActive) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
                    .padding(3.dp)
            ) {
                Badge(
                    modifier = Modifier.size(8.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun WorkoutDurationText(workoutDurationSeconds: Int) {
    Text(
        text = String.format(
            locale = Locale.getDefault(),
            "%02d:%02d:%02d",
            workoutDurationSeconds / 60 / 60,
            workoutDurationSeconds / 60,
            workoutDurationSeconds % 60
        ),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 12.dp)
    )
}