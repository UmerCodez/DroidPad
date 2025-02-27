
/*
 *     This file is a part of DroidPad (https://www.github.com/umer0586/DroidPad)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     DroidPad is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     DroidPad is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DroidPad. If not, see <https://www.gnu.org/licenses/>.
 *
 */
package com.github.umer0586.droidpad.data.daggermodules

import android.content.Context
import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadItemRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import com.github.umer0586.droidpad.data.repositories.JsonRepository
import com.github.umer0586.droidpad.data.repositories.PreferenceRepository
import com.github.umer0586.droidpad.data.repositoriesimp.ConnectionConfigRepositoryImp
import com.github.umer0586.droidpad.data.repositoriesimp.ControlPadItemRepositoryImp
import com.github.umer0586.droidpad.data.repositoriesimp.ControlPadRepositoryImp
import com.github.umer0586.droidpad.data.repositoriesimp.JsonRepositoryImp
import com.github.umer0586.droidpad.data.repositoriesimp.PreferenceRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryDI {
    
    @Provides
    @ViewModelScoped
    fun provideControlPadRepository(appDatabase: AppDatabase): ControlPadRepository {
        return ControlPadRepositoryImp(appDatabase)
    }

    @Provides
    @ViewModelScoped
    fun provideControlPadItemRepository(appDatabase: AppDatabase): ControlPadItemRepository {
        return ControlPadItemRepositoryImp(appDatabase)
    }

    @Provides
    @ViewModelScoped
    fun provideConnectionConfigurationRepository(appDatabase: AppDatabase): ConnectionConfigRepository {
        return ConnectionConfigRepositoryImp(appDatabase)
    }

    @Provides
    @ViewModelScoped
    fun provideSettingsRepository(@ApplicationContext context: Context): PreferenceRepository {
        return PreferenceRepositoryImp(context)
    }

    @Provides
    @ViewModelScoped
    fun provideJsonRepository() : JsonRepository {
        return JsonRepositoryImp()
    }


}