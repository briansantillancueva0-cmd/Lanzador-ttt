package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainScaffold
import com.example.ui.theme.TagTeamTheme
import com.example.ui.viewmodel.TagTeamViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TagTeamTheme {
                val viewModel: TagTeamViewModel = viewModel()
                MainScaffold(viewModel = viewModel)
            }
        }
    }
}
