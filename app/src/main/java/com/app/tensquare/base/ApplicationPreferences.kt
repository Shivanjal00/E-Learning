package com.app.tensquare.base

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefManager @Inject constructor(@ApplicationContext val context: Context) {
    private val FIRST_TIME = "FIRST_TIME"
    private val USER_ID = "user_id"
    private val USER_DATA = "user_data"
    private val USER_TOKEN = "user_token"
    private val REFRESH_TOKEN = "refresh_token"
    private val LANGUAGE_ID = "language_id"
    private val CLASS_ID = "class_id"
    private val IS_ON_BOARDING_VIEWED = "is_on_boarding_viewed"
    private val IS_GUEST = "is_guest"
    private val Is_Sub_Plan = "subPlan"
    private val IS_ENROLLED = "is_enrolled"
    private val IS_USER_PINNED_TO_HOME = "is_user_pinned_to_home"
    private val DEVICE_TOKEN = "DEVICE_TOKEN"
    private val SELECT_SUB = "SELECT_SUB"

    private val PREFS_NAME = "TenSquarePreference"
    private val LANGUAGE = "language"
    private val LANGUAGE_NAME = "language_name"
    private val CLASS_NAME = "class_name"

    private val editor: SharedPreferences.Editor

    init {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        editor = prefs.edit()
    }

    //get preference manager object for a preference_name
    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    //set data to preference manager through a key
    private fun setPreferencesData(key: String, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    private fun applyPreferencesData(key: String, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    //get data from preference manager using a key
    private fun getPreferenceData(key: String, defaultValue: String?): String? {
        return getPrefs().getString(key, defaultValue)
    }

    fun isFirstTime(): String? {
        return getPreferenceData(FIRST_TIME, "")
    }


    fun setFirstTime(data: String) {
        setPreferencesData(FIRST_TIME, data)
    }


    //set boolean preferences
    private fun setBoolean(key: String, value: Boolean?) {
        editor.putBoolean(key, value!!)
        editor.commit()
    }

    //get boolean preferences
    private fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return getPrefs().getBoolean(key, defaultValue)
    }

    fun setUserId(userId: String?) {
        setPreferencesData(USER_ID, userId)
    }

    fun getUserId(): String? {
        return getPreferenceData(USER_ID, null)
    }

    fun setUserLanguage(language: String?) {
        setPreferencesData(LANGUAGE, language)
    }

    fun getUserLanguage(): String? {
        return getPreferenceData(LANGUAGE, null)
    }

    fun setUserData(userId: String?) {
        setPreferencesData(USER_DATA, userId)
    }

    fun getUserData(): String? {
        return getPreferenceData(USER_DATA, "")
    }

    fun setUserToken(userToken: String?) {
        setPreferencesData(USER_TOKEN, userToken)
    }

    fun getUserToken(): String? {
        return getPreferenceData(USER_TOKEN, "")
    }

    fun setRefreshToken(userToken: String?) {
        setPreferencesData(REFRESH_TOKEN, userToken)
    }

    fun getRefreshToken(): String? {
        return getPreferenceData(REFRESH_TOKEN, "")
    }

    fun setSelectedLanguageId(id: String?) {
        setPreferencesData(LANGUAGE_ID, id)
    }

    fun getSelectedLanguageId(): String? {
        return getPreferenceData(LANGUAGE_ID, "")
    }

   fun setSelectedLanguagenName(id: String?) {
        setPreferencesData(LANGUAGE_NAME, id)
    }

    fun getSelectedLanguageName(): String? {
        return getPreferenceData(LANGUAGE_NAME, "English")
    }

    fun setSelectedClassId(id: String?) {
        setPreferencesData(CLASS_ID, id)
    }

    fun getSelectedClassId(): String? {
        return getPreferenceData(CLASS_ID, "")
    }

    fun setSelectedClassName(id: String?) {
        setPreferencesData(CLASS_NAME, id)
    }

    fun getSelectedClassName(): String? {
        return getPreferenceData(CLASS_NAME, "")
    }

    fun setOnBoardingViewed(isViewed: Boolean) {
        return setBoolean(IS_ON_BOARDING_VIEWED, isViewed)
    }

    fun isOnBoardingViewed(): Boolean {
        return getBoolean(IS_ON_BOARDING_VIEWED, false)
    }

    fun setIsGuestUser(isGuest: Boolean) {
        return setBoolean(IS_GUEST, isGuest)
    }

    fun isGuestUser(): Boolean {
        return getBoolean(IS_GUEST, false)
    }

    fun setIsEnrolled(isGuest: Boolean) {
        return setBoolean(IS_ENROLLED, isGuest)
    }

    fun isEnrolled(): Boolean {
        return getBoolean(IS_ENROLLED, false)
    }

    // particular Sub Plan Check
    fun setIsSubPlan(isSubPlan: Boolean) {
        return setBoolean(Is_Sub_Plan, isSubPlan)
    }

    fun isSubPlan(): Boolean {
        return getBoolean(Is_Sub_Plan, false)
    }

    fun setUserPinnedToHome(isViewed: Boolean) {
        return setBoolean(IS_USER_PINNED_TO_HOME, isViewed)
    }

    fun isUserPinnedToHome(): Boolean {
        return getBoolean(IS_USER_PINNED_TO_HOME, false)
    }

    fun getSelectSub(): String? {
        return getPreferenceData(SELECT_SUB, "2")
    }

    fun setSelectSub(selectSub : String?) {
        applyPreferencesData(SELECT_SUB, selectSub)
    }

    fun getDeviceToken(): String? {
        return getPreferenceData(DEVICE_TOKEN, "")
    }

    fun setDeviceToken(deviceToken: String?) {
        applyPreferencesData(DEVICE_TOKEN, deviceToken)
    }


    /*  fun setInt(key: String?, value: Int) {
          editor.putInt(key, value)
          editor.apply()
      }

      fun getInt(key: String?): Int {
          val prefs = context.getSharedPreferences(PREFS_NAME, 0)
          return prefs.getInt(key, 0)
      }

      fun setString(key: Satring?, value: String?) {
          editor.putString(key, value)
          editor.apply()
      }

      fun getString(key: String): String? {
          val prefs = context.getSharedPreferences(PREFS_NAME, 0)
          return prefs.getString(key, "")
      }

      fun setBoolean(key: String?, value: Boolean) {
          editor.putBoolean(key, value)
          editor.apply()
      }

      fun getBoolean(key: String?): Boolean {
          val prefs = context.getSharedPreferences(PREFS_NAME, 0)
          return prefs.getBoolean(key, false)
      }*/

    fun getString(key: String): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(key, "")
    }


    fun clear() {
        editor.clear()
        editor.apply()
    }


}

/*
class ApplicationPreferences(private val context: Context) {
    private lateinit var prefsT: ApplicationPreferences
    private val PREF_NAME = "namo_chai_prefs"
    private val USER_ID = "user_id"
    private val USER_DETAILS = "user_details"
    private val USER_DETAILS_ON_BOARDING = "user_details_on_boarding"
    private val FCM_TOKEN = "fcm_token"
    private val IS_ONBOARDING_SKIPPED = "is_onboarding_skipped"
    private val CITY_ID = "city_id"
    private val CITY_PINCODE = "city_pincode"
    private val EMAIl = "email"
    private val USER_ADDRESS = "user_address"
    private val USER_TOKEN = "user_token"
    private val GUEST_TOKEN = "guest_token"
    private val NOTIFICATION_COUNT = "notification_count"
    private val OFFER_COUNT = "offer_count"
    private val LAST_USER_MOBILE_NO = "lastUserMobileNo"
    private val SMS_COUNT = "smsCount"
    private val IS_PIN_SET = "is_pin_set"


    fun setPinStatus(name: Boolean) {
        setPreferencesData(IS_PIN_SET, name)
    }

    fun getPinStatus(): Boolean {
        return getPreferenceData(IS_PIN_SET, false)
    }



    fun setLastUserMobileNo(lastUserMobileNo: String?) {
        setPreferencesData(LAST_USER_MOBILE_NO, lastUserMobileNo)
    }

    fun getLastUserMobileNo(): String? {
        return getPreferenceData(LAST_USER_MOBILE_NO, null)
    }

   */
/* fun setUserDetails(userDetail: UserData) {
        val gson = Gson()
        setPreferencesData(USER_DETAILS, gson.toJson(userDetail))
    }

    fun getUserDetails(): UserData {
        val gson = Gson()
        return gson.fromJson(getPreferenceData(USER_DETAILS, ""), UserData::class.java)
    }*//*


    fun setIsOnboardingSkipped(isOnboardingSkipped: Boolean) {
        setPreferencesData(IS_ONBOARDING_SKIPPED, isOnboardingSkipped)
    }

    fun getIsOnboardingSkipped(): Boolean {
        return getPreferenceData(IS_ONBOARDING_SKIPPED, false)
    }

    fun setNotificationCount(count: Int) {
        setPreferencesData(NOTIFICATION_COUNT, count)
    }

    fun getNotificationCount(): Int {
        return getPreferenceData(NOTIFICATION_COUNT, 0)
    }

    fun setOfferCount(count: Int) {
        setPreferencesData(OFFER_COUNT, count)
    }

    fun getOfferCount(): Int {
        return getPreferenceData(OFFER_COUNT, 0)
    }

    fun setCityPinCode(cityPincode: String?) {
        setPreferencesData(CITY_PINCODE, cityPincode)
    }

    fun getCityPinCode(): String? {
        return getPreferenceData(CITY_PINCODE, "")
    }

    fun setEmail(email: String?) {
        setPreferencesData(EMAIl, email)
    }

    fun getEmail(): String? {
        return getPreferenceData(EMAIl, null)
    }

    fun setUserToken(userToken: String?) {
        setPreferencesData(USER_TOKEN, userToken)
    }

    fun getUserToken(): String? {
        return getPreferenceData(USER_TOKEN, "")
    }

    fun setGuestToken(guestToken: String?) {
        setPreferencesData(GUEST_TOKEN, guestToken)
    }

    fun getGuestToken(): String? {
        return getPreferenceData(GUEST_TOKEN, "")
    }

    fun getAccessToken(): String? {
        return if (isLogin()) getUserToken() else getGuestToken()
    }

    fun setSmsCount(smsCount: String?) {
        setPreferencesData(SMS_COUNT, smsCount)
    }

    fun getSmsCount(): String? {
        return getPreferenceData(SMS_COUNT, "")
    }

    fun setFCMToken(userToken: String?) {
        setPreferencesData(FCM_TOKEN, userToken)
    }

    fun getFCMToken(): String? {
        return getPreferenceData(FCM_TOKEN, "")
    }

    fun isLogin(): Boolean {
        return getUserId() != null && getUserId()!!.trim { it <= ' ' }.isNotEmpty()
    }

    fun clearUser() {
//        setLastUserMobileNo(getSaloonDetail().mobile_no)
        setUserId(null)
        setSmsCount(null)
        //setUserDetails(null)
        setUserToken(null)
    }

    fun clear() {
        getPrefsEditor().clear().commit()
    }

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun getPrefsEditor(): SharedPreferences.Editor {
        return getPrefs().edit()
    }

    */
/*
     * Save string data type
     *
     * *//*

    private fun setPreferencesData(key: String, value: String?) {
        val editor = getPrefsEditor()
        editor.putString(key, value)
        editor.commit()
    }

    */
/*
     * Save Int data type
     *
     * *//*

    private fun setPreferencesData(key: String, value: Int) {
        val editor = getPrefsEditor()
        editor.putInt(key, value)
        editor.commit()
    }

    */
/*
     * Save Long data type
     *
     * *//*

    private fun setPreferencesData(key: String, value: Long) {
        val editor = getPrefsEditor()
        editor.putLong(key, value)
        editor.commit()
    }

    */
/*
     * Save boolean data type
     *
     * *//*

    private fun setPreferencesData(key: String, value: Boolean) {
        val editor = getPrefsEditor()
        editor.putBoolean(key, value)
        editor.commit()
    }

    */
/*
     * Save float data type
     *
     * *//*

    private fun setPreferencesData(key: String, value: Float) {
        val editor = getPrefsEditor()
        editor.putFloat(key, value)
        editor.commit()
    }

    */
/*
     *
     * fetch  method for pref
     *
     * *//*

    */
/*
     *
     * fetch  method for pref
     *
     * *//*

    */
/*
     *  getPreferenceData String data from pref
     * *//*

    private fun getPreferenceData(key: String, defaultValue: String?): String? {
        return getPrefs().getString(key, defaultValue)
    }

    */
/*
     *  getPreferenceData int data from pref
     * *//*

    private fun getPreferenceData(key: String, defaultValue: Int): Int {
        return getPrefs().getInt(key, defaultValue)
    }

    */
/*
     *  getPreferenceData boolean data from pref
     * *//*

    private fun getPreferenceData(key: String, defaultValue: Boolean): Boolean {
        return getPrefs().getBoolean(key, defaultValue)
    }

    */
/*
     *  getPreferenceData long data from pref
     * *//*

    private fun getPreferenceData(key: String, defaultValue: Long): Long {
        return getPrefs().getLong(key, defaultValue)
    }
}*/
