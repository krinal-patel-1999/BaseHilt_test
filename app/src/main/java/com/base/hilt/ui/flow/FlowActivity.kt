package com.base.hilt.ui.flow

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.lifecycleScope
import com.base.hilt.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import kotlin.math.log

@AndroidEntryPoint
class FlowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)

        GlobalScope.launch() {
            setupFlow()
                .flowOn(Dispatchers.IO)
                .map {
                    it * 2
                }
                .filter {
                    it < 8

                }

                .collect() {
                    Log.d("FlowBase", it.toString())
                }
            getFemalePersons()
                .flowOn(Dispatchers.IO)
                .collect { femalePersons ->
                femalePersons.toString()
                Log.d("HumanFilter", femalePersons.toString())

            }
            getFemalePersons().buffer(3)
        }
                //use to shared flow
      GlobalScope.launch {
          sharedFlow()
              .collect(){
                  Log.d("Sharede",it.toString())
              }
      }
        GlobalScope.launch(Dispatchers.Main){
            val shared = sharedFlow()
            delay(1500)
                shared.collect(){
                    Log.d("Sharede2",it.toString())
                }
        }



        GlobalScope.launch {
           val state =  statedFlow()
           // delay(6000)
            Log.d("state",state.value.toString())
               /*state.collect(){
                    Log.d("state","item-$it")
                }*/
        }

    }

    val list = mutableListOf<HumanListData>()

    /*   fun humanList(): Flow<HumanListData> {
           return `flow {
               list.add(HumanListData("krinal","Female"))
               list.add(HumanListData("krinal","Female"))
               list.add(HumanListData("Piyush","Male"))
               list.add(HumanListData("Prakash","Male"))

           }

       }*/


    //Flow in use map,filter and collect

    fun getHumanFlow(): Flow<List<HumanListData>> {
        // Replace this with your actual data source or API call
        val Humanlist = listOf(
            HumanListData("John", "Male"),
            HumanListData("Jane", "Female"),
            HumanListData("Bob", "Male"),
            HumanListData("Alice", "Female")
            // Add more persons as needed
        )

        return flow {
            emit(Humanlist)
        }
    }


    fun getMalePersons(): Flow<List<HumanListData>> {
        return getHumanFlow()
            .map { Humanlist ->
                Humanlist.filter { it.Gender == "Male" }
            }
    }

    fun getFemalePersons(): Flow<List<HumanListData>> {
        return getHumanFlow()
            .map { Humanlist ->
                Humanlist.filter { it.Gender == "Female" }
            }
    }


    fun setupFlow(): Flow<Int> {
        return flow {
            Log.d(TAG, "Start flow")
            val list = listOf(1, 2, 3, 4, 5)
            list.forEach {
                // Emit items with 500 milliseconds delay
                delay(1000)
                Log.d("", "Emitting $it")
                emit(it)
            }


        }
    }

// coding for sharedflow
        fun sharedFlow(): Flow<Int> {
            val mutableSharedFlow = MutableSharedFlow<Int>()
            GlobalScope.launch {
                Log.d(TAG, "Start flow")
                val list = listOf(1, 2, 3, 4, 5)
                list.forEach {
                    mutableSharedFlow.emit(it)
                }
            }
        return mutableSharedFlow
            //.flowOn(Dispatchers.Default)
        }



    // coding for stateflow
    fun statedFlow(): StateFlow<Int> {
        val mutableStateFlow = MutableStateFlow(10)
        GlobalScope.launch {
            val list = listOf(1, 2, 3, 4, 5)
            list.forEach {
                delay(4000)
                mutableStateFlow.emit(it)
               // Log.d("state","Emitting - $it")
            }
        }
        return mutableStateFlow
        //.flowOn(Dispatchers.Default)
    }


}




