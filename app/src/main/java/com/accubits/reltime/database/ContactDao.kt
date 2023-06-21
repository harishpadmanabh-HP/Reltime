package com.accubits.reltime.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>): Unit

    @Update
    suspend fun updateContact(contact: Contact): Int

    @Delete
    suspend fun deleteContact(contact: Contact): Int

    @Query("DELETE FROM contact_table")
    suspend fun deleteAllContacts(): Int

    @Query("SELECT * FROM contact_table ORDER BY contact_name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE exist like :showAllContacts ORDER BY contact_name ASC")
    fun getAllContacts(showAllContacts: Boolean): Flow<List<Contact>>
}