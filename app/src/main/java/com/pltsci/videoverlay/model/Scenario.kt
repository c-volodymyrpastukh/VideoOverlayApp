package com.pltsci.videoverlay.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val cellIndices: Set<Int>,
    val targetLinkIndex: Int
)

@Serializable
data class ScenarioLink(
    val imageUrl: String,
    val routes: List<Route> = emptyList()
)

@Serializable
data class Scenario(
    val links: List<ScenarioLink> = emptyList()
)
