package com.abhishekjagushte.engage.ui.main.fragments.profile.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.main.fragments.profile.ProfileActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ProfileFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileActivityViewModel::class)
    abstract fun bindViewModel(viewmodel: ProfileActivityViewModel): ViewModel
}

