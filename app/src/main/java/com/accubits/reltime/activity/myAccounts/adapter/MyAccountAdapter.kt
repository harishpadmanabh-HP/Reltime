package com.accubits.reltime.activity.myAccounts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.accubits.reltime.R
import com.accubits.reltime.activity.myAccounts.ItemClickListener
import com.accubits.reltime.activity.myAccounts.adapter.MyAccountListAdapter.AccountDataHolder
import com.accubits.reltime.activity.myAccounts.model.*
import com.accubits.reltime.databinding.AccountItemBinding
import com.accubits.reltime.utils.Extensions.getAccountBalanceWithCoinCode
import com.accubits.reltime.utils.Extensions.getAccountId
import com.accubits.reltime.utils.Extensions.loadImageWithUrl
import com.accubits.reltime.utils.convertRTOtoEURO
import com.accubits.reltime.utils.isBrandLogo

/*
class MyAccountAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<MyAccountAdapter.AccountDataHolder>() {

    val list = ArrayList<ReltimeAccount>()
    override fun getItemCount(): Int {
        return list.size
    }

    inner class AccountDataHolder(private val binder: AccountItemBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun setWalletItem(
            accountName: String, userAddress: String, displayBalance: String, currentValue: String?,
            type: String, account: ReltimeAccount
        ) {
            binder.apply {
                tvAccountName.text = accountName
                tvHash.text = userAddress
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.balance)
                tvBalanceLabel.visibility = View.VISIBLE
                tvBalance.text =
                    account.getAccountBalanceWithCoinCode()// Utils.getRTOAmount(displayBalance)
                ivTypeIcon.setImageResource(R.drawable.ic_rto_home)
                ivEdit.visibility = View.INVISIBLE
                ivQr.visibility = View.VISIBLE
                ivCopy.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivDelete.visibility = View.GONE
                root.setOnClickListener {

                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(userAddress, null)
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(userAddress, null)
                }
            }
        }

        fun setCrypto(
            account: CryptoWallet
        ) {
            binder.apply {
                tvAccountName.text = account.coin_name
                tvHash.text = account.address
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.balance)
                tvBalanceLabel.visibility = View.VISIBLE
                tvBalance.text =
                    account.getAccountBalanceWithCoinCode()// Utils.getRTOAmount(displayBalance)
                ivTypeIcon.loadImageWithUrl(account.icon) {
                    if (!it) {
                        ivTypeIcon.setImageResource(R.drawable.ic_crypto)
                    }
                }
                ivEdit.visibility = View.INVISIBLE
                ivQr.visibility = View.VISIBLE
                ivCopy.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivDelete.visibility = View.GONE
                root.setOnClickListener {

                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(account.address, null)
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(account.address, null)
                }
            }
        }

        fun setJointAccountItem(account: JointAccount) {
            binder.apply {
                tvAccountName.text = account.name
                tvHash.text =
                    (if (account.members <= 1) "${account.members} Member" else "${account.members} Members")
                tvBalance.text = (if (account.rtoBalance == "") "0.00 RTO" else account.rtoBalance)
                ivEdit.visibility = View.VISIBLE
                ivDelete.visibility = View.GONE
                ivCopy.visibility = View.VISIBLE
                ivQr.visibility = View.VISIBLE
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.balance)
                tvBalanceLabel.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivTypeIcon.setImageResource(R.drawable.ic_add_joint)
                root.setOnClickListener {
                    listener.onAccountItemSelected("jointAccount", account, account.id)
                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(
                        account.address,
                        it.context.resources.getString(R.string.joint_account_address)
                    )
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(
                        account.address,
                        it.context.resources.getString(R.string.joint_account_address)
                    )
                }
                if (account.isAdmin) {
                    ivEdit.setOnClickListener {
                        listener.onJointAccountEdit(account)
                    }
                    ivEdit.visibility = View.VISIBLE
                } else {
                    ivEdit.visibility = View.INVISIBLE
                }
            }
        }

        fun setBankAccountItem(account: BankAccount) {
            binder.apply {
                tvAccountName.text = account.fullName
                tvHash.text = account.accountNumber
                tvBalance.text = account.address
                ivEdit.visibility = View.INVISIBLE
                ivDelete.visibility = View.GONE
                ivCopy.visibility = View.VISIBLE
                ivQr.visibility = View.VISIBLE
                ivTypeIcon.setImageResource(R.drawable.ic_bank_colored)
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.address)
                tvBalanceLabel.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                root.setOnClickListener {
                    listener.onAccountItemSelected("bankAccount", account, account.id)
                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(
                        account.accountNumber,
                        it.context.resources.getString(R.string.bank_account_number)
                    )
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(
                        account.accountNumber,
                        it.context.resources.getString(R.string.bank_account_number)
                    )
                }
            }
        }

        fun setCardItem(card: Card) {
            binder.apply {
                tvAccountName.text = card.cardName
                tvHash.text = card.cardNumber
                tvBalance.text = card.cardType
                ivEdit.visibility = View.INVISIBLE
                ivCopy.visibility = View.INVISIBLE
                ivQr.visibility = View.INVISIBLE
                ivDelete.visibility = View.VISIBLE
                tvBalanceLabel.visibility = View.INVISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivTypeIcon.setImageResource(R.drawable.ic_card_2)
                root.setOnClickListener {
                    listener.onAccountItemSelected("card", card, -1)
                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(card.cardNumber, null)
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(card.cardNumber, null)
                }
                ivDelete.setOnClickListener {
                    listener.onDelete("card", card, card.id)
                }
            }
        }

    }

    fun setItems(accounts: List<ReltimeAccount>) {
        val oldSize = list.size
        list.clear()
        list.addAll(accounts)
        if (oldSize > list.size)
            notifyDataSetChanged()
        else
            notifyItemRangeChanged(0, list.size)
    }

    override fun onBindViewHolder(
        holder: AccountDataHolder,
        position: Int
    ) {
        when (val account = list[position]) {
            is RTO ->
                holder.setWalletItem(
                    account.displayText,
                    account.userAddress,
                    account.balance,
                    account.currentValue,
                    account.coinCode, account
                )
            is JointAccount -> holder.setJointAccountItem(account = account)
            is BankAccount -> holder.setBankAccountItem(account = account)
            is Card -> holder.setCardItem(card = account)
            is CryptoWallet -> holder.setCrypto(account = account)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountDataHolder {
        val binder = AccountItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountDataHolder(binder)
    }
}

*/




class MyAccountListAdapter(private val listener: ItemClickListener) : ListAdapter<ReltimeAccount, AccountDataHolder>(MyAccountsItemDiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountDataHolder {
        val binder = AccountItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountDataHolder(binder)
    }

    override fun onBindViewHolder(holder: AccountDataHolder, position: Int) {
        println("NAGRADEBUG ${getItem(position) is RTO}   ${getItem(position)}   ")
        when (val account = getItem(position)) {
            is RTO ->
                holder.setWalletItem(
                    account.displayText,
                    account.userAddress,
                    account.balance,
                    account.currentValue,
                    account.coinCode.convertRTOtoEURO(), account
                )
            is JointAccount -> holder.setJointAccountItem(account = account)
            is BankAccount -> holder.setBankAccountItem(account = account)
            is Card -> holder.setCardItem(card = account)
            is CryptoWallet -> holder.setCrypto(account = account)

        }
    }


    inner class AccountDataHolder(private val binder: AccountItemBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun setWalletItem(
            accountName: String, userAddress: String, displayBalance: String, currentValue: String?,
            type: String, account: ReltimeAccount
        ) {
            binder.apply {
                tvAccountName.text = accountName.convertRTOtoEURO()
                tvHash.text = userAddress
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.balance)
                tvBalanceLabel.visibility = View.VISIBLE
                tvBalance.text =
                    account.getAccountBalanceWithCoinCode()// Utils.getRTOAmount(displayBalance)
                ivTypeIcon.setImageResource(R.drawable.nagra_round_small)
                ivEdit.visibility = View.INVISIBLE
                ivQr.visibility = View.VISIBLE
                ivCopy.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivDelete.visibility = View.GONE
                root.setOnClickListener {

                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(userAddress, null)
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(userAddress, null)
                }
            }
        }

        fun setCrypto(
            account: CryptoWallet
        ) {
            binder.apply {
                tvAccountName.text = account.coin_name
                tvHash.text = account.address
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.balance)
                tvBalanceLabel.visibility = View.VISIBLE
                tvBalance.text =
                    account.getAccountBalanceWithCoinCode()// Utils.getRTOAmount(displayBalance)
               if (account.icon.isBrandLogo()) {
                   ivTypeIcon.setImageResource(R.drawable.nagra_round_small)
               } else {
                   ivTypeIcon.loadImageWithUrl(account.icon) {
                       if (!it) {
                           ivTypeIcon.setImageResource(R.drawable.ic_crypto)
                       }
                   }
               }
                ivEdit.visibility = View.INVISIBLE
                ivQr.visibility = View.VISIBLE
                ivCopy.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivDelete.visibility = View.GONE
                root.setOnClickListener {

                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(account.address, null)
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(account.address, null)
                }
            }
        }

        fun setJointAccountItem(account: JointAccount) {
            binder.apply {
                tvAccountName.text = account.name
                tvHash.text =
                    (if (account.members <= 1) "${account.members} Member" else "${account.members} Members")
                tvBalance.text = (if (account.rtoBalance == "") "0.00 RTO" else account.rtoBalance)
                ivEdit.visibility = View.VISIBLE
                ivDelete.visibility = View.GONE
                ivCopy.visibility = View.VISIBLE
                ivQr.visibility = View.VISIBLE
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.balance)
                tvBalanceLabel.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivTypeIcon.setImageResource(R.drawable.ic_add_joint)
                root.setOnClickListener {
                    listener.onAccountItemSelected("jointAccount", account, account.id)
                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(
                        account.address,
                        it.context.resources.getString(R.string.joint_account_address)
                    )
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(
                        account.address,
                        it.context.resources.getString(R.string.joint_account_address)
                    )
                }
                if (account.isAdmin) {
                    ivEdit.setOnClickListener {
                        listener.onJointAccountEdit(account)
                    }
                    ivEdit.visibility = View.VISIBLE
                } else {
                    ivEdit.visibility = View.INVISIBLE
                }
            }
        }

        fun setBankAccountItem(account: BankAccount) {
            binder.apply {
                tvAccountName.text = account.fullName
                tvHash.text = account.accountNumber
                tvBalance.text = account.address
                ivEdit.visibility = View.INVISIBLE
                ivDelete.visibility = View.GONE
                ivCopy.visibility = View.VISIBLE
                ivQr.visibility = View.VISIBLE
                ivTypeIcon.setImageResource(R.drawable.ic_bank_colored)
                tvBalanceLabel.text = this.root.context.resources.getText(R.string.address)
                tvBalanceLabel.visibility = View.VISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                root.setOnClickListener {
                    listener.onAccountItemSelected("bankAccount", account, account.id)
                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(
                        account.accountNumber,
                        it.context.resources.getString(R.string.bank_account_number)
                    )
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(
                        account.accountNumber,
                        it.context.resources.getString(R.string.bank_account_number)
                    )
                }
            }
        }

        fun setCardItem(card: Card) {
            binder.apply {
                tvAccountName.text = card.cardName
                tvHash.text = card.cardNumber
                tvBalance.text = card.cardType
                ivEdit.visibility = View.INVISIBLE
                ivCopy.visibility = View.INVISIBLE
                ivQr.visibility = View.INVISIBLE
                ivDelete.visibility = View.VISIBLE
                tvBalanceLabel.visibility = View.INVISIBLE
                tvCurrentValueLabel.visibility = View.GONE
                tvCurrentValue.visibility = View.GONE
                ivTypeIcon.setImageResource(R.drawable.ic_card_2)
                root.setOnClickListener {
                    listener.onAccountItemSelected("card", card, -1)
                }
                ivCopy.setOnClickListener {
                    listener.onCopySelect(card.cardNumber, null)
                }
                ivQr.setOnClickListener {
                    listener.onQRSelect(card.cardNumber, null)
                }
                ivDelete.setOnClickListener {
                    listener.onDelete("card", card, card.id)
                }
            }
        }

    }
}


class MyAccountsItemDiffCallback : DiffUtil.ItemCallback<ReltimeAccount>() {
    override fun areItemsTheSame(oldItem: ReltimeAccount, newItem: ReltimeAccount): Boolean = oldItem.getAccountId() == newItem.getAccountId()

    override fun areContentsTheSame(oldItem: ReltimeAccount, newItem: ReltimeAccount): Boolean = oldItem.equals(newItem)
}