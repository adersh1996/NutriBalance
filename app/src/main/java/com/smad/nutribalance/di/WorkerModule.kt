package com.smad.nutribalance.di

import android.content.Context
import androidx.work.WorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Note: HiltWorkerFactory is provided automatically by Hilt when hilt-work is on the classpath.
// This module provides any additional bindings needed by workers.
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule
