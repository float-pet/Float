package com.floatpet.floatapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.floatpet.floatapp.ui.base.BaseExpandedFloat
import com.floatpet.floatapp.ui.base.BaseFloat
import com.floatpet.floatapp.ui.crypto.CryptoFloat
import com.floatpet.floatapp.ui.crypto.CryptoMiniFloat
import com.floatpet.floatapp.ui.crypto.CryptoExpandedFloat
import com.floatpet.floatapp.ui.crypto.CryptoDraggableSol
import io.github.luiisca.floating.views.CloseBehavior
import io.github.luiisca.floating.views.CloseFloatConfig
import io.github.luiisca.floating.views.ExpandedFloatConfig
import io.github.luiisca.floating.views.FloatingViewsConfig
import io.github.luiisca.floating.views.MainFloatConfig
import io.github.luiisca.floating.views.helpers.FloatServiceStateManager
import io.github.luiisca.floating.views.helpers.FloatingViewsManager

@Composable
fun App() {
  Scaffold { innerPadding ->
    println(innerPadding)

    val context = LocalContext.current
    val isServiceRunning by FloatServiceStateManager.isServiceRunning.collectAsState()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Base Float (Original)
      Button(
        modifier = Modifier.widthIn(min = 200.dp, max = 300.dp),
        onClick = {
          val config = FloatingViewsConfig(
            enableAnimations = false,
            main = MainFloatConfig(
              composable = { BaseFloat() },
            ),
            close = CloseFloatConfig(
              closeBehavior = CloseBehavior.CLOSE_SNAPS_TO_MAIN_FLOAT,
            ),
            expanded = ExpandedFloatConfig(
              composable = { close -> BaseExpandedFloat(close) },
            )
          )

          FloatingViewsManager.startFloatServiceIfPermitted(context, config)
        }
      ) {
        Text(text = "Base Float", style = MaterialTheme.typography.bodyLarge)
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Crypto Float with 3-state progression (New)
      Button(
        modifier = Modifier.widthIn(min = 200.dp, max = 300.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondary
        ),
        onClick = {
          val config = FloatingViewsConfig(
            enableAnimations = true,
            main = MainFloatConfig(
              composable = { CryptoFloat() },
            ),
            close = CloseFloatConfig(
              closeBehavior = CloseBehavior.CLOSE_SNAPS_TO_MAIN_FLOAT,
              composable = { CryptoMiniFloat() }
            ),
            expanded = ExpandedFloatConfig(
              composable = { close -> CryptoExpandedFloat(close) },
            )
          )

          FloatingViewsManager.startFloatServiceIfPermitted(context, config)
        }
      ) {
        Text(text = "Crypto 3-State", style = MaterialTheme.typography.bodyLarge)
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Draggable SOL Price Float
      Button(
        modifier = Modifier.widthIn(min = 200.dp, max = 300.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.tertiary
        ),
        onClick = {
          val config = FloatingViewsConfig(
            enableAnimations = true,
            main = MainFloatConfig(
              composable = { CryptoDraggableSol {} },
            ),
            close = CloseFloatConfig(
              closeBehavior = CloseBehavior.CLOSE_SNAPS_TO_MAIN_FLOAT,
            ),
            expanded = ExpandedFloatConfig(
              composable = { close -> CryptoExpandedFloat(close) },
            )
          )

          FloatingViewsManager.startFloatServiceIfPermitted(context, config)
        }
      ) {
        Text(text = "SOL Price (Draggable)", style = MaterialTheme.typography.bodyLarge)
      }

      // Display a button to stop the service if it's running
      if (isServiceRunning) {
        Spacer(modifier = Modifier.height(16.dp))
        Button(
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
          ),
          modifier = Modifier.widthIn(min = 200.dp, max = 300.dp),
          onClick = {
            FloatingViewsManager.stopFloatService(context)
          }
        ) {
          Text(text = "Remove all", style = MaterialTheme.typography.bodyLarge)
        }
      }
    }
  }
}