package com.abhishekjagushte.engage.ui.main

import dagger.Subcomponent


@Subcomponent(modules = [MainActivityModule::class])
interface MainComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(mainActivity: MainActivity)
}

