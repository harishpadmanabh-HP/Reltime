package com.accubits.reltime.activity.v2.transfer.listener

import com.accubits.reltime.activity.myAccounts.model.BankAccount
import com.accubits.reltime.activity.myAccounts.model.JointAccount

interface JointAccountItemClickListener {
    fun onAccountClicked(jointAccount: JointAccount)
}
