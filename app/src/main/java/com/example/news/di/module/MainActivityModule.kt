package com.example.news.di.module

import com.example.news.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentInjectorsModule::class])
    abstract fun contributeMainActivity(): MainActivity
}