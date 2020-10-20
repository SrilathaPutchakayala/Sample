package com.tm.repository

class UserRepository {

    private var userApi: UserApi? = null

    companion object {
        @JvmStatic
        fun getInstance(): UserRepository {
            
            return UserRepository()
        }
    }
    init {
        userApi = RetrofitService.createService(UserApi::class.java)
    }

    fun getUserInfo() = userApi?.getUsersList()

    
    fun getUserAlbumListByUserId(userId : Int) = userApi?.getUserAlbumListByUserId(userId)
    
    fun getUserAlbumDetail(albumId : Int, photoId : Int) = userApi?.getUserAlbumByAlbumIdAndUserId(albumId, photoId)

}