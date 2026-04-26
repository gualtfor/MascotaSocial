package com.example.mascotasocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.mascotasocial.ui.theme.MascotaSocialTheme
import android.net.Uri
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.util.UnstableApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MascotaSocialTheme {
                MainScreen()
            }
        }
    }
}

data class Photo(
    val id: String,
    val url: String,
    val alt: String,
    val likes: String,
    val category: String,
    val isTop: Boolean = false
)

val PHOTOS = listOf(
    Photo("1", "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=800", "Golden retriever", "1.2k", "Perros"),
    Photo("2", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=800", "Ginger cat", "850", "Gatos"),
    Photo("3", "https://images.unsplash.com/photo-1452570053594-1b985d6ea890?auto=format&fit=crop&q=80&w=400", "Parrot", "420", "Aves"),
    Photo("4", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&q=80&w=400", "Fluffy dog", "630", "Perros"),
    Photo("5", "https://images.unsplash.com/photo-1533738363-b7f9aef128ce?auto=format&fit=crop&q=80&w=600", "Husky", "2.1k", "Perros", true),
    Photo("6", "https://images.unsplash.com/photo-1513245543132-31f507417b26?auto=format&fit=crop&q=80&w=800", "Cat sunglasses", "980", "Gatos"),
    Photo("7", "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&q=80&w=400", "Rabbit", "310", "Exóticos")
)

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Photos : Screen("photos", "Fotos", Icons.Default.Photo)
    object Videos : Screen("videos", "Videos", Icons.Default.PlayCircle)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object AddVideo : Screen("add_video", "Video", Icons.Default.PlayCircle)
    object AddPhoto : Screen("add_photo", "Foto", Icons.Default.AddAPhoto)
    object EditProfile : Screen("edit_profile", "Editar Perfil", Icons.Default.Edit)
}

data class UserProfile(
    val ownerName: String,
    val petName: String,
    val breed: String,
    val location: String,
    val email: String,
    val phone: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Estado global simple para el perfil
    var userProfile by remember {
        mutableStateOf(
            UserProfile(
                ownerName = "Christian Gualteros",
                petName = "Toby",
                breed = "Golden Retriever",
                location = "Bogotá, Colombia",
                email = "christiangualteros36@gmail.com",
                phone = "+57 313 377 6590"
            )
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(modifier = Modifier.fillMaxSize()) {
        // Menú Lateral (Navigation Rail)
        NavigationRail(
            containerColor = Color.White,
            header = {
                Icon(
                    Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color(0xFF964300),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(vertical = 12.dp)
                )
            }
        ) {
            val screens = listOf(Screen.Home, Screen.Photos, Screen.Videos, Screen.Profile)
            screens.forEach { screen ->
                NavigationRailItem(
                    selected = currentRoute == screen.route,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { Icon(screen.icon, null) },
                    label = { Text(screen.label) }
                )
            }
        }

        // Contenido Principal
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "MascotaSocial",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF964300),
                            fontSize = 22.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                        }
                    }
                )
            },
            floatingActionButton = {
                var showAddMenu by remember { mutableStateOf(false) }

                Column(horizontalAlignment = Alignment.End) {
                    if (showAddMenu) {
                        SmallFloatingActionButton(
                            onClick = {
                                showAddMenu = false
                                navController.navigate(Screen.AddVideo.route)
                            },
                            containerColor = Color(0xFFF9873E),
                            contentColor = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Default.VideoCall, null)
                        }
                        SmallFloatingActionButton(
                            onClick = {
                                showAddMenu = false
                                navController.navigate(Screen.AddPhoto.route)
                            },
                            containerColor = Color(0xFFF9873E),
                            contentColor = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Default.AddAPhoto, null)
                        }
                    }

                    FloatingActionButton(
                        onClick = { showAddMenu = !showAddMenu },
                        containerColor = Color(0xFF964300),
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        val icon = if (showAddMenu) Icons.Default.Close else Icons.Default.Add
                        Icon(icon, null)
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Photos.route) { PhotosScreen() }
                composable(Screen.Videos.route) { VideosScreen() }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        profile = userProfile,
                        onEditProfile = { navController.navigate(Screen.EditProfile.route) }
                    )
                }
                composable(Screen.AddVideo.route) { AddVideoScreen(onBack = { navController.popBackStack() }) }
                composable(Screen.AddPhoto.route) { AddPhotoScreen(onBack = { navController.popBackStack() }) }
                composable(Screen.EditProfile.route) {
                    EditProfileScreen(
                        profile = userProfile,
                        onBack = { navController.popBackStack() },
                        onSave = { updatedProfile ->
                            userProfile = updatedProfile
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profile: UserProfile,
    onBack: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var ownerName by remember { mutableStateOf(profile.ownerName) }
    var petName by remember { mutableStateOf(profile.petName) }
    var breed by remember { mutableStateOf(profile.breed) }
    var location by remember { mutableStateOf(profile.location) }
    var email by remember { mutableStateOf(profile.email) }
    var phone by remember { mutableStateOf(profile.phone) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, null)
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        onSave(
                            UserProfile(
                                ownerName = ownerName,
                                petName = petName,
                                breed = breed,
                                location = location,
                                email = email,
                                phone = phone
                            )
                        )
                    }
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = Color(0xFF964300))
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Dueño y Mascota
            Text("Información Básica", fontWeight = FontWeight.Bold, color = Color.Gray)

            OutlinedTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text("Nombre del Dueño") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text("Nombre de la Mascota") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Raza") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text("Contacto y Ubicación", fontWeight = FontWeight.Bold, color = Color.Gray)

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Color(0xFF964300)) }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF964300)) }
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFF964300)) }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        UserProfile(
                            ownerName = ownerName,
                            petName = petName,
                            breed = breed,
                            location = location,
                            email = email,
                            phone = phone
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF964300)),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Actualizar Perfil", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var urlText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F7)),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Explorar la Web",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF964300)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = urlText,
                            onValueChange = { urlText = it },
                            placeholder = { Text(
                                text = "https://example.com",
                                fontSize = 9.sp
                            ) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (urlText.isNotBlank()) {
                                    val formattedUrl = if (!urlText.startsWith("http://") && !urlText.startsWith("https://")) {
                                        "https://$urlText"
                                    } else {
                                        urlText
                                    }
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Handle error if no browser found or invalid URL
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF964300))
                        ) {
                            Text("Abrir")
                        }
                    }
                }
            }
        }

        item {
            Column(Modifier.padding(top = 16.dp)) {
                Text(
                    "Novedades",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2C2F30),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Stories Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        StoryItem(isUser = true)
                    }
                    items(PHOTOS) { photo ->
                        StoryItem(photo = photo)
                    }
                }
            }
        }

        items(PHOTOS.shuffled()) { photo ->
            Box(Modifier.padding(horizontal = 16.dp)) {
                PostCard(photo)
            }
        }
    }
}

@Composable
fun StoryItem(photo: Photo? = null, isUser: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .then(
                    if (isUser) Modifier.background(Color.Transparent)
                    else Modifier.background(Brush.linearGradient(listOf(Color(0xFFF9873E), Color(0xFF964300))))
                )
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
            ) {
                if (isUser) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFE6E8EA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color(0xFF964300))
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(photo?.url),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            if (isUser) "Tu historia" else photo?.category ?: "Amigo",
            fontSize = 11.sp,
            maxLines = 1,
            color = Color(0xFF595C5D)
        )
    }
}

@Composable
fun PostCard(photo: Photo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9873E))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Usuario_Pet", fontWeight = FontWeight.Bold)
                    Text("Hace 2 horas", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Image(
                painter = rememberAsyncImagePainter(photo.url),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FavoriteBorder, null, tint = Color.Gray)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Outlined.ChatBubble, null, tint = Color.Gray)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Default.Share, null, tint = Color.Gray)
            }
            Text(
                "¡Mirad qué guapo está hoy! #mascotas #amor",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen() {
    var activeCategory by remember { mutableStateOf("Todos") }
    val categories = listOf("Todos", "Perros", "Gatos", "Aves", "Exóticos")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F7))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Fotos",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2C2F30)
            )
            Text(
                "Capturando los mejores momentos de tus amigos peludos.",
                color = Color(0xFF595C5D),
                fontWeight = FontWeight.Medium
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = activeCategory == category,
                    onClick = { activeCategory = category },
                    label = { Text(category, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF964300),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFB7D3FF),
                        labelColor = Color(0xFF004884)
                    ),
                    shape = RoundedCornerShape(50)
                )
            }
        }

        val filteredPhotos = if (activeCategory == "Todos") PHOTOS else PHOTOS.filter { it.category == activeCategory }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            items(filteredPhotos) { photo ->
                PhotoItem(photo)
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUrl: String, isVisible: Boolean, placeholderUrl: String) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    isLoading = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    hasError = true
                    isLoading = false
                }
            })
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }

    // Escuchar cambios en el estado de reproducción
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Placeholder image while loading
        if (isLoading || hasError) {
            Image(
                painter = rememberAsyncImagePainter(placeholderUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (!hasError) {
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true // Habilitar controles estándar
                        controllerAutoShow = true
                        controllerHideOnTouch = true
                        resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Indicador de pausa (icono grande al centro)
        if (!isPlaying && !isLoading && !hasError) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )
        }

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        }

        if (hasError) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Error, null, tint = Color.White, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text("Error al cargar video", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideosScreen() {
    val pagerState = rememberPagerState(pageCount = { PHOTOS.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            VideoPage(PHOTOS[page], isVisible = pagerState.currentPage == page, pageIndex = page)
        }

        // Top Header Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Videos", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {}) {
                Icon(Icons.Default.Search, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun VideoPage(photo: Photo, isVisible: Boolean, pageIndex: Int) {
    // Lista de videos públicos muy estables de Google
    val publicVideos = listOf(
        "https://www.w3schools.com/html/mov_bbb.mp4",
        "https://vjs.zencdn.net/v/oceans.mp4",
        "https://www.w3schools.com/html/movie.mp4",
        "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4"
    )

    val sampleVideoUrl = publicVideos[pageIndex % publicVideos.size]

    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayer(
            videoUrl = sampleVideoUrl,
            isVisible = isVisible,
            placeholderUrl = photo.url
        )

        // Gradient for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 1000f
                    )
                )
        )

        // Right Side Actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile with Plus
            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFF9873E)))
                }
                Box(
                    modifier = Modifier
                        .offset(y = 8.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            VideoAction(Icons.Default.Favorite, photo.likes)
            VideoAction(Icons.Default.ChatBubble, (photo.id.toInt() * 12).toString())
            VideoAction(Icons.Default.Bookmark, (photo.id.toInt() * 5).toString())
            VideoAction(Icons.Default.Share, "Share")

            // Rotating Music Disc (Static for now)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, null, tint = Color.White)
            }
        }

        // Bottom Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 20.dp, end = 80.dp)
        ) {
            Text("@Usuario_Pet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "¡Mirad qué salto! Mi mascota es un atleta profesional 🐾 #mascotas #divertido #video",
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 2
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Audio original - MascotaSocial", color = Color.White, fontSize = 13.sp)
            }
        }

        // Progress Bar
        LinearProgressIndicator(
            progress = 0.4f,
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun VideoAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(32.dp))
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhotoScreen(onBack: () -> Unit) {
    var caption by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Nueva Foto", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, null)
                }
            },
            actions = {
                TextButton(onClick = { onBack() }) {
                    Text("Publicar", fontWeight = FontWeight.Bold, color = Color(0xFF964300))
                }
            }
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Image Preview (con clic para seleccionar)
            Box(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Text("Seleccionar", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            TextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("¿Qué mascota es esta? Escribe una descripción...") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Divider(color = Color(0xFFF5F6F7))

        ListItem(
            headlineContent = { Text("Etiquetar personas") },
            leadingContent = { Icon(Icons.Default.Person, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            modifier = Modifier.clickable { }
        )
        ListItem(
            headlineContent = { Text("Ubicación") },
            leadingContent = { Icon(Icons.Default.LocationOn, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "Tu foto se compartirá con tus seguidores y podrá aparecer en la sección de Fotos.",
            modifier = Modifier.padding(16.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVideoScreen(onBack: () -> Unit) {
    var caption by remember { mutableStateOf("") }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedVideoUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Nuevo Video", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, null)
                }
            },
            actions = {
                TextButton(onClick = { onBack() }) {
                    Text("Publicar", fontWeight = FontWeight.Bold, color = Color(0xFF964300))
                }
            }
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Video Preview (con clic para seleccionar)
            Box(
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .clickable { videoPickerLauncher.launch("video/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedVideoUri != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF964300), modifier = Modifier.size(48.dp))
                        Text("Listp", color = Color(0xFF964300), fontSize = 10.sp)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Text("Seleccionar", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            TextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Escribe un pie de video...") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Divider(color = Color(0xFFF5F6F7))

        if (selectedVideoUri != null) {
            Text(
                "Video seleccionado: ${selectedVideoUri!!.lastPathSegment}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }

        ListItem(
            headlineContent = { Text("Etiquetar personas") },
            leadingContent = { Icon(Icons.Default.Person, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            modifier = Modifier.clickable { }
        )
        ListItem(
            headlineContent = { Text("Ubicación") },
            leadingContent = { Icon(Icons.Default.LocationOn, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            modifier = Modifier.clickable { }
        )
        ListItem(
            headlineContent = { Text("Añadir música") },
            leadingContent = { Icon(Icons.Default.MusicNote, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) },
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "Tu video se compartirá con tus seguidores y podrá aparecer en la sección de Videos.",
            modifier = Modifier.padding(16.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileScreen(profile: UserProfile, onEditProfile: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(Icons.Default.GridOn, Icons.Default.PlayCircle, Icons.Default.PersonPin)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Profile Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9873E))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFFE6E8EA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=200"),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat("Posts", "124")
                    ProfileStat("Seguidores", "1.5k")
                    ProfileStat("Siguiendo", "432")
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("${profile.ownerName} & ${profile.petName}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C2F30))
            Text("${profile.breed} 🐾", fontSize = 16.sp, color = Color(0xFF964300), fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ProfileInfoItem(Icons.Default.LocationOn, profile.location)
                ProfileInfoItem(Icons.Default.Email, profile.email)
                ProfileInfoItem(Icons.Default.Phone, profile.phone)
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEditProfile,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF964300),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Editar perfil", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF964300)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF964300)),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Compartir", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Custom Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color(0xFF964300),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF964300)
                )
            }
        ) {
            for ((index, icon) in tabs.withIndex()) {
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    icon = { Icon(icon, null) }
                )
            }
        }

        // Content Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(PHOTOS) { photo ->
                Image(
                    painter = rememberAsyncImagePainter(photo.url),
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun PhotoItem(photo: Photo) {
    var showDescription by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE6E8EA))
            .clickable { showDescription = !showDescription }
    ) {
        Image(
            painter = rememberAsyncImagePainter(photo.url),
            contentDescription = photo.alt,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (photo.id.toInt() % 2 == 0) 0.8f else 1.2f),
            contentScale = ContentScale.Crop
        )

        // Overlay de degradado para los textos
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (showDescription)
                            listOf(Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.7f))
                        else
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                        startY = 100f
                    )
                )
        )

        if (showDescription) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        photo.alt,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                photo.likes,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (photo.isTop) {
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("TOP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF964300), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true, name = "Main Screen")
@Composable
fun MainScreenPreview() {
    MascotaSocialTheme {
        MainScreen()
    }
}

@Preview(showBackground = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    MascotaSocialTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, name = "Post Card")
@Composable
fun PostCardPreview() {
    MascotaSocialTheme {
        PostCard(PHOTOS[0])
    }
}
