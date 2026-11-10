package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.EduBooksApp
import com.example.ui.MainViewModel
import com.example.ui.theme.EduBooksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduBooksTheme {
                val viewModel: MainViewModel = viewModel()
                EduBooksApp(viewModel = viewModel)
            }
        }
    }
}
