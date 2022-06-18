package com.example.tummocduplicate.view

import com.example.tummocduplicate.utils.Constants

sealed class Screens(val route : String) {
    object Home : Screens(route = Constants.LIST_OF_ROUTES_SCREEN)
    object Maps : Screens(route = Constants.MAPS_NAVIGATION_SCREEN)
}
