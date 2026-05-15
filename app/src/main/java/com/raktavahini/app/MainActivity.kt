package com.raktavahini.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.raktavahini.app.ui.RaktaVahiniApp
import com.raktavahini.app.ui.theme.RaktaVahiniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaktaVahiniTheme {
                RaktaVahiniApp()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun MainActivityPreview() {
    RaktaVahiniTheme {
        RaktaVahiniApp()
    }
}
