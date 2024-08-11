package com.mathias8dev.composesimplestoragemanager

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.mathias8dev.composesimplestoragemanager.ui.screens.NavGraphs
import com.mathias8dev.composesimplestoragemanager.ui.theme.ComposeSimpleStorageManagerTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
        setupCoilImageLoader()
        setContent {
            val navController = rememberNavController()
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            navController.navigatorProvider += bottomSheetNavigator
            val navHostEngine = rememberAnimatedNavHostEngine(
                rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
            )

            val snackbarHostState = remember { SnackbarHostState() }


            ComposeSimpleStorageManagerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) { innerPadding ->

                    val layoutDirection = LocalLayoutDirection.current


                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHostState,
                    ) {
                        ModalBottomSheetLayout(
                            modifier = Modifier.padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding(),
                                start = innerPadding.calculateStartPadding(layoutDirection),
                                end = innerPadding.calculateEndPadding(layoutDirection),
                            ),
                            bottomSheetNavigator = bottomSheetNavigator,
                            sheetShape = RoundedCornerShape(16.dp),
                        ) {
                            DestinationsNavHost(
                                navController = navController,
                                navGraph = NavGraphs.root,
                                engine = navHostEngine
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
    }

    private fun setupCoilImageLoader() {
        fun getUnsafeClient(): OkHttpClient {
            val trustAllCerts: Array<TrustManager> = arrayOf(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            okHttpClientBuilder.hostnameVerifier { _, _ -> true }
            return okHttpClientBuilder.build()
        }

        val imageLoader = ImageLoader
            .Builder(this)
            .okHttpClient { getUnsafeClient() }
            .components {
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
                if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)

    }
}
