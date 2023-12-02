package com.net

import android.content.Context
import com.net.remote.Api
import com.net.remote.Repository
import com.net.remote.createWebService
import com.net.remote.getOkHttpClient
import com.net.util.Constant
import com.net.vm.GetPDFViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

/**
 *
 * java类作用描述
 * zrj 2021/6/26 10:44
 * 更新者 2021/6/26 10:44
 */

val viewModelModule = module {
    viewModel { GetPDFViewModel(get()) }
}


val remoteModule = module {
    single { getOkHttpClient() }
    single { createWebService<Api>(get(), Constant.AI_BASE_URL) }
}

val repositoryModule = module {
    single { Repository(get()) }
}

val appModule = listOf(viewModelModule, remoteModule, repositoryModule)


object KoinInit {
    private val viewModelModule = module {
        viewModel { GetPDFViewModel(get()) }
    }

    private val remoteModule = module {
        single { getOkHttpClient() }
        single { createWebService<Api>(get(), Constant.AI_BASE_URL) }
    }

    private val repositoryModule = module {
        single { Repository(get()) }
    }

    private val appModule = listOf(viewModelModule, remoteModule, repositoryModule)

    fun koinStart(androidContext: Context) {
        startKoin {
            AndroidLogger(Level.DEBUG)
            androidContext(androidContext)
            modules(appModule)
        }
    }
}
