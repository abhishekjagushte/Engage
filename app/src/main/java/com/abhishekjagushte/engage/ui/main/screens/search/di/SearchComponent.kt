package com.abhishekjagushte.engage.ui.main.screens.search.di

import com.abhishekjagushte.engage.ui.main.screens.search.SearchFragment
import dagger.Subcomponent


@Subcomponent(modules = [SearchFragmentModule::class])
interface SearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }

    fun inject(fragment: SearchFragment)
}

