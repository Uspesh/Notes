package com.example.notes.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes.R
import com.example.notes.ui.theme.Orange
import com.example.notes.ui.navigation.NavigationRoutes
import com.example.notes.utils.sendNoteBroadcast
import com.example.notes.view_models.NotesViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderNotesScreen(parentFolder: String, navigator: NavHostController, notesViewModel: NotesViewModel) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        bottomBar = { NotesBottomBar(parentFolder = parentFolder, navigator = navigator, notesViewModel = notesViewModel) }
    ) {
        Column{
            NotesTopBar(navigator = navigator, notesViewModel = notesViewModel)
            Text(
                text = parentFolder,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            SearchBar()
            NotesList(navigator = navigator, modifier = Modifier.padding(top = 10.dp), notesViewModel = notesViewModel, parentFolder = parentFolder)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopBar(
    navigator: NavHostController,
    parentFolder: String = "Folders",
    notesViewModel: NotesViewModel
) {
    val state by notesViewModel.allNotesState.collectAsState()

    if (notesViewModel.openDefaultPending){
        DefaultPending(notesViewModel)
    } else if (notesViewModel.openAlliCloudPending){
        AlliCloudPending(notesViewModel)
    } else if (notesViewModel.openNotesAndSharedPending){
        NotesAndSharedPending(notesViewModel)
    }

    TopAppBar(
        title = {
            Text(
                text = parentFolder,
                style = MaterialTheme.typography.titleLarge,
                color = Orange,
                modifier = Modifier.clickable {
                    if(parentFolder != "Folders") {
                        val currentNote = state.notes.last()
                        if (currentNote.title.isEmpty() && currentNote.textBody.isEmpty()) {
                            notesViewModel.deleteNote(currentNote.id)
                        }
                        navigator.navigate(NavigationRoutes.FolderDetail.withArgs(parentFolder))
                    } else {
                        navigator.navigate(NavigationRoutes.MainScreen.route)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        actions = {
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                when(parentFolder){
                    "Recently Deleted" -> Text(
                        text = "Edit",
                        color = Orange,
                        modifier = Modifier.clickable {  }
                    )
                    "Notes" -> Image(
                        imageVector = Icons.Default.Pending,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Orange),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { notesViewModel.openNotesAndSharedPending = true }
                    )
                    "Shared" -> Image(
                        imageVector = Icons.Default.Pending,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Orange),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { notesViewModel.openNotesAndSharedPending = true }
                    )
                    "All iCloud" -> Image(
                        imageVector = Icons.Default.Pending,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Orange),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { notesViewModel.openAlliCloudPending = true }
                    )
                    else -> Row {
                        Icon(
                            imageVector = Icons.Default.IosShare, contentDescription = null,
                            tint = Orange,
                            modifier = Modifier.size(36.dp)
                        )
                        Image(
                            imageVector = Icons.Default.Pending,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Orange),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { notesViewModel.openDefaultPending = true }
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if(parentFolder != "Folders") {
                        val currentNote = state.notes.last()
                        if (currentNote.title.isEmpty() && currentNote.textBody.isEmpty()) {
                            notesViewModel.deleteNote(currentNote.id)
                        }
                        navigator.navigate(NavigationRoutes.FolderDetail.withArgs(parentFolder))
                    } else {
                        navigator.navigate(NavigationRoutes.MainScreen.route)
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Orange)
            }
        }
    )
}

@Composable
fun AlliCloudPending(notesViewModel: NotesViewModel) {
    DropdownMenu(
        expanded = notesViewModel.openAlliCloudPending,
        onDismissRequest = { notesViewModel.openAlliCloudPending = false },
        modifier = Modifier.clip(MaterialTheme.shapes.small)
    ) {
        DropdownMenuItem(text = { Text(text = "View as Gallery") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
    }
}

@Composable
fun NotesAndSharedPending(notesViewModel: NotesViewModel) {
    DropdownMenu(
        expanded = notesViewModel.openNotesAndSharedPending,
        onDismissRequest = { notesViewModel.openNotesAndSharedPending = false },
        modifier = Modifier.clip(MaterialTheme.shapes.small)
    ) {
        DropdownMenuItem(text = { Text(text ="View as Gallery") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Sort By") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
    }
}

@Composable
fun DefaultPending(notesViewModel: NotesViewModel) {
    DropdownMenu(
        expanded = notesViewModel.openDefaultPending,
        onDismissRequest = { notesViewModel.openDefaultPending = false },
        modifier = Modifier.clip(MaterialTheme.shapes.small)
    ) {
        DropdownMenuItem(text = { Text(text = "View as Gallery") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Share Folder") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Add Folder") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Move This Folder") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Rename") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Select Notes") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Sort By") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Group By Date") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "View Attachments") }, onClick = { /*TODO*/ })
        Divider()
        DropdownMenuItem(text = { Text(text = "Convert to Smart Folder") }, onClick = { /*TODO*/ })
    }
}

@Composable
fun NotesBottomBar(modifier: Modifier = Modifier, parentFolder: String, navigator: NavHostController, notesViewModel: NotesViewModel) {//title: String?
    val state by notesViewModel.allNotesState.collectAsState()
    val countNotes = state.notes.size

    if(notesViewModel.openCreateNoteDialog){
//        notesViewModel.opensDialog()
        AddNoteDialog(notesViewModel = notesViewModel, parentFolder = parentFolder)
    }

    BottomAppBar(
        modifier = modifier
            .height(46.dp)
            .fillMaxWidth(),
        containerColor = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "$countNotes Notes",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                color = Color.White
            )
            Icon(
                painter = painterResource(id = R.drawable.edit_square_fill0_wght400_grad0_opsz24),
                contentDescription = null,
                tint = Orange,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        notesViewModel.openCreateNoteDialog = true
                        //navigator.navigate(NavigationRoutes.NewNote.route)
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(notesViewModel : NotesViewModel, parentFolder: String) {
    val state by notesViewModel.allNotesState.collectAsState()
    AlertDialog(
        onDismissRequest = {notesViewModel.openCreateNoteDialog = false},
        confirmButton = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ){
                            Button(onClick = {notesViewModel.createNote(parentFolder = parentFolder)}) {
                                Text(text = "Create Note")
                            }
                        }
        },
        title = { Text(text = "Add note")},
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.title,
                    onValueChange = {title ->
                        notesViewModel.updateNoteTitle(title = title)
                    },
                    placeholder = { Text(text = "Note title")}
                )
            }
        }
    )
}


@Composable
fun NotesList(parentFolder: String, navigator: NavHostController, modifier: Modifier = Modifier, notesViewModel: NotesViewModel) {
    val state by notesViewModel.allNotesState.collectAsState()

    val notesList = when(parentFolder){
        "Recently Deleted" -> state.deletedNotes
        "All iCloud" -> state.allNotes
        else -> state.notes
    }

    Column(
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
    ) {
        LazyColumn{
            itemsIndexed(notesList){_, item ->
                Note(title = item.title, date = item.date, firstLine = item.firstLine, index = item.id, navigator = navigator, notesViewModel = notesViewModel)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(title: String, date: String, firstLine: String, index: Int, navigator: NavHostController, notesViewModel: NotesViewModel) {
    var showMenu by remember {
        mutableStateOf(false)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { navigator.navigate(NavigationRoutes.NoteDetail.withArgs(index)) },
                onLongClick = { showMenu = true }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray,
            contentColor = Color.White
        ),
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Row {
                Text(
                    text = date,
                    color = Color.Gray
                )
                Text(
                    text = if (firstLine.length > 30) {
                        "${firstLine.take(30)}..."
                    } else {
                        firstLine
                    },
                    modifier = Modifier.padding(start = 5.dp),
                    color = Color.Gray
                )
            }
        }
    }
    val context = LocalContext.current
    val currentNote = notesViewModel.getNote(title)

    DropdownMenu(
        expanded = showMenu,
        modifier = Modifier.clip(MaterialTheme.shapes.small),
        onDismissRequest = { showMenu = !showMenu }) {
        DropdownMenuItem(
            text = { Text(text = "Pin Note") },
            onClick = { /*TODO*/ },
            trailingIcon = { Icon(imageVector = Icons.Default.PushPin, contentDescription = null) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(text = "Lock Note") },
            onClick = { /*TODO*/ },
            trailingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(text = "Share Note") },
            onClick = { /*TODO*/ },
            trailingIcon = { Icon(imageVector = Icons.Default.IosShare, contentDescription = null, modifier = Modifier.clickable {
                sendNoteBroadcast(context = context, title = currentNote.title, textBody = currentNote.textBody)
            }) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(text = "Move") },
            onClick = { /*TODO*/ },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null
                )
            }
        )
        Divider()
        DropdownMenuItem(
            text = {
                Text("Delete", color = Color.Red)
            },
            onClick = {
                notesViewModel.deleteNote(index)
                showMenu = false
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun NotePreview() {
//    NotesTheme {
//        Note(title = "Test", date = "10/01/01", firstLine = "How to implement something new for car bird something special at all", index = 0, navigator = rememberNavController())
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun FolderNotesScreenPreview() {
//    NotesTheme {
//        FolderNotesScreen(title = "Test", navigator = rememberNavController())
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun BotBarPreview() {
//    NotesTheme {
//        NotesBottomBar(navigator = rememberNavController())
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun TopBarPreview() {
//    NotesTheme {
//        NotesTopBar(navigator = rememberNavController())
//    }
//}