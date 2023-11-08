/*
 * Copyright 2023 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.parkme.navigation

import android.content.Context
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.bumptech.glide.Glide
import com.example.parkme.viewmodels.CheckoutViewModel
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.parkme.MainActivity
import com.example.parkme.R
import com.example.parkme.utils.PaymentsUtil
import com.google.pay.button.PayButton
import com.google.wallet.button.WalletButton


@Composable
fun ProductScreen(
    title:String,
    description:String,
    price:String,
    image:String,
    viewModel: CheckoutViewModel,
    googlePayButtonOnClick: () -> Unit,
    googleWalletButtonOnClick: () -> Unit,
    contexto : Context
) {

    val activity = LocalContext.current as Activity
    val state by viewModel.state.collectAsState()
    val padding = 20.dp
    val black = Color(0xff000000.toInt())
    val grey = Color(0xffeeeeee.toInt())

    if (state.checkoutSuccess) {
        Column(
            modifier = Modifier
                .testTag("successScreen")
                .background(grey)
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                contentDescription = null,
                painter =  rememberImagePainter(
                    data = "https://i.pinimg.com/originals/16/1a/cf/161acfbe0420d1676dabf4599caebd32.jpg",
                    builder = {
                        crossfade(true)
                    }
                ),
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
            )
            Text(text = "Pago completado ! Ya puede retirarse del garaje")
            Button(onClick = {
                activity.finish()
            })
            {
                    Text("Volver")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .background(grey)
                .padding(padding)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(space = padding / 2),
        ) {
            Image(
                contentDescription = null,
                painter = rememberImagePainter(
                        data = "https://i.pinimg.com/originals/16/1a/cf/161acfbe0420d1676dabf4599caebd32.jpg",
                builder = {
                    crossfade(true)
                }
            ) ,
                    modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )
            }
            Text(
                text = title,
                color = black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = price, color = black)
            Spacer(Modifier)
            Text(
                text = "Description",
                color = black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = black
            )
            if (state.googlePayAvailable == true) {
                PayButton(
                    modifier = Modifier
                        .testTag("payButton")
                        .fillMaxWidth(),
                    onClick = { if (state.googlePayButtonClickable) googlePayButtonOnClick() },
                    allowedPaymentMethods = PaymentsUtil.allowedPaymentMethods.toString()
                )
            }
            if (state.googleWalletAvailable == false) {
                Spacer(Modifier)
                Text(
                    text = "Or add a pass to your Google Wallet:",
                    color = black
                )
                WalletButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { if (state.googleWalletButtonClickable) googleWalletButtonOnClick() },
                )
            }
        }
    }

