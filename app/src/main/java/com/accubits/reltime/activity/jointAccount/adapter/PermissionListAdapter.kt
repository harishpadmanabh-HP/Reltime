package com.accubits.reltime.activity.jointAccount.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.accubits.reltime.databinding.PermissionListviewBinding
import com.accubits.reltime.models.PermissionList

class PermissionListAdapter(
    private var selectedPermission: Int,
    private var listener: ClickListener
) : BaseAdapter() {
    private var permissionList: ArrayList<PermissionList>? = null

    //  private var prevView: RadioButton? = null
    //   private lateinit var touchList: ArrayList<Boolean>
    fun setItems(faqList: ArrayList<PermissionList>) {
        this.permissionList = faqList
        //      touchList = ArrayList(Collections.nCopies(faqList.size, false))
    }

    override fun getCount(): Int {
        return permissionList!!.size
    }

    override fun getItem(position: Int): Any {
        return permissionList?.get(position) as PermissionList
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binder = PermissionListviewBinding.inflate(LayoutInflater.from(parent?.context))
        onClick(binder, position)
        binder.tvHeader.text = permissionList?.get(position)?.label
        binder.tvHeader.isChecked =
            selectedPermission == permissionList?.get(position)?.permissionId
        return binder.root
    }

    fun onClick(binder: PermissionListviewBinding, position: Int) {
        binder.tvHeader.setOnClickListener {
            permissionList?.get(position)?.let {
                selectedPermission = it.permissionId
                listener.onClick(permissionList?.get(position)!!)
                notifyDataSetChanged()
            }
        }
    }

}

interface ClickListener {
    fun onClick(permission: PermissionList)
}