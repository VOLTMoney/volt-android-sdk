package com.voltmoney.voltsdk

import java.io.Serializable


interface MyCallback : Serializable{
    fun onActivityADestroyed()
}