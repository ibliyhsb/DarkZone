package cl.duoc.app.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cl.duoc.app.model.data.config.AppDatabase
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import cl.duoc.app.ui.screen.BlogCreateScreen
import cl.duoc.app.ui.screen.BlogScreen
import cl.duoc.app.ui.screen.HistoryScreen
import cl.duoc.app.ui.screen.FormularioRegistroScreen
import cl.duoc.app.ui.screen.StartScreen
import cl.duoc.app.ui.screen.LoginScreen
import cl.duoc.app.ui.screen.ProfileScreen
import cl.duoc.app.ui.screen.NewsScreen
import cl.duoc.app.ui.screen.NewsDetailScreen
import cl.duoc.app.viewmodel.BlogViewModel
import cl.duoc.app.viewmodel.BlogViewModelFactory
import cl.duoc.app.viewmodel.LoginViewModel
import cl.duoc.app.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val Login = "login"
    const val Registro = "registro"
    const val Start = "start"
    const val History = "historias"
    const val Blogs = "blogs"
    const val BlogCreate = "blog_create"
    const val Profile = "perfil"
    const val News = "noticias"
    const val NewsDetail = "news_detail/{id}"
}

@Composable
fun NavBar() {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.Registro) {
        composable(Routes.Registro) {
            FormularioRegistroScreen(onNavigateToLogin = {
                nav.navigate(Routes.Login)
            })
        }

        composable(Routes.Login) {
            val context = LocalContext.current
            val db = remember(context) { AppDatabase.getDatabase(context) }
            val repository = remember(db) { cl.duoc.app.model.data.repository.FormularioUsuarioRepository(db.formularioUsuarioDao()) }
            val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(it, repository))

            LoginScreen(
                viewModel = viewModel,
                onAuthenticated = { username ->
                    nav.navigate("main_shell/$username") {
                        popUpTo(0)
                    }
                },
                onNavigateToRegistro = {
                    nav.navigate(Routes.Registro)
                }
            )
        }

        navigation(
            startDestination = Routes.Start,
            route = "main_shell/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            composable(Routes.Start) { backStackEntry ->
                DrawerScaffold(currentRoute = Routes.Start, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    StartScreen()
                }
            }
            composable(Routes.History) { backStackEntry ->
                DrawerScaffold(currentRoute = Routes.History, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    HistoryScreen()
                }
            }
            composable(Routes.Blogs) { backStackEntry ->
                val blogVm = getSharedBlogViewModel(backStackEntry = backStackEntry, nav = nav)
                DrawerScaffold(currentRoute = Routes.Blogs, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    BlogScreen(viewModel = blogVm, onNewBlog = { nav.navigate(Routes.BlogCreate) })
                }
            }
            composable(Routes.News) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("main_shell/{username}") }
                val newsVm: cl.duoc.app.viewmodel.NewsViewModel = viewModel(viewModelStoreOwner = parentEntry)
                DrawerScaffold(currentRoute = Routes.News, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    NewsScreen(viewModel = newsVm, onOpen = { id -> nav.navigate("news_detail/$id") })
                }
            }
            composable("news_detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("main_shell/{username}") }
                val newsVm: cl.duoc.app.viewmodel.NewsViewModel = viewModel(viewModelStoreOwner = parentEntry)
                DrawerScaffold(currentRoute = Routes.News, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    NewsDetailScreen(id = id, viewModel = newsVm)
                }
            }
            composable(Routes.BlogCreate) { backStackEntry ->
                val blogVm = getSharedBlogViewModel(backStackEntry = backStackEntry, nav = nav)
                DrawerScaffold(currentRoute = Routes.BlogCreate, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    BlogCreateScreen(viewModel = blogVm, onSaved = { nav.popBackStack() })
                }
            }
            composable(Routes.Profile) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("main_shell/{username}") }
                val username = parentEntry.arguments?.getString("username") ?: ""
                DrawerScaffold(currentRoute = Routes.Profile, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    ProfileScreen(username = username, navController = nav)
                }
            }
        }
    }
}

@Composable
private fun getSharedBlogViewModel(backStackEntry: NavBackStackEntry, nav: NavController): BlogViewModel {
    val parentEntry = remember(backStackEntry) { nav.getBackStackEntry("main_shell/{username}") }
    val context = LocalContext.current
    val db = remember(context) { AppDatabase.getDatabase(context) }
    val repo = remember(db) { FormularioBlogsRepository(db.formularioBlogsDao()) }
    val factory = remember(repo) { BlogViewModelFactory(parentEntry, repo) }
    return viewModel(viewModelStoreOwner = parentEntry, factory = factory)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val destinations = listOf(
        DrawerItem("Inicio", Routes.Start, Icons.Default.Home),
        DrawerItem("Historias", Routes.History, Icons.Default.History),
        DrawerItem("Blogs", Routes.Blogs, Icons.Default.Book),
        DrawerItem("Perfil", Routes.Profile, Icons.Default.SupervisedUserCircle),
        DrawerItem("Noticias", Routes.News, Icons.Default.Newspaper)
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
                Spacer(modifier = Modifier.weight(1f))
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión") },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Routes.Login) {
                                popUpTo(0)
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
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
private fun appBarTitle(route: String?): String = when (route) {
    Routes.Start -> "Inicio"
    Routes.History -> "Historias"
    Routes.Profile -> "Perfil"
    Routes.Blogs -> "Blogs"
    Routes.BlogCreate -> "Crear Blog"
    Routes.News -> "Noticias"
    else -> ""
}