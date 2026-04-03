package edu.nd.pmcburne.hello

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.weight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import edu.nd.pmcburne.hello.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TagDropdown(
            selectedTag = uiState.selectedTag,
            allTags = uiState.allTags,
            onTagSelected = { viewModel.selectTag(it) }
        )

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
        // debug text:
//        Text("Selected tag: ${uiState.selectedTag}")
//        Text("Tags loaded: ${uiState.allTags.size}")
//        Text("Filtered locations: ${uiState.filteredLocations.size}")

        CampusMap(
            locations = uiState.filteredLocations,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        uiState.errorMessage?.let {
            Text(text = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDropdown(
    selectedTag: String,
    allTags: List<String>,
    onTagSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedTag,
                modifier = Modifier.weight(1f)
            )
            Text("▼")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allTags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag) },
                    onClick = {
                        onTagSelected(tag)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CampusMap(
    locations: List<LocationEntity>,
    modifier: Modifier = Modifier
) {
    val defaultCenter = LatLng(38.0356, -78.5034)

    val firstLocation = locations.firstOrNull()
    val mapCenter = if (firstLocation != null) {
        LatLng(firstLocation.latitude, firstLocation.longitude)
    } else {
        defaultCenter
    }

    val cameraPositionState = rememberCameraPositionState()

    androidx.compose.runtime.LaunchedEffect(mapCenter) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(mapCenter, 15f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        locations.forEach { location ->
            val cleanDescription = location.description
                .replace("&apos;", "'")
                .replace("&quot;", "\"")

            val shortDescription =
                if (cleanDescription.length > 80)
                    cleanDescription.take(80) + "..."
                else
                    cleanDescription

            Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(location.latitude, location.longitude)
                ),
                title = location.name,
                snippet = shortDescription
            )
        }
    }
}