package com.zzwl.bakeMedicine.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.bakeMedicine.room.repository.UserRepository

class UserViewModel : BaseViewModel() {
     val repository by lazy { UserRepository() }
}