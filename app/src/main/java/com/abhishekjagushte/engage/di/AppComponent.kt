package com.abhishekjagushte.engage.di

import android.app.Application
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.notifications.NotificationHandler
import com.abhishekjagushte.engage.ui.SplashScreenFragment
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatComponent
import com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen.di.ChatScreenComponent
import com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.eventscreen.di.EventComponent
import com.abhishekjagushte.engage.ui.main.MainActivity
import com.abhishekjagushte.engage.ui.main.MainComponent
import com.abhishekjagushte.engage.ui.main.fragments.chatlist.ChatListComponent
import com.abhishekjagushte.engage.ui.main.fragments.profile.di.ProfileComponent
import com.abhishekjagushte.engage.ui.main.fragments.search.di.SearchComponent
import com.abhishekjagushte.engage.ui.setup.fragments.login.di.LoginComponent
import com.abhishekjagushte.engage.ui.setup.fragments.setusername.di.SetUsernameComponent
import com.abhishekjagushte.engage.ui.setup.fragments.signup.di.SignUpComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjector
import javax.inject.Singleton


@Singleton
@Component(modules =
        [LocalStorageModule::class,
            NetworkModule::class,
            ViewModelBuilderModule::class,
            SubcomponentsModule::class])
interface AppComponent: AndroidInjector<EngageApplication>{

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun inject(splashScreen: SplashScreenFragment)
    fun inject(mainActivity: MainActivity)

    fun addMainComponent(): MainComponent.Factory
    fun addEventScreenComponent(): EventComponent.Factory
    fun addChatScreenComponent(): ChatScreenComponent.Factory
    fun addChatComponent(): ChatComponent.Factory
    fun inject(notificationHandler: NotificationHandler)
    fun addProfileComponent(): ProfileComponent.Factory
    fun addSearchComponent(): SearchComponent.Factory
    fun addChatListComponent(): ChatListComponent.Factory
    fun addLoginComponent(): LoginComponent.Factory
    fun addSignUpComponent(): SignUpComponent.Factory
    fun addSetUsernameComponent(): SetUsernameComponent.Factory
}




@Module(subcomponents =
    [LoginComponent::class,
    SignUpComponent::class,
    SetUsernameComponent::class,
    ChatListComponent::class,
    SearchComponent::class,
    ProfileComponent::class,
    ChatComponent::class,
    ChatScreenComponent::class,
    EventComponent::class,
    MainComponent::class])
object SubcomponentsModule
