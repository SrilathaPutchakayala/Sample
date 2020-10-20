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
import com.tm.databinding.ActivityUserAlbumListBinding
import com.tm.databinding.ItemUserAlbumBinding
import com.tm.model.UserAlbum
import com.tm.ui.adapters.ModelBindingAdapter
import com.tm.ui.handlers.OnModelClickListener
import com.tm.utils.ConnectivityReceiver
import com.tm.viewmodels.UserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_album_list.*
import org.jetbrains.anko.startActivity

@Suppress("DEPRECATION")
class UserAlbumListActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var dataViewBinding: ActivityUserAlbumListBinding
    private lateinit var listAdapter: ModelBindingAdapter<UserAlbum, ItemUserAlbumBinding>
    private var myCompositeDisposable: CompositeDisposable? = null
    var userId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_album_list)
        dataViewBinding.viewModel = (ViewModelProviders.of(this).get(UserViewModel::class.java)).apply { init() }
        myCompositeDisposable = CompositeDisposable()

        title = "User Album"

        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        rl_user_album_list.layoutManager = layoutManager

        userId = if (intent?.extras != null && intent?.extras!!.containsKey(EXTRA_USER_ID)) {
            intent.extras!!.getInt(EXTRA_USER_ID)
        } else null

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun loadData(userId : Int?) {

        val userAlbumList = userId?.let { dataViewBinding.viewModel?.getUserAlbumListByUserId(it) }

        myCompositeDisposable?.add(userAlbumList!!
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(this::handleResponse))
    }

    private fun handleResponse(userAlbumList: List<UserAlbum>) {

        listAdapter = ModelBindingAdapter(userAlbumList.toMutableList(), R.layout.item_user_album) { binding, model ->
            binding.userAlbum = model
            binding.listener = object : OnModelClickListener<UserAlbum> {
                override fun onClick(model: UserAlbum) {
                    startActivity<UserAlbumViewActivity>("album_id" to model.albumId, "photo_id" to model.id)
                }
            }
        }
        dataViewBinding.rlUserAlbumList.adapter = listAdapter
        dataViewBinding.rlUserAlbumList.layoutManager = LinearLayoutManager(this)
        dataViewBinding.rlUserAlbumList.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        myCompositeDisposable?.clear()
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
            loadData(userId)
        }
    }

    companion object {
        const val EXTRA_USER_ID = "user_id"
    }
}