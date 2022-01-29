package com.example.news.data.di.module

import com.example.news.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentInjectorsModule::class])
    abstract fun contributeMainActivity(): MainActivity
}