package com.baka3k.test.movie.detail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.baka3k.architecture.core.ui.component.AsyncImageView
import com.baka3k.architecture.core.ui.theme.AppTheme
import com.baka3k.core.data.movie.model.PhotoSize
import com.baka3k.core.data.movie.model.asPhotoUrl
import com.baka3k.core.model.Genre
import com.baka3k.core.model.Movie
import com.google.accompanist.flowlayout.FlowRow
import kotlin.random.Random

@Composable
fun topGradient() {
    val topColor = AppTheme.colors.colorBackgroundTheme.copy(alpha = 0.8f)
    val colorGray = Color(220, 220, 220, 0)
    val gradientGrayWhite = Brush.verticalGradient(0f to topColor, 10f to colorGray)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                gradientGrayWhite
            )
    )
}

@Composable
fun photoGradient(modifier: Modifier = Modifier) {
    val topColor = Color.Transparent
    val colorGray = AppTheme.colors.colorBackgroundTheme
    val gradientGrayWhite = Brush.verticalGradient(0f to topColor, 40f to colorGray)
    Box(
        modifier = modifier.background(
            gradientGrayWhite
        )
    )
}

@Composable
fun MovieInfoScreen(modifier: Modifier = Modifier, movie: Movie) {
    val color = AppTheme.colors.colorBackgroundTheme
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    ConstraintLayout {
        val (photo, info, content) = createRefs()
        AsyncImageView(
            data = movie.posterPath.asPhotoUrl(PhotoSize.Poster.w780),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .height(screenWidth)
                .drawWithCache {
                    val gradient = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, color),
                        startY = size.height / 3 * 2,
                        endY = size.height
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(gradient, blendMode = BlendMode.Multiply)
                    }
                }
                .constrainAs(photo) {
                    top.linkTo(parent.top)
                },
        )
        infoLayout(movie = movie, modifier = modifier.constrainAs(info) {
            bottom.linkTo(photo.bottom, margin = 20.dp)
        })
        Column(modifier = modifier
            .padding(start = 7.dp, end = 7.dp, top = 7.dp)
            .constrainAs(content) {
                top.linkTo(photo.bottom)
            }) {
            Text(
                text = "Plot",
                style = LocalTextStyle.current.copy(Color(106, 82, 156))
            )
            Text(
                modifier = modifier.padding(start = 11.dp, end = 11.dp, top = 7.dp),
                text = movie.overview,
                style = MaterialTheme.typography.bodySmall.copy(AppTheme.colors.colorContentEditText)
            )

        }
    }
}

@Composable
private fun infoLayout(modifier: Modifier = Modifier, movie: Movie) {
    Column(
        modifier = modifier
            .padding(top = 170.dp, start = 15.dp, end = 15.dp)
            .background(color = Color(255, 255, 255, 50), shape = MaterialTheme.shapes.large)
            .height(160.dp)
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium.copy(AppTheme.colors.colorContentEditText),
            modifier = modifier.padding(top = 19.dp, start = 9.dp, end = 9.dp),
            maxLines = 1
        )
        val modifier = Modifier.padding(start = 9.dp, top = 7.dp, end = 9.dp, bottom = 7.dp)
        FlowRow(
            modifier = modifier
                .height(IntrinsicSize.Max)
                .verticalScroll(rememberScrollState())
        ) {
            Row {
                roundButton(title = "${movie.voteCount} Vote",
                    backgroundColor = randomColor(),
                    modifier = modifier,
                    onClicked = {})
                roundButton(title = "${movie.voteAverage} *",
                    backgroundColor = randomColor(),
                    modifier = modifier,
                    onClicked = {})
            }
            genreUi(genres = movie.genres, modifier = modifier)
        }
    }
}

@Composable
fun genreUi(genres: List<Genre>, modifier: Modifier = Modifier) {
    genres.forEach { genre: Genre ->
        roundButton(title = genre.name,
            backgroundColor = randomColor(),
            modifier = modifier,
            onClicked = {})
    }
}

fun randomColor(): Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}

@Composable
private fun roundButton(
    title: String, backgroundColor: Color, modifier: Modifier = Modifier, onClicked: () -> Unit
) {
    OutlinedButton(
        onClick = onClicked, shape = CircleShape, colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
        ), border = BorderStroke(0.dp, Color.Transparent), modifier = modifier.height(40.dp)
    ) {
        Text(
            text = title, style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
        )
    }
}
