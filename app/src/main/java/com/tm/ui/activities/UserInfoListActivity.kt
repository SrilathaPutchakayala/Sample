package com.tm.ui.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tm.R
import com.tm.databinding.ActivityUserInfoListBinding
import com.tm.databinding.ItemUserInfoBinding
import com.tm.model.User
import com.tm.ui.adapters.ModelBindingAdapter
import com.tm.ui.handlers.OnModelClickListener
import com.tm.utils.ConnectivityReceiver
import com.tm.viewmodels.UserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_info_list.*
import org.jetbrains.anko.startActivity

@Suppress("DEPRECATION")
class UserInfoListActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var dataViewBinding: ActivityUserInfoListBinding
    private lateinit var listAdapter: ModelBindingAdapter<User, ItemUserInfoBinding>
    private var myCompositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info_list)
        dataViewBinding.viewModel = (ViewModelProviders.of(this).get(UserViewModel::class.java)).apply { init() }
        title = "User Info"
        myCompositeDisposable = CompositeDisposable()
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        rl_user_list.layoutManager = layoutManager

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun loadData() {

        myCompositeDisposable?.add(dataViewBinding.viewModel?.getUserList()!!
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(this::handleResponse))
    }

    private fun handleResponse(userInfoList: List<User>) {

        listAdapter = ModelBindingAdapter(userInfoList.toMutableList(), R.layout.item_user_info) { binding, model ->
            binding.user = model
            binding.listener = object : OnModelClickListener<User> {
                override fun onClick(model: User) {
                    startActivity<UserAlbumListActivity>("user_id" to model.id)
                }
            }
        }
        dataViewBinding.rlUserList.adapter = listAdapter
        dataViewBinding.rlUserList.layoutManager = LinearLayoutManager(this)
        dataViewBinding.rlUserList.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        )
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            dataViewBinding.viewModel?.isNetworkAvailable?.set(false)
        } else {
            dataViewBinding.viewModel?.isNetworkAvailable?.set(true)
            loadData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myCompositeDisposable?.clear()
    }
}

