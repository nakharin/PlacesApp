package com.nakharin.placesapp.view.fragment.nearby

interface ContractView {
    fun onShowLoading()
    fun onHideLoading()
    fun onResponseSuccess(name: String?, vicinity: String?)
    fun onResponseError(localizedMessage: String)
}