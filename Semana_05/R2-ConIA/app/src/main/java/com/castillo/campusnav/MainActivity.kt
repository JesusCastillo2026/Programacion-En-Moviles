package com.castillo.campusnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.castillo.campusnav.data.CampusStore
import com.castillo.campusnav.navigation.AppNavigation
import com.castillo.campusnav.ui.CampusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val store = remember { CampusStore(applicationContext) }
            CampusTheme { AppNavigation(store) }
        }
    }
}

