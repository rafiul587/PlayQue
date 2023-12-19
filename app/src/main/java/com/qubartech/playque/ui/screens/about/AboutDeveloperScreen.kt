package com.qubartech.playque.ui.screens.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qubartech.playque.R

@Composable
fun AboutDeveloperScreen(navController: NavController) {
    ProfileScreen()
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImage()
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    fontSize = 20.sp,
                    text = "Qubartech",
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Qubartech is a software agency provides any kind of mobile and well app solution for your business.",
                    modifier = Modifier.padding(3.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )
            }
            /*Text(
                text = "Java | Kotlin | Jetpack Compose",
                modifier = Modifier.padding(3.dp),
                style = MaterialTheme.typography.bodyMedium
            )*/
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            CompanyInfo()
            Spacer(modifier = Modifier.height(8.dp))
            Feedback()
        }
    }
}

@Composable
fun CompanyInfo() {

    ElevatedCard() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .wrapContentHeight(),
        ) {
            Text(
                text = "Find us on",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            SocialMediaLinkRow(
                mediaName = "Facebook",
                url = "https://facebook.com/qubartech",
                username = "qubartech",
                icon = R.drawable.facebook
            )
            SocialMediaLinkRow(
                mediaName = "LinkedIn",
                url = "https://www.linkedin.com/company/qubartech",
                username = "qubartech",
                icon = R.drawable.linkedin
            )

            SocialMediaLinkRow(
                mediaName = "Twitter",
                url = "https://twitter.com/qubartech",
                username = "qubartech",
                icon = R.drawable.twitter
            )
            SocialMediaLinkRow(
                mediaName = "Website",
                url = "https://qubartech.com",
                username = "https://qubartech.com",
                icon = R.drawable.web
            )
            SocialMediaLinkRow(
                mediaName = "Website",
                url = "https://gridplays.com",
                username = "https://gridplays.com",
                icon = R.drawable.web
            )
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
                .padding(20.dp),
        ) {
            Text(
                text = "Feedback",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "You can email us to give feedback, report bugs or make feature requests.",
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row(


                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Email, contentDescription = "Email icon")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Email:")
                Spacer(modifier = Modifier.width(10.dp))
                ClickableText(
                    modifier = Modifier.weight(1f),
                    style = TextStyle.Default.copy(
                        textAlign = TextAlign.End,
                        fontSize = 16.sp
                    ),

                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                            append("qubartech@gmail.com")
                        }

                    },
                    onClick = {
                        context.sendMail(
                            "qubartech@gmail.com",
                            "Feedback or Feature Request"
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
fun SocialMediaLinkRow(
    modifier: Modifier = Modifier,
    mediaName: String,
    username: String,
    url: String,
    @DrawableRes icon: Int,
) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val annotatedString = createAnnotatedString(url)
        Image(painterResource(id = icon), contentDescription = "$mediaName icon")
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$mediaName:",
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        ClickableText(
            modifier = Modifier.weight(1f),
            text = buildAnnotatedString { append(username) },
            style = TextStyle(
                textAlign = TextAlign.End,
                fontSize = 15.sp,
                color = Color(0xFF2988E6),
                fontFamily = FontFamily(Font(R.font.lato_bold))
            ),
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

@Composable
fun createAnnotatedString(
    text: String
): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(
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