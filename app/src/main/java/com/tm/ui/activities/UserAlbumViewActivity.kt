package com.tm.ui.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.tm.R
import com.tm.databinding.ActivityUserAlbumDetailBinding
import com.tm.model.UserAlbum
import com.tm.utils.ConnectivityReceiver
import com.tm.viewmodels.UserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@Suppress("DEPRECATION")
class UserAlbumViewActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private lateinit var dataViewBinding: ActivityUserAlbumDetailBinding
    private var myCompositeDisposable: CompositeDisposable? = null
    var albumId : Int? = null
    var photoId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_album_detail)
        dataViewBinding.viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java).apply { init() }
        myCompositeDisposable = CompositeDisposable()

        title = "User Album Detail"

        albumId = if (intent?.extras != null && intent?.extras!!.containsKey(EXTRA_ALBUM_ID)) {
            intent.extras!!.getInt(EXTRA_ALBUM_ID)
        } else null

        photoId = if (intent?.extras != null && intent?.extras!!.containsKey(EXTRA_PHOTO_ID)) {
            intent.extras!!.getInt(EXTRA_PHOTO_ID)
        } else null

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun loadData(albumId : Int?, photoId: Int?) {
        dataViewBinding.viewModel?.albumId?.set(albumId)
        dataViewBinding.viewModel?.photoId?.set(photoId)

        val userAlbum = dataViewBinding.viewModel?.getUserAlbumDetail(albumId!!, photoId!!)

        myCompositeDisposable?.add(userAlbum!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse))

    }

    private fun handleResponse(userAlbumList: List<UserAlbum>) {
        if(userAlbumList.isNotEmpty()){
            dataViewBinding.viewModel?.userAlbumTitle?.set(userAlbumList.first().title)
            dataViewBinding.viewModel?.userAlbumUrl?.set(userAlbumList.first().url)
        }
    }

    companion object {
        const val EXTRA_ALBUM_ID = "album_id"
        const val EXTRA_PHOTO_ID = "photo_id"
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
            loadData(albumId, photoId)
        }
    }
}


