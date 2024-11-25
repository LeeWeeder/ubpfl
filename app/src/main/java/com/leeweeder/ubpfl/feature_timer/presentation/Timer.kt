package com.leeweeder.ubpfl.feature_timer.presentation

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leeweeder.ubpfl.R
import com.leeweeder.ubpfl.feature_timer.data.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScaffold(
    state: TimerScaffoldState = rememberTimerScaffoldState(),
    recentTimerDurationsState: RecentTimerDurationsState,
    onTimerStart: (duration: Int) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val isTimerVisible = state.isVisible
    LaunchedEffect(key1 = isTimerVisible) {
        // If the timer is hidden by user, hide the bottom sheet
        if (!isTimerVisible) {
            state.hide()
        } else {
            state.show()
        }
    }

    var bottomSheetHeight by remember {
        mutableStateOf(0.dp)
    }

    val density = LocalDensity.current

    BottomSheetScaffold(
        scaffoldState = state.bottomSheetScaffoldState,
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                Text(
                    text = state.workoutDurationSeconds.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }, actions = {
                Box(contentAlignment = Alignment.BottomEnd) {
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current
                    FilledIconToggleButton(checked = isTimerVisible, onCheckedChange = {
                        state.toggleVisibility(it)

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
                    androidx.compose.animation.AnimatedVisibility(visible = state.isTimerActive) {
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
            })
        },
        sheetContent = {
            TimerBottomSheet(
                recentTimerDurationsState = recentTimerDurationsState,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        bottomSheetHeight = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    },
                state = state,
                onTimerStart = onTimerStart
            )
        },
        sheetDragHandle = null,
        sheetSwipeEnabled = true,
        sheetShape = RectangleShape,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) { paddingValues ->
        val bottomPadding by animateDpAsState(targetValue = if (state.isVisible) bottomSheetHeight - with(
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

class TimerScaffoldState @OptIn(ExperimentalMaterial3Api::class) constructor(
    initialWorkoutDurationSeconds: Int,
    timerSheetVisibilityState: TimerSheetVisibilityState,
    private val scope: CoroutineScope,
    val bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    private lateinit var timer: CountDownTimer
    val timerSheetVisibilityState: TimerSheetVisibilityState
        get() = _timerSheetVisibilityState.value

    val isVisible: Boolean
        get() = timerSheetVisibilityState is TimerSheetVisibilityState.Shown

    var workoutDurationSeconds: Int
        get() = _workoutDurationSeconds.intValue
        set(value) {
            _workoutDurationSeconds.intValue = value
        }

    val isTimerActive: Boolean
        get() = false

    val timerValue: TimerValue
        get() = _timerValue.value

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
        if (state == TimerSheetState.Timer) {
            startTimer()
        } else if (state == TimerSheetState.Preparation) {
            startPreparationTimer()
        }

        scope.launch {
            show()
        }

        _timerSheetState.value = state
    }

    fun startTimer() {
        timer = CountDownTimer(
            duration = workoutDurationSeconds * 1000L,
            onTick = { secondsRemaining ->
                _timerValue.value = TimerValue(secondsRemaining / 60, secondsRemaining % 60)
            },
            onTimerFinish = {
                setTimerSheetState(state = TimerSheetState.Configuration)
            }
        )
        timer.start()
    }

    fun startPreparationTimer() {
        timer = CountDownTimer(
            duration = 10 * 1000L,
            onTick = { secondsRemaining ->
                _timerValue.value = TimerValue(secondsRemaining / 60, secondsRemaining % 60)
            },
            onTimerFinish = {
                setTimerSheetState(state = TimerSheetState.Timer)
                startTimer()
            }
        )
    }

    fun stop() {
        timer.cancel()
        setTimerSheetState(state = TimerSheetState.Configuration)
        _timerValue.value = TimerValue(workoutDurationSeconds / 60, workoutDurationSeconds % 60)
    }

    private var _timerSheetVisibilityState = mutableStateOf(timerSheetVisibilityState)
    private var _timerSheetState = mutableStateOf(TimerSheetState.Configuration)
    private var _workoutDurationSeconds = mutableIntStateOf(initialWorkoutDurationSeconds)
    private var _timerValue =
        mutableStateOf(TimerValue(workoutDurationSeconds / 60, workoutDurationSeconds % 60))
}

data class TimerValue(
    val minutes: Int,
    val seconds: Int
)

@Composable
@ExperimentalMaterial3Api
fun rememberTimerScaffoldState(
    initialWorkoutDurationSeconds: Int = 0,
    initialTimerStateValue: TimerSheetVisibilityState = TimerSheetVisibilityState.Hidden
): TimerScaffoldState {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false
        )
    )
    val scope = rememberCoroutineScope()
    return remember(initialWorkoutDurationSeconds, initialTimerStateValue) {
        TimerScaffoldState(
            initialWorkoutDurationSeconds = initialWorkoutDurationSeconds,
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
    state: TimerScaffoldState,
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
        when (state.timerSheetState) {
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
                                            state.workoutDurationSeconds = it
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

                var minutes by remember(state.workoutDurationSeconds) {
                    mutableStateOf((state.workoutDurationSeconds / 60).toString())
                }

                var seconds by remember(state.workoutDurationSeconds) {
                    mutableStateOf(if (state.workoutDurationSeconds == 0) "30" else (state.workoutDurationSeconds % 60).toString())
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
                            state.workoutDurationSeconds = duration
                            state.setTimerSheetState(
                                if (shouldPreparationCountdown) TimerSheetState.Preparation else TimerSheetState.Timer
                            )
                            onTimerStart(duration)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Start")
                }
            }

            TimerSheetState.Preparation -> {
                Column {
                    OutlinedCard {

                    }
                    Row {
                        Button(onClick = {

                        }) { Text("Pause") }
                        Button(onClick = {
                            state.stop()
                        }) { Text("Stop") }
                    }
                }
            }

            TimerSheetState.Timer -> {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val remainingTimeColor = MaterialTheme.colorScheme.tertiary
                    val border = CardDefaults.outlinedCardBorder()
                    val density = LocalDensity.current
                    val cornerRadius = CornerRadius(with(density) {
                        12.dp.toPx()
                    })
                    val duration = state.workoutDurationSeconds

                    var isInitialization by remember {
                        mutableStateOf(true)
                    }

                    val totalSeconds by remember {
                        derivedStateOf {
                            state.timerValue.minutes * 60 + state.timerValue.seconds
                        }
                    }

                    val progress by remember {
                        derivedStateOf {
                            totalSeconds / duration.toFloat()
                        }
                    }

                    val progressPercentage by animateFloatAsState(
                        targetValue = if (totalSeconds == 0 && isInitialization) {
                            isInitialization = false
                            1f
                        } else {
                            progress
                        },
                        label = "Timer progress animation",
                        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                    )

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
                                        width = progressPercentage * size.width,
                                        size.height
                                    ),
                                    cornerRadius = cornerRadius
                                )
                                drawRoundRect(
                                    brush = border.brush,
                                    style = Stroke(width = with(density) { border.width.toPx() }),
                                    cornerRadius = cornerRadius
                                )
                            }

                    )
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
                                state.stop()
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
        }
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