package com.accubits.reltime.database

class ContactRepository(private val dao: ContactDao) {

    val contacts = dao.getAllContacts()

    fun getAllContacts(showAllContacts: Boolean)=dao.getAllContacts(showAllContacts)

    suspend fun insert(contact: Contact): Long {
        return dao.insertContact(contact)
    }

    suspend fun insertContacts(contacts: List<Contact>): Unit {
        dao.insertContacts(contacts)
    }

    suspend fun update(contact: Contact): Int {
        return dao.updateContact(contact)
    }

    suspend fun delete(contact: Contact): Int {
        return dao.deleteContact(contact)
    }

    suspend fun deleteAllContacts(): Int {
        return dao.deleteAllContacts()
    }
}