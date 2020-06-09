package com.abhishekjagushte.engage.ui.main.fragments.profile.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.main.fragments.profile.ProfileFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ProfileFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileFragmentViewModel::class)
    abstract fun bindViewModel(viewmodel: ProfileFragmentViewModel): ViewModel
}

