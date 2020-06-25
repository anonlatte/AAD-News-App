package com.example.news.di.module

import com.example.news.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentInjectorsModule {

    @ContributesAndroidInjector
    abstract fun injectHomeFragment(): HomeFragment

}