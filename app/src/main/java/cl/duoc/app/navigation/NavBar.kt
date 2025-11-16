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
import cl.duoc.app.ui.screen.BlogCreateScreen
import cl.duoc.app.ui.screen.BlogScreen
import cl.duoc.app.ui.screen.FormularioRegistroScreen
import cl.duoc.app.ui.screen.StartScreen
import cl.duoc.app.ui.screen.LoginScreen
import cl.duoc.app.ui.screen.ProfileScreen
import cl.duoc.app.ui.screen.NewsScreen
import cl.duoc.app.viewmodel.BlogViewModel
import cl.duoc.app.viewmodel.BlogViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val START = "start"
    const val BLOGS = "blogs"
    const val BLOG_CREATE = "blog_create"
    const val PROFILE = "perfil"
    const val NEWS = "noticias"
    const val BLOG_DETAIL = "blog_detail/{blogId}"
}

@Suppress("UnrememberedGetBackStackEntry")
@Composable
fun NavBar() {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            val context = LocalContext.current
            val db = remember(context) { AppDatabase.getDatabase(context) }
            val repo = remember(db) { FormularioUsuarioRepository(db.formularioUsuarioDao()) }
            val entry = remember(nav.getBackStackEntry(Routes.LOGIN)) { nav.getBackStackEntry(Routes.LOGIN) }
            val loginFactory = remember(repo, entry) { LoginViewModelFactory(entry, repo) }
            val loginVm: LoginViewModel = viewModel(viewModelStoreOwner = entry, factory = loginFactory)

            LoginScreen(
                viewModel = loginVm,
                onAuthenticated = {
                    nav.navigate("main_shell") { // Navigate to the nested graph
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegistro = { nav.navigate(Routes.REGISTRO) }
            )
        }
        composable(Routes.REGISTRO) {
            FormularioRegistroScreen(onNavigateToLogin = {
                nav.navigate(Routes.LOGIN)
            })
        }

        navigation(startDestination = Routes.START, route = "main_shell") {
            composable(Routes.START) {
                DrawerScaffold(
                    currentRoute = Routes.START,
                    onNavigate = { route -> nav.navigate(route) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    StartScreen()
                }
            }
            composable(Routes.BLOGS) { backStackEntry ->
                val blogVm = getSharedBlogViewModel(backStackEntry = backStackEntry, nav = nav)
                DrawerScaffold(currentRoute = Routes.BLOGS, onNavigate = { route -> nav.navigate(route) }, drawerState = drawerState, scope = scope, navController = nav) {
                    BlogScreen(
                        viewModel = blogVm,
                        onNewBlog = { nav.navigate(Routes.BLOG_CREATE) },
                        onBlogClicked = { blogId ->
                            nav.navigate("blog_detail/$blogId")
                        }
                    )
                }
            }
            composable(Routes.BLOG_CREATE) {
                val blogVm = getSharedBlogViewModel(backStackEntry = it, nav = nav)
                DrawerScaffold(
                    currentRoute = Routes.BLOG_CREATE,
                    onNavigate = { route -> nav.navigate(route) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    BlogCreateScreen(viewModel = blogVm, onSaved = { nav.popBackStack() })
                }
            }
            composable(
                route = Routes.BLOG_DETAIL,
                arguments = listOf(navArgument("blogId") { type = NavType.LongType })
            ) {
                val blogId = it.arguments?.getLong("blogId")
                val blogVm = getSharedBlogViewModel(backStackEntry = it, nav = nav)
                DrawerScaffold(
                    currentRoute = Routes.BLOG_DETAIL,
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

            composable(Routes.PROFILE) {
                DrawerScaffold(
                    currentRoute = Routes.PROFILE,
                    onNavigate = { route -> nav.navigate(route) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    ProfileScreen()
                }
            }
                composable(Routes.NEWS) {
                    val newsEntry = remember(nav.getBackStackEntry("main_shell")) { nav.getBackStackEntry("main_shell") }
                    val newsVm: NewsViewModel = viewModel(viewModelStoreOwner = newsEntry)
                DrawerScaffold(
                    currentRoute = Routes.NEWS,
                    onNavigate = { route -> nav.navigate(route) },
                    drawerState = drawerState,
                    scope = scope,
                    navController = nav
                ) {
                    NewsScreen(viewModel = newsVm, onOpen = {})
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
        DrawerItem("Inicio", Routes.START, Icons.Default.Home),
        DrawerItem("Blogs", Routes.BLOGS, Icons.Default.Book),
        DrawerItem("Perfil", Routes.PROFILE, Icons.Default.SupervisedUserCircle),
        DrawerItem("Noticias", Routes.NEWS, Icons.Default.Newspaper)
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
                            navController.navigate(Routes.LOGIN) {
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
    Routes.START -> "Inicio"
    Routes.PROFILE -> "Perfil"
    Routes.BLOGS -> "Blogs"
    Routes.BLOG_CREATE -> "Crear Blog"
    Routes.NEWS -> "Noticias"
    Routes.BLOG_DETAIL -> "Detalle del Blog"
    else -> ""
}