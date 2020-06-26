package com.example.admin.activity

import android.content.Intent
import android.util.Log.e
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.admin.adapter.SubServicesAdapter
import com.example.admin.base.BaseActivity
import com.example.admin.databinding.ActivityDashboardBinding
import com.example.admin.model.ServicesModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.example.admin.R

class DashboardActivity : BaseActivity<ActivityDashboardBinding>(){

    private lateinit var mBinding: ActivityDashboardBinding
    private var databaseRef: DatabaseReference? = null
    private var servicesName : ArrayList<String>? = null
    private var db: FirebaseFirestore? = null
    private var randomName: String = ""
    private var servicesModel = ServicesModel()
    private var serviceAdapter : SubServicesAdapter? = null
    private var name : ArrayList<String> = ArrayList()

    override fun onPermissionsGranted(requestCode: Int) {
    }

    override fun contentView() = R.layout.activity_dashboard

    override fun initUI(binding: ActivityDashboardBinding) {
        mBinding = binding
        db = FirebaseFirestore.getInstance()
        serviceAdapter = SubServicesAdapter()
        mBinding.rvSubServices.adapter = serviceAdapter

        mBinding.toolBar.txtSignOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        mBinding.toolBar.imgBack.setOnClickListener {
            if(mAuth.currentUser!=null){
                this.finishAffinity()
            }
        }

        servicesName = ArrayList()
        mBinding.progressBar.visibility = View.VISIBLE
        databaseRef = FirebaseDatabase.getInstance().getReference("Services")
        databaseRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                mBinding.progressBar.visibility = View.GONE
            }
            override fun onDataChange(p0: DataSnapshot) {
                for(i in p0.children.iterator()){
                    servicesName?.add(i.getValue(String::class.java)!!)
                }
                updateDropDown(servicesName!!)
                mBinding.progressBar.visibility = View.GONE
            }
        })

        getTotalVenders()


        mBinding.spServices.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(servicesName!!.isNotEmpty()){
                    mBinding.txtHeading.text = servicesName!![position]
                    fetchForRv()
                }
            }
        }

        mBinding.btnAdd.setOnClickListener {
            if(mBinding.edtServices.text.isNotEmpty()){
                mBinding.progressBar.visibility = View.VISIBLE
                randomName = System.currentTimeMillis().toString()
                val hashMap:HashMap<String,String> = HashMap()
                hashMap[randomName] = mBinding.edtServices.text.toString()

                db?.collection("Admin")?.document(mBinding.txtHeading.text.toString())
                    ?.update("name", FieldValue.arrayUnion(hashMap))
                    ?.addOnCompleteListener {
                       // showToast("Update")
                        mBinding.edtServices.text.clear()
                        fetchForRv()
                        mBinding.progressBar.visibility = View.GONE
                    }?.addOnFailureListener {
                        showToast(it.message.toString())
                        mBinding.progressBar.visibility = View.GONE
                    }?.addOnCanceledListener {
                        showToast("Cancel")
                        mBinding.progressBar.visibility = View.GONE
                    }
            }else{
                showToast("Enter sub services")
            }
        }
    }

    private fun getTotalVenders() {
        db?.collection("Users")?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list: MutableList<String> = ArrayList()
                    for (document in task.result!!) {
                        list.add(document.id)
                    }
                    mBinding.txtActiveServices.text = "Active Services: ${list.size}"
                    mBinding.txtTotalVenders.text = "Active Services: ${list.size}"
                } else {
                    showToast("something went wrong")
                }
            }?.addOnFailureListener {
                showToast(it.message.toString())
            }
    }

    private fun fetchForRv() {
        mBinding.progressBar.visibility = View.VISIBLE
        db?.collection("Admin")?.document(mBinding.txtHeading.text.toString())?.get()?.addOnCompleteListener {
           if(it.isSuccessful){
               servicesModel = it.result?.toObject(ServicesModel::class.java)!!
               name.clear()
               servicesModel.name?.map { it ->
                   it.mapValues {
                       name.add(it.value)
                   }
               }

               serviceAdapter?.addItem(name)
               serviceAdapter?.notifyDataSetChanged()
               mBinding.progressBar.visibility = View.GONE

           } else{
               showToast("Something went wrong")
               mBinding.progressBar.visibility = View.GONE
           }
        }?.addOnFailureListener {
            showToast(it.message.toString())
            mBinding.progressBar.visibility = View.GONE
        }
    }

    fun updateDropDown(servicesName: ArrayList<String>) {
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_value, servicesName
        )
        mBinding.spServices.adapter = spinnerArrayAdapter
        mBinding.txtTotalServices.text = "Total Services : ${servicesName.size}"
    }
}