package com.zoku.runit

import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.zoku.ui.model.PhoneWatchConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ClientDataViewModel @Inject constructor(

) : ViewModel(), DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _phoneWatchData = MutableStateFlow<PhoneWatchConnection>(PhoneWatchConnection.EMPTY)
    val phoneWatchData: StateFlow<PhoneWatchConnection> get() = _phoneWatchData

    val _bpm = MutableStateFlow<List<Int>>(emptyList())
    val bpm: StateFlow<List<Int>> get() = _bpm

    fun addBpmData(bpm: Int) {
        _bpm.update { data ->
            data + bpm
        }
    }

    fun updateMessageType(connection: PhoneWatchConnection) {
        _phoneWatchData.update { connection }
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {

    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == PhoneWatchConnection.SEND_BPM.route) {
            // ByteArray를 String으로 변환한 후 Int로 변환
            val bpmString = messageEvent.data.toString(Charsets.UTF_8)
            val bpm = bpmString.toIntOrNull()

            if (bpm != null) {
                addBpmData(bpm)
                Timber.tag("ClientDataViewModel").d("BPM 받기 성공 ${bpm}")
            } else {
                Timber.tag("ClientDataViewModel").e("BPM 받기 실패")
            }
        }
        updateMessageType(
            PhoneWatchConnection.getType(messageEvent.path) ?: PhoneWatchConnection.EMPTY
        )
        Timber.tag("ClientDataViewModel").d("message받기 $messageEvent")
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Timber.tag("ClientDataViewModel").d("$capabilityInfo")
    }
}