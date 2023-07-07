package com.accubits.reltime.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.activity.settings.adapter.FaqTagAdapter
import com.accubits.reltime.activity.v2.transfer.pagingSource.TransferTransactionPagingV2Source
import com.accubits.reltime.activity.v2.wallet.TransactionPagedV2Adapter
import com.accubits.reltime.activity.v2.wallet.TransactionPagingV2Source
import com.accubits.reltime.activity.v2.wallet.accountDetail.AccountTransactionPagingSourceV2
import com.accubits.reltime.api.RetrofitApi
import com.accubits.reltime.database.ContactDao
import com.accubits.reltime.database.ContactDatabase
import com.accubits.reltime.database.ContactRepository
import com.accubits.reltime.helpers.BiometricPromptUtils
import com.accubits.reltime.helpers.DeCryptor
import com.accubits.reltime.helpers.EnCryptor
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.BiometricLoginRepository
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.retrofit.RetrofitClient
import com.accubits.reltime.retrofit.TokenAuthenticator
import com.accubits.reltime.utils.AppConfig
import com.accubits.reltime.utils.cryptoutils.BitcoinUtils
import com.accubits.reltime.views.borrow.paging.BorrowPagingSource
import com.accubits.reltime.views.lend.pageing.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TAG = "AppModule"
    lateinit var networks: Network

    @Singleton
    @Provides
    fun provideRoomDBInstance(@ApplicationContext context: Context): ContactDatabase {
        return ContactDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesRoomDao(contactDatabase: ContactDatabase): ContactDao {
        return contactDatabase.contactDao
    }

    @Singleton
    @Provides
    fun providesRoomRepository(contactDao: ContactDao): ContactRepository {
        return ContactRepository(contactDao)
    }

    @Singleton
    @Provides
    fun providesRetrofit(gson: Gson, authenticator: TokenAuthenticator): Retrofit {
        return Retrofit.Builder()
            .client(getRetrofitClient(authenticator))
            .baseUrl(AppConfig.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun providesTokenAuthenticator(@ApplicationContext context: Context): TokenAuthenticator {
        return TokenAuthenticator(context, proviedPreferncemanager(context))
    }

    @Singleton
    @Provides
    fun providesBiometric(@ApplicationContext context: Context): BiometricPromptUtils {
        return BiometricPromptUtils(context)
    }

    @Singleton
    @Provides
    fun providesGson(): Gson {
        return GsonBuilder().setLenient().create()
    }


    @Singleton
    @Provides
    fun proviedPreferncemanager(@ApplicationContext context: Context): PreferenceManager =
        PreferenceManager(context)


    @Singleton
    @Provides
    fun providesRetrofitApi(retrofit: Retrofit): RetrofitApi =
        retrofit.create(RetrofitApi::class.java)

    @Singleton
    @Provides
    fun providesReltimeRepo(retrofitApi: RetrofitApi, gson: Gson): ReltimeRepository =
        ReltimeRepository(retrofitApi, gson)

    @Singleton
    @Provides
    fun providesPhoneNumberUtil(@ApplicationContext context: Context): PhoneNumberUtil =
        PhoneNumberUtil.getInstance(context)

    @Singleton
    @Provides
    fun getRetrofitClient(authenticator: TokenAuthenticator? = null): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Accept", "application/json")
                    it.addHeader("Time-Zone", TimeZone.getDefault().id)
                    it.addHeader("App", "Nagra")
                }.build())
            }.also { client ->
                authenticator?.let { client.authenticator(it) }
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
    }

    @Singleton
    @Provides
    fun providesConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideAdapter(): FaqTagAdapter = FaqTagAdapter()

    @Provides
    @Singleton
    fun provideDeCryptor(): DeCryptor = DeCryptor()

    @Provides
    @Singleton
    fun provideEnCryptor(): EnCryptor = EnCryptor()

    @Provides
    @Singleton
    fun provideLendingListAdapter1(): LendingTokenListPagedAdapter1 =
        LendingTokenListPagedAdapter1()

    @Provides
    @Singleton
    fun provideLendingHistoryListAdapter(): LendingHistoryListPagedAdapter =
        LendingHistoryListPagedAdapter()

    @Provides
    @Singleton
    fun provideAllLendingListAdapter1(): AllLendingTokenListPagedAdapter1 =
        AllLendingTokenListPagedAdapter1()

    @Singleton
    @Provides
    fun provideBorrowDataSource(
        @ApplicationContext context: Context,
        reltimeRepository: ReltimeRepository,
        preferenceManager: PreferenceManager
    ): BorrowPagingSource {
        return BorrowPagingSource(reltimeRepository, preferenceManager)
    }

    @Singleton
    @Provides
    fun provideTransferTransactionDataSource(
        @ApplicationContext context: Context,
        reltimeRepository: ReltimeRepository,
        preferenceManager: PreferenceManager
    ): TransferTransactionPagingV2Source {
        return TransferTransactionPagingV2Source(reltimeRepository, preferenceManager)
    }


    @Singleton
    @Provides
    fun providePagingDataSource(
        @ApplicationContext context: Context,
        reltimeRepository: ReltimeRepository,
        preferenceManager: PreferenceManager
    ): TokenPagingSource {
        return TokenPagingSource(reltimeRepository, preferenceManager)
    }

    @Singleton
    @Provides
    fun providesAllLendingPagingSource(
        @ApplicationContext context: Context,
        reltimeRepository: ReltimeRepository,
        preferenceManager: PreferenceManager
    ): AllLendingPagingSource {
        return AllLendingPagingSource(reltimeRepository, preferenceManager)
    }

    @Singleton
    @Provides
    fun providesLendingHistoryPagingSource(
        @ApplicationContext context: Context,
        reltimeRepository: ReltimeRepository,
        preferenceManager: PreferenceManager
    ): LendingHistoryPagingSource {
        return LendingHistoryPagingSource(reltimeRepository, preferenceManager)
    }

    @Singleton
    @Provides
    fun providesBiometricLoginRepo(): BiometricLoginRepository =
        BiometricLoginRepository()


    @Singleton
    @Provides
    fun provideTransactionDataSourceV2(
        @ApplicationContext context: Context,
        reltimeRepository: ReltimeRepository,
        preferenceManager: PreferenceManager
    ): TransactionPagingV2Source {
        return TransactionPagingV2Source(reltimeRepository, preferenceManager)
    }

    @Singleton
    @Provides
    fun provideTransactionHistoryV2Adapter(@ApplicationContext context: Context): TransactionPagedV2Adapter {
        return TransactionPagedV2Adapter(context)
    }

    @Singleton
    @Provides
    fun provideAccountTransactionDataSourceV2(
        @ApplicationContext context: Context,
        reltimeRepository: ReltimeRepository,
        preferenceManager: PreferenceManager
    ): AccountTransactionPagingSourceV2 {
        return AccountTransactionPagingSourceV2(reltimeRepository, preferenceManager)
    }

    @Provides
    @Singleton
    fun provideBitcoinUtil(preferenceManager: PreferenceManager): BitcoinUtils = BitcoinUtils(preferenceManager)

}