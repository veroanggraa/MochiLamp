package com.veroanggra.mochilamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.veroanggra.mochilamp.ui.lamp.KawaiiLampScreen
import com.veroanggra.mochilamp.ui.theme.KawaiiLampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Modern Android Edge-to-Edge
        enableEdgeToEdge()
        setContent {
            KawaiiLampTheme {
                KawaiiLampScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
