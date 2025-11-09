package cl.duoc.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import cl.duoc.app.ui.screen.FormularioServicioScreen
import cl.duoc.app.ui.screen.StartScreen
import cl.duoc.app.ui.screen.LoginScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val Login = "login"
    const val Start = "start"
    const val Form = "form"
}

@Composable
fun NavBar() {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.Login) {
        // LOGIN
        composable(Routes.Login) {
            LoginScreen(
                onAuthenticated = {
                    nav.navigate(Routes.Start) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // SHELL (drawer + scaffold)
        navigation(startDestination = Routes.Start, route = "main_shell") {
            composable(Routes.Start) {
                DrawerScaffold(
                    currentRoute = Routes.Start,
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope
                ) {
                    StartScreen()
                }
            }
            composable(Routes.Form) {
                DrawerScaffold(
                    currentRoute = Routes.Form,
                    onNavigate = { nav.navigate(it) },
                    drawerState = drawerState,
                    scope = scope
                ) {
                    FormularioServicioScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    content: @Composable () -> Unit
) {
    val destinations = listOf(
        DrawerItem("Inicio", Routes.Start),
        DrawerItem("Formulario de servicio", Routes.Form)
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

private data class DrawerItem(val label: String, val route: String)

@Composable
private fun appBarTitle(route: String?): String = when (route) {
    Routes.Start -> "Inicio"
    Routes.Form  -> "Formulario de Servicio"
    else         -> ""
}