package com.eternaljust.msea.utils

class UserInfo {
    companion object {
        val instance by lazy { UserInfo() }
    }

    var auth: String
        get() = DataStoreUtil.getData(UserInfoKey.AUTH, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.AUTH, value)

    var salt: String
        get() = DataStoreUtil.getData(UserInfoKey.SALT, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.SALT, value)

    var formhash: String
        get() = DataStoreUtil.getData(UserInfoKey.FORMHASH, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.FORMHASH, value)

    var uid: String
        get() = DataStoreUtil.getData(UserInfoKey.UID, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.UID, value)

    var name: String
        get() = DataStoreUtil.getData(UserInfoKey.NAME, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.NAME, value)

    var level: String
        get() = DataStoreUtil.getData(UserInfoKey.LEVEL, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.LEVEL, value)

    var avatar: String
        get() = DataStoreUtil.getData(UserInfoKey.AVATAR, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.AVATAR, value)

    var friend: String
        get() = DataStoreUtil.getData(UserInfoKey.FRIEND, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.FRIEND, value)

    var reply: String
        get() = DataStoreUtil.getData(UserInfoKey.REPLY, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.REPLY, value)

    var topic: String
        get() = DataStoreUtil.getData(UserInfoKey.TOPIC, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.TOPIC, value)

    var integral: String
        get() = DataStoreUtil.getData(UserInfoKey.INTEGRAL, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.INTEGRAL, value)

    var bits: String
        get() = DataStoreUtil.getData(UserInfoKey.BITS, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.BITS, value)

    var violation: String
        get() = DataStoreUtil.getData(UserInfoKey.VIOLATION, "")
        set(value) = DataStoreUtil.syncSetData(UserInfoKey.VIOLATION, value)

    fun clear() {
        DataStoreUtil.syncRemoveData(UserInfoKey.AUTH, auth)
        DataStoreUtil.syncRemoveData(UserInfoKey.SALT, salt)
        DataStoreUtil.syncRemoveData(UserInfoKey.FORMHASH, formhash)
        DataStoreUtil.syncRemoveData(UserInfoKey.UID, uid)
        DataStoreUtil.syncRemoveData(UserInfoKey.NAME, name)
        DataStoreUtil.syncRemoveData(UserInfoKey.LEVEL, level)
        DataStoreUtil.syncRemoveData(UserInfoKey.AVATAR, avatar)
        DataStoreUtil.syncRemoveData(UserInfoKey.FRIEND, friend)
        DataStoreUtil.syncRemoveData(UserInfoKey.REPLY, reply)
        DataStoreUtil.syncRemoveData(UserInfoKey.TOPIC, topic)
        DataStoreUtil.syncRemoveData(UserInfoKey.INTEGRAL, integral)
        DataStoreUtil.syncRemoveData(UserInfoKey.BITS, bits)
        DataStoreUtil.syncRemoveData(UserInfoKey.VIOLATION, violation)
    }
}

object UserInfoKey {
    const val AUTH = "authKey"
    const val SALT = "saltKey"
    const val FORMHASH = "formhashKey"
    const val UID = "uidKey"
    const val NAME = "nameKey"
    const val LEVEL = "levelKey"
    const val AVATAR = "avatarKey"
    const val FRIEND = "friendKey"
    const val REPLY = "replyKey"
    const val TOPIC = "topicKey"
    const val INTEGRAL = "integralKey"
    const val BITS = "bitsKey"
    const val VIOLATION = "violationKey"
}
