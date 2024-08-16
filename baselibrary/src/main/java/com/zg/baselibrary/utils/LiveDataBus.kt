package com.zg.baselibrary.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveDataBus private constructor() {
    //存放订阅者
    private val bus: MutableMap<String, BusMutableLiveData<Any>>

    init {
        bus = HashMap()
    }

    /**
     * 用来给用户进行订阅（存入map）因为可能会有很多用户进行订阅，所以加上同步
     *
     * @param key
     * @param type
     * @param <T>
     * @return
    </T> */
    @Synchronized
    fun <T> with(key: String, type: Class<T>?): BusMutableLiveData<T> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as BusMutableLiveData<T>
    }

    fun with(key: String): BusMutableLiveData<Any> {
        return with(key, Any::class.java)
    }

    class BusMutableLiveData<T> : MutableLiveData<T>() {
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            hook(observer)
        }

        private fun hook(observer: Observer<in T>) {

            //通过反射修改mLastVersion的值
            val liveDataClass = LiveData::class.java
            try {
                val field = liveDataClass.getDeclaredField("mObservers")
                field.isAccessible = true
                //获取这个字段对应的对象
                val mObject = field[this]
                //得到SafeIterableMap.class
                val aClass: Class<*> = mObject.javaClass
                //获取到SafeIterableMap的get方法
                val get = aClass.getDeclaredMethod("get", Any::class.java)
                get.isAccessible = true
                //执行get方法
                val invoke = get.invoke(mObject, observer)
                //取到SafeIterableMap中的value
                var observerWraper: Any? = null
                if (invoke != null && invoke is Map.Entry<*, *>) {
                    observerWraper = invoke.value
                }
                if (observerWraper == null) {
                    throw NullPointerException("observerWraper is null")
                }
                //得到ObserverWrapper的类对象(这里需要getSuperclass是因为map集合塞的就是ObserverWrapper的子类对象)
                val superclass: Class<*> = observerWraper.javaClass.superclass
                val mLastVersion = superclass.getDeclaredField("mLastVersion")
                mLastVersion.isAccessible = true

                //得到mVersion
                val mVersion = liveDataClass.getDeclaredField("mVersion")
                mVersion.isAccessible = true
                //把mVersion的值填入到mLastVersion中去
                val mVersionValue = mVersion[this]
                mLastVersion[observerWraper] = mVersionValue
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        //单例
        val instance = LiveDataBus()
    }
}