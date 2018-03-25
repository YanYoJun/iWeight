package com.plbear.iweight.data

/**
 * Created by yanyongjun on 16/11/5.
 */
class Data {
    var id = -1
    var time: Long = 0
    var weight: Float = 0.toFloat()

    constructor(id: Int, time: Long, weight: Float) {
        init(id, time, weight)
    }

    constructor() {

    }

    private fun init(id: Int, time: Long, weight: Float) {
        this.time = time
        this.id = id
        this.weight = weight
    }

    override fun toString(): String {
        return "Data($id,$time,$weight)"
    }

    fun equals(data: Data?): Boolean {
        return if (data == null) {
            false
        } else time == data.time
    }

}