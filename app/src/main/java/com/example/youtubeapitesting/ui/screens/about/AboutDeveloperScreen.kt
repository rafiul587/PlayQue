package com.example.youtubeapitesting.ui.screens.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.youtubeapitesting.R

@Composable
fun AboutDeveloperScreen(navController: NavController) {
    CompositionLocalProvider(LocalTextStyle provides TextStyle.Default.copy(color = MaterialTheme.colorScheme.onTertiary)) {
        ProfileScreen()
    }
}

@Composable
fun ProfileScreen() {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImage()
                Column(
                    modifier = Modifier
                        .padding(5.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.labelMedium,
                        text = "QubarTech"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "QubarTech is a software agency provides any kind of mobile & web app solution for your business.",
                        modifier = Modifier.padding(3.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )

                }
            }

            /*Text(
                text = "Java | Kotlin | Jetpack Compose",
                modifier = Modifier.padding(3.dp),
                style = MaterialTheme.typography.bodyMedium
            )*/
        }

        Spacer(modifier = Modifier.height(5.dp))

        CompanyInfo()
        Spacer(modifier = Modifier.height(5.dp))
        Feedback()
    }
}

@Composable
fun CompanyInfo() {

    val uriHandler = LocalUriHandler.current
    ElevatedCard() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 10.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Website and Social Link",
                fontSize = 20.sp,
                textDecoration = TextDecoration.Underline,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val annotatedString = createAnnotatedString("https://qubartech.com")
                Text(text = "Website:")
                Spacer(modifier = Modifier.width(10.dp))
                ClickableText(
                    modifier = Modifier.weight(1f),
                    text = annotatedString,
                    style = TextStyle(textAlign = TextAlign.End),
                    onClick = {
                        annotatedString
                            .getStringAnnotations(
                                "URL",
                                it, it
                            )
                            .firstOrNull()?.let { stringAnnotation ->
                                uriHandler.openUri(stringAnnotation.item)
                            }
                    }
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val annotatedString = createAnnotatedString("https://facebook.com/qubartech")
                Text(text = "Facebook:")
                Spacer(modifier = Modifier.width(10.dp))
                ClickableText(
                    modifier = Modifier.weight(1f),
                    text = annotatedString,
                    style = TextStyle(textAlign = TextAlign.End),
                    onClick = {
                        annotatedString
                            .getStringAnnotations(
                                "URL",
                                it, it
                            )
                            .firstOrNull()?.let { stringAnnotation ->
                                uriHandler.openUri(stringAnnotation.item)
                            }
                    }
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val annotatedString =
                    createAnnotatedString("https://www.linkedin.com/company/qubartech")
                Text(text = "LinkedIn:")
                Spacer(modifier = Modifier.width(10.dp))
                ClickableText(
                    modifier = Modifier.weight(1f),
                    text = annotatedString,
                    style = TextStyle(textAlign = TextAlign.End),
                    onClick = {
                        annotatedString
                            .getStringAnnotations(
                                "URL",
                                it, it
                            )
                            .firstOrNull()?.let { stringAnnotation ->
                                uriHandler.openUri(stringAnnotation.item)
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun Feedback() {
    val context = LocalContext.current
    ElevatedCard() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Feedback",
                textDecoration = TextDecoration.Underline,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "You can email us to give feedback of any bugs or make feature requests.",
                color = MaterialTheme.colorScheme.onTertiary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Email:")
                Spacer(modifier = Modifier.width(10.dp))
                ClickableText(
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onTertiary,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 16.sp
                    ),

                    text = buildAnnotatedString {
                        append("qubartech@gmail.com")
                    },
                    onClick = {
                        context.sendMail(
                            "qubartech@gmail.com",
                            "StudyTube | Feedback or Feature Request"
                        )
                    })
            }
        }
    }
}

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = CircleShape,
        modifier = Modifier
            .border(2.dp, Color.Gray, CircleShape)
            .size(80.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_qubartech),
            contentDescription = "profile image",
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun createAnnotatedString(
    text: String
): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            ),
            0,
            text.length
        )
        addStringAnnotation(
            tag = "URL",
            annotation = text,
            start = 0,
            end = text.length
        )
    }
}

fun Context.sendMail(to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // or "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {

    } catch (t: Throwable) {
    }
}

@Preview
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}