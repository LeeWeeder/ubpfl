package com.leeweeder.luxthenics.ui.progression_selection

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leeweeder.luxthenics.R
import com.leeweeder.luxthenics.api_program.asset.ExerciseCategory
import com.leeweeder.luxthenics.api_program.asset.formatName
import com.leeweeder.luxthenics.feature_progression.data.source.Progression
import kotlin.math.max

@Composable
fun ProgressionSelectionScreen(
    viewModel: ProgressionSelectionViewModel = hiltViewModel(), onCloseProgressionScreen: () -> Unit
) {
    val exerciseCategory = viewModel.exerciseCategory.value
    val temporarilySelectedProgression = viewModel.temporarilySelectedProgression.value
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val onEvent = viewModel::onEvent
    ProgressionSelectionScreen(
        exerciseCategory = exerciseCategory,
        temporarilySelectedProgression = temporarilySelectedProgression,
        onCloseProgressionScreen = onCloseProgressionScreen,
        uiState = uiState,
        onEvent = onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressionSelectionScreen(
    exerciseCategory: ExerciseCategory,
    temporarilySelectedProgression: Progression?,
    onCloseProgressionScreen: () -> Unit,
    uiState: ProgressionSelectionUiState,
    onEvent: (ProgressionSelectionEvent) -> Unit
) {
    val addEditProgressionDialogState = rememberAddEditDialogState()

    AddEditProgressionDialog(
        state = addEditProgressionDialogState
    ) { progression ->
        onEvent(ProgressionSelectionEvent.AddEditProgression(progression))
    }

    Scaffold(topBar = {
        Column {
            TopAppBar(title = { Text(text = exerciseCategory.formatName()) }, navigationIcon = {
                IconButton(onClick = onCloseProgressionScreen) {
                    Icon(
                        painter = painterResource(id = R.drawable.close_24px),
                        contentDescription = "Close progression screen"
                    )
                }
            })
            HorizontalDivider()
        }
    }, bottomBar = {
        AnimatedContent(
            targetState = uiState !is ProgressionSelectionUiState.Loading, label = "Bottom bar"
        ) { isNotLoading ->
            if (isNotLoading) {
                BottomAppBar(actions = {
                    IconButton(onClick = {
                        temporarilySelectedProgression?.let {
                            addEditProgressionDialogState.showEditMode(
                                it.name,
                                it.isMileStone,
                                it.id
                            )
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_24px),
                            contentDescription = "Edit selected progression"
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_24px),
                            contentDescription = "Delete selected progression"
                        )
                    }
                    IconButton(onClick = {
                        temporarilySelectedProgression?.let {
                            addEditProgressionDialogState.showAddMode(it.level - 1)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_row_above_24px),
                            contentDescription = "Add easier progression"
                        )
                    }
                    IconButton(onClick = {
                        temporarilySelectedProgression?.let {
                            addEditProgressionDialogState.showAddMode(it.level + 1)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_row_below_24px),
                            contentDescription = "Add harder progression"
                        )
                    }
                }, floatingActionButton = {
                    Button(
                        onClick = { onEvent(ProgressionSelectionEvent.SetProgressionLevelWithTemporarilySelectedProgressionLevel) },
                        modifier = Modifier.height(56.dp),
                        shape = FloatingActionButtonDefaults.shape,
                        enabled = temporarilySelectedProgression?.level != (uiState as ProgressionSelectionUiState.Success).level
                    ) {
                        Text(text = "Choose")
                    }
                })
            }
        }
    }) { paddingValues ->
        AnimatedContent(
            targetState = uiState is ProgressionSelectionUiState.Loading,
            label = "Loading",
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) { isLoading ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val progressions = (uiState as ProgressionSelectionUiState.Success).progressions
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(progressions) { index, progression ->
                        if (index != 0) {
                            VerticalDivider(
                                modifier = Modifier.height(16.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        ProgressionContainer(temporarySelected = temporarilySelectedProgression == progression,
                            selected = uiState.level == progression.level,
                            isMilestone = progression.isMileStone,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            onClick = {
                                onEvent(
                                    ProgressionSelectionEvent.SetTemporarilySelectedProgression(
                                        progression
                                    )
                                )
                            }) {
                            Text(
                                text = progression.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberAddEditDialogState(
    initialVisibleState: Boolean = false,
    initialMode: ProgressionDialogMode = ProgressionDialogMode.Add,
    initialProgression: AddEditProgression = AddEditProgression(
        name = "", isMilestone = false, level = -1
    )
): AddEditProgressionDialogState {
    return rememberSaveable(saver = AddEditProgressionDialogState.Saver()) {
        AddEditProgressionDialogState(
            initialVisibleState = initialVisibleState,
            initialMode = initialMode,
            initialProgression = initialProgression
        )
    }
}

enum class ProgressionDialogMode {
    Add, Edit
}

class AddEditProgressionDialogState(
    initialVisibleState: Boolean,
    initialMode: ProgressionDialogMode,
    initialProgression: AddEditProgression
) {
    var progression: AddEditProgression
        get() = _progression.value
        set(value) {
            _progression.value = value
        }

    var mode: ProgressionDialogMode
        get() = _mode.value
        set(value) {
            _mode.value = value
        }

    var visible: Boolean
        get() = _visible.value
        private set(value) {
            _visible.value = value
        }

    fun setIsMilestone(value: Boolean) {
        _progression.value = progression.copy(isMilestone = value)
    }

    private fun show() {
        _visible.value = true
    }

    fun showEditMode(name: String, isMilestone: Boolean, id: Int) {
        _progression.value = progression.copy(name = name, isMilestone = isMilestone, id = id)
        _mode.value = ProgressionDialogMode.Edit
        show()
    }

    fun showAddMode(level: Int) {
        _progression.value = progression.copy(name = "", isMilestone = false, level = level)
        _mode.value = ProgressionDialogMode.Add
        show()
    }

    fun hide() {
        _visible.value = false
    }

    companion object {
        fun Saver(): Saver<AddEditProgressionDialogState, *> = listSaver(save = {
            val progression = it.progression
            listOf(
                progression.name,
                progression.level,
                progression.isMilestone,
                it.visible,
                it.mode.ordinal
            )
        }, restore = {
            AddEditProgressionDialogState(
                initialProgression = AddEditProgression(
                    name = it[0] as String, level = it[1] as Int, isMilestone = it[2] as Boolean
                ),
                initialVisibleState = it[3] as Boolean,
                initialMode = ProgressionDialogMode.entries[it[4] as Int]
            )
        })
    }

    private val _progression = mutableStateOf(initialProgression)
    private val _mode = mutableStateOf(initialMode)
    private val _visible = mutableStateOf(initialVisibleState)
}

data class AddEditProgression(
    val name: String, val isMilestone: Boolean, val level: Int, val id: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditProgressionDialog(
    state: AddEditProgressionDialogState, onConfirm: (AddEditProgression) -> Unit
) {
    val focusRequester = remember {
        FocusRequester()
    }

    if (state.visible) {
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
        val onDismissRequest = {
            state.hide()
        }
        BasicAlertDialog(onDismissRequest = onDismissRequest) {
            val mode = state.mode
            val progression = state.progression
            val progressionName = progression.name
            val textFieldState = rememberTextFieldState(
                initialText = progressionName, initialSelection = when (mode) {
                    ProgressionDialogMode.Add -> TextRange(progressionName.length)
                    ProgressionDialogMode.Edit -> TextRange(0, progressionName.length)
                }
            )

            AlertDialogContent(title = "${mode.name} progression", content = {
                Column {
                    TextField(
                        state = textFieldState,
                        label = {
                            Text(text = "Name")
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ListItem(headlineContent = { Text(text = "Milestone") }, trailingContent = {
                        Switch(checked = progression.isMilestone, onCheckedChange = {
                            state.setIsMilestone(it)
                        })
                    }, modifier = Modifier.padding(horizontal = 8.dp))
                }
            }, onDismissRequest = onDismissRequest) {
                TextButton(onClick = {
                    onConfirm(state.progression.copy(name = textFieldState.text.toString()))
                    onDismissRequest()
                }) {
                    Text(text = mode.name)
                }
            }
        }
    }
}

@Composable
private fun AlertDialogContent(
    title: String,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit
) {
    Surface(
        shape = AlertDialogDefaults.shape,
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column {
            ProvideContentColorTextStyle(
                contentColor = AlertDialogDefaults.titleContentColor,
                textStyle = MaterialTheme.typography.headlineSmall
            ) {
                Box(
                    Modifier
                        .padding(bottom = 16.dp, top = 24.dp)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title
                    )
                }
            }
            ProvideContentColorTextStyle(
                contentColor = AlertDialogDefaults.textContentColor,
                textStyle = MaterialTheme.typography.bodyMedium
            ) {
                Box(
                    Modifier
                        .weight(weight = 1f, fill = false)
                        .align(Alignment.Start)
                ) {
                    content()
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                ProvideContentColorTextStyle(contentColor = MaterialTheme.colorScheme.primary,
                    textStyle = MaterialTheme.typography.labelLarge,
                    content = {
                        AlertDialogFlowRow(
                            mainAxisSpacing = 8.dp, crossAxisSpacing = 12.dp
                        ) {
                            TextButton(onClick = onDismissRequest) {
                                Text(text = "Cancel")
                            }
                            confirmButton()
                        }
                    })
            }
        }
    }
}

@Composable
private fun AlertDialogFlowRow(
    mainAxisSpacing: Dp, crossAxisSpacing: Dp, content: @Composable () -> Unit
) {
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentMainAxisSize + mainAxisSpacing.roundToPx() + placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            // Ensures that confirming actions appear above dismissive actions.
            @Suppress("ListIterator") sequences.add(0, currentSequence.toList())
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        measurables.fastForEach { measurable ->
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        layout(mainAxisLayoutSize, crossAxisLayoutSize) {
            sequences.fastForEachIndexed { i, placeables ->
                val childrenMainAxisSizes = IntArray(placeables.size) { j ->
                    placeables[j].width + if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                }
                val arrangement = Arrangement.End
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(arrangement) {
                    arrange(
                        mainAxisLayoutSize,
                        childrenMainAxisSizes,
                        layoutDirection,
                        mainAxisPositions
                    )
                }
                placeables.fastForEachIndexed { j, placeable ->
                    placeable.place(
                        x = mainAxisPositions[j], y = crossAxisPositions[i]
                    )
                }
            }
        }
    }
}

@Composable
fun ProvideContentColorTextStyle(
    contentColor: Color, textStyle: TextStyle, content: @Composable () -> Unit
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content
    )
}

private const val CONTAINER_HEIGHT = 72

@Composable
private fun ProgressionContainer(
    temporarySelected: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    isMilestone: Boolean,
    onClick: (() -> Unit)?,
    content: @Composable BoxScope.() -> Unit
) {
    @Composable
    fun CenteredBox(content: @Composable BoxScope.() -> Unit) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
            AnimatedVisibility(
                visible = selected,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.check_circle_20px),
                    contentDescription = "Is currently the selected progression",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    val modifier1 = modifier
        .height(CONTAINER_HEIGHT.dp)
        .clip(CardDefaults.shape)
        .clickable(interactionSource = remember {
            MutableInteractionSource()
        }, indication = null, onClick = {
            onClick?.let {
                onClick()
            }
        }, enabled = onClick != null)

    val borderTransition =
        updateTransition(targetState = temporarySelected, label = "Progression container border")
    val borderWidth by borderTransition.animateDp(label = "Border width") {
        if (it) 2.dp else CardDefaults.outlinedCardBorder().width
    }
    val borderColor by borderTransition.animateColor(label = "Border color") {
        if (it) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline
    }
    val border = BorderStroke(borderWidth, color = borderColor)
    OutlinedCard(
        modifier = modifier1, border = border
    ) {
        CenteredBox {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    enabled: Boolean = true,
    isError: Boolean = false,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: ((() -> Unit) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    placeholder: (@Composable () -> Unit)? = null,
    label: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    shape: Shape = TextFieldDefaults.shape,
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled, isError, interactionSource).value
    }

    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
        BasicTextField(state = state,
            modifier = modifier.defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth, minHeight = TextFieldDefaults.MinHeight
            ),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(colors.cursorColor(isError).value),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            interactionSource = interactionSource,
            lineLimits = lineLimits,
            decorator = @Composable { innerTextField ->
                // places leading icon, text field with label and placeholder, trailing icon
                TextFieldDefaults.DecorationBox(
                    value = state.text.toString(),
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    label = label,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    shape = shape,
                    singleLine = lineLimits is TextFieldLineLimits.SingleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors
                )
            })
    }
}

@Composable
fun TextFieldColors.textColor(
    enabled: Boolean, isError: Boolean, interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledTextColor
        isError -> errorTextColor
        focused -> focusedTextColor
        else -> unfocusedTextColor
    }
    return rememberUpdatedState(targetValue)
}

val TextFieldColors.selectionColors: TextSelectionColors
    @Composable get() = textSelectionColors

@Composable
internal fun TextFieldColors.cursorColor(isError: Boolean): State<Color> {
    return rememberUpdatedState(if (isError) errorCursorColor else cursorColor)
}