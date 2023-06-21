package com.accubits.reltime.activity.v2.transfer.listener

import com.accubits.reltime.activity.myAccounts.model.BankAccount

interface RelTimeAccountItemClickListener {
    fun onAccountClicked(bankAccount: BankAccount)
}
