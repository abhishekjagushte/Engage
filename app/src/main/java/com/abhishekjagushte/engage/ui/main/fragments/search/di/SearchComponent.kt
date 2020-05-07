package com.abhishekjagushte.engage.ui.main.fragments.search.di

import com.abhishekjagushte.engage.ui.main.fragments.search.SearchFragment
import com.abhishekjagushte.engage.ui.setup.fragments.login.LoginFragment
import dagger.Subcomponent


@Subcomponent(modules = [SearchFragmentModule::class])
interface SearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }

    fun inject(fragment: SearchFragment)
}

