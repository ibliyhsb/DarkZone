package cl.duoc.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.rememberCoroutineScope
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository
import cl.duoc.app.viewmodel.LoginViewModel
import cl.duoc.app.viewmodel.LoginViewModelFactory
import cl.duoc.app.viewmodel.NewsViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import cl.duoc.app.ui.screen.*
import cl.duoc.app.viewmodel.BlogViewModel
import cl.duoc.app.viewmodel.BlogViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val Login = "login"
    const val Registro = "registro"
    const val Start = "start"
    const val Form = "form"
    const val History = "historias"
    const val Blogs = "blogs"
    const val BlogCreate = "blog_create"
    const val BlogDetail = "blog_detail/{blogId}"
    const val Favorites = "favoritos"
    const val Contact = "contacto"
    const val Profile = "perfil"
    const val News = "noticias"
    const val RecentBlogs = "recent_blogs"

    fun blogDetail(blogId: Long) = "blog_detail/$blogId"
}

@Composable
fun NavBar() {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.Login) {
        composable(Routes.Login) {
            val context = LocalContext.current
            val db = remember(context) { AppDatabase.getDatabase(context) }
            val repo = remember(db) { FormularioUsuarioRepository(db.formularioUsuarioDao()) }
            val entry = remember { nav.getBackStackEntry(Routes.Login) }
            val loginFactory = remember(repo, entry) { LoginViewModelFactory(entry, repo) }
            val loginVm: LoginViewModel = viewModel(viewModelStoreOwner = entry, factory = loginFactory)

            LoginScreen(
                viewModel = loginVm,
                onAuthenticated = {
                    nav.navigate("main_shell") { // Navigate to the nested graph
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegistro = { nav.navigate(Routes.Registro) }
            )
        }

        navigation(startDestination = Routes.Start, route = "main_shell") { 
            composable(Routes.Start) {
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    StartScreen()
                }
            }
            composable(Routes.Form) {
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    FormularioServicioScreen()
                }
            }
            composable(Routes.History) {
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    HistoryScreen()
                }
            }

            composable(Routes.Blogs) {
                val blogVm = getSharedBlogViewModel(backStackEntry = it, nav = nav)
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    BlogScreen(viewModel = blogVm, onNewBlog = { nav.navigate(Routes.BlogCreate) }, onBlogClicked = { blogId -> nav.navigate(Routes.blogDetail(blogId)) })
                }
            }
            composable(Routes.BlogCreate) {
                val blogVm = getSharedBlogViewModel(backStackEntry = it, nav = nav)
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    BlogCreateScreen(viewModel = blogVm, onSaved = { nav.popBackStack() })
                }
            }
            composable(
                route = Routes.BlogDetail,
                arguments = listOf(navArgument("blogId") { type = NavType.LongType })
            ) {
                val blogId = it.arguments?.getLong("blogId")
                val blogVm = getSharedBlogViewModel(backStackEntry = it, nav = nav)
                DrawerScaffold(
                    onNavigate = { route -> nav.navigate(route) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    BlogCreateScreen(
                        viewModel = blogVm, 
                        onSaved = { nav.popBackStack() }, 
                        blogId = blogId,
                        readOnly = true
                    )
                }
            }

            composable(Routes.Profile) {
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    ProfileScreen()
                }
            }
            composable(Routes.Favorites) {
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    FavoritiesScreen()
                }
            }
            composable(Routes.Contact) {
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    ContactScreen()
                }
            }
            composable(Routes.News) {
                    val newsEntry = remember { nav.getBackStackEntry("main_shell") }
                    val newsVm: NewsViewModel = viewModel(viewModelStoreOwner = newsEntry)
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    NewsScreen(viewModel = newsVm, onOpen = {})
                }
            }
            composable(Routes.RecentBlogs) {
                val blogVm = getSharedBlogViewModel(backStackEntry = it, nav = nav)
                DrawerScaffold(
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    RecentBlogsScreen(
                        viewModel = blogVm,
                        onBlogClicked = { blogId -> nav.navigate(Routes.blogDetail(blogId)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun getSharedBlogViewModel(backStackEntry: NavBackStackEntry, nav: NavController): BlogViewModel {
    val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("main_shell") }
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getDatabase(context) }
    val repo = remember(db) { FormularioBlogsRepository(db.formularioBlogsDao()) }
    val factory = remember(repo, parentEntry) { BlogViewModelFactory(parentEntry, repo) }
    return viewModel(viewModelStoreOwner = parentEntry, factory = factory)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerScaffold(
    onNavigate: (String) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val destinations = listOf(
        DrawerItem("Inicio", Routes.Start, Icons.Default.Home),
        DrawerItem("Historias", Routes.History, Icons.Default.History),
        DrawerItem("Blogs", Routes.Blogs, Icons.Default.Book),
        DrawerItem("Blogs Recientes", Routes.RecentBlogs, Icons.Default.History),
        DrawerItem("Perfil", Routes.Profile, Icons.Default.SupervisedUserCircle),
        DrawerItem("Favoritos", Routes.Favorites, Icons.Default.Stars),
        DrawerItem("Contacto", Routes.Contact, Icons.Default.Help),
        DrawerItem("Noticias", Routes.News, Icons.Default.Newspaper),
        DrawerItem("Formulario de servicio", Routes.Form, Icons.Default.Description)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menú",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                destinations.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != item.route) {
                                onNavigate(item.route)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(appBarTitle(currentRoute)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { padding ->
            Surface(Modifier.padding(padding)) {
                content()
            }
        }
    }
}

private data class DrawerItem(val label: String, val route: String, val icon: ImageVector)

@Composable
private fun appBarTitle(route: String?): String = when {
    route == Routes.Start -> "Inicio"
    route == Routes.Form -> "Formulario de Servicio"
    route == Routes.History -> "Historias"
    route == Routes.Favorites -> "Favoritos"
    route == Routes.Profile -> "Perfil"
    route == Routes.Blogs -> "Blogs"
    route == Routes.BlogCreate -> "Crear Blog"
    route?.startsWith("blog_detail") == true -> "Detalle del Blog"
    route == Routes.Contact -> "Contacto"
    route == Routes.News -> "Noticias"
    route == Routes.RecentBlogs -> "Blogs Recientes"
    else -> ""
}