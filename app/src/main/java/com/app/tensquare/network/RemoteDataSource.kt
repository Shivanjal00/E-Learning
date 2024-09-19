package com.app.tensquare.network

import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.ui.forgotpassword.ForgotPasswordRequest
import com.app.tensquare.ui.home.HomeDataRequest
import com.app.tensquare.ui.home.SearchRequest
import com.app.tensquare.ui.home.UpdateLanguageRequest
import com.app.tensquare.ui.login.LoginRequest
import com.app.tensquare.ui.login.SignupRequest
import com.app.tensquare.ui.notification.NotificationDeleteRequest
import com.app.tensquare.ui.otp.OtpRequest
import com.app.tensquare.ui.password.NewPasswordRequest
import com.app.tensquare.ui.questionbank.PracticeQuestionRequest
import com.app.tensquare.ui.questionbank.PracticeSessionTimeRequest
import com.app.tensquare.ui.questionbank.TestAnswerSubmitRequest
import com.app.tensquare.ui.questionbank.TestQuestionRequest
import com.app.tensquare.ui.revisionvideo.LikeVideoRequest
import com.app.tensquare.ui.revisionvideo.VideoFeedbackRequest
import com.app.tensquare.ui.transaction.SignVerificationRequest
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val sharedPrefManager: SharedPrefManager
) {
    suspend fun login(loginRequest: LoginRequest) = apiService.login(loginRequest,sharedPrefManager.getUserToken() ?: "")

    suspend fun getGuestToken(deviceId: String) = apiService.getGuestToken(deviceId,sharedPrefManager.getUserToken() ?: "")

    suspend fun verifyOTP(push_token: String, request: OtpRequest) =
        apiService.verifyOTP(push_token, request,sharedPrefManager.getUserToken() ?: "")

    suspend fun resendOTP(push_token: String, request: JsonObject) =
        apiService.resendOTP(push_token, request,sharedPrefManager.getUserToken() ?: "")

    suspend fun signup(push_token: String, request: SignupRequest) =
        apiService.signup(push_token, request,sharedPrefManager.getUserToken() ?: "")

    suspend fun setPassword(request: NewPasswordRequest) = apiService.setPassword(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun updateLanguage(request: UpdateLanguageRequest) = apiService.updateLanguage(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun logout() = apiService.logout(sharedPrefManager.getUserToken() ?: "")

    suspend fun deleteUserAccount() = apiService.deleteUserAccount(sharedPrefManager.getUserToken() ?: "")

    suspend fun requestForPassword(request: ForgotPasswordRequest) =
        apiService.requestForPassword(request,sharedPrefManager.getUserToken() ?: "")

    //suspend fun getQuestionBank() = apiService.getQuestionBank()
    suspend fun getTestQuestionList(testQuestionRequest: TestQuestionRequest) =
        apiService.getTestQuestionList(testQuestionRequest,sharedPrefManager.getUserToken() ?: "")

    suspend fun submitTestAnswers(request: TestAnswerSubmitRequest) =
        apiService.submitTestAnswers(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getTestAnswer(id: String) = apiService.getAnswerSheetOfTest(id,sharedPrefManager.getUserToken() ?: "")

    suspend fun getPracticeSessionQuestionList(answerSubmissionRequest: PracticeQuestionRequest) =
        apiService.getPracticeSessionQuestionList(answerSubmissionRequest,sharedPrefManager.getUserToken() ?: "")

    suspend fun submitPracticeSessionTime(request: PracticeSessionTimeRequest) =
        apiService.submitPracticeSessionTime(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getLanguageList() = apiService.getLanguageList(sharedPrefManager.getUserToken() ?: "")

    suspend fun getClassList(languageId: String) = apiService.getClassList("android", languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getSubjectList(languageId: String) =
        apiService.getSubjectList(/*"android",*/sharedPrefManager.getSelectedClassId() ?: "",languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getRefreshToken(token : String) = apiService.getRefreshToken(token)

    suspend fun searchSomething(searchRequest: SearchRequest) =
        apiService.searchSomething(searchRequest,sharedPrefManager.getUserToken() ?: "")

    suspend fun getHomeData(token: String, request: HomeDataRequest) =
        apiService.getHomeData(/*token,*/ request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getAboutUs(languageId: String, type: String) = apiService.getAboutUs(languageId ,type,sharedPrefManager.getUserToken() ?: "")

    suspend fun getPrivacyPolicy(languageId: String , type: String) = apiService.getPrivacyPolicy(languageId , type,sharedPrefManager.getUserToken() ?: "")

    suspend fun getFAQS(languageId: String) = apiService.getFAQS(languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun contactUs(message: String) = apiService.contactUs(message,sharedPrefManager.getUserToken() ?: "")

    suspend fun getProfileDetail(token: String) = apiService.getProfileDetail(sharedPrefManager.getUserToken() ?: "")

    suspend fun getSubscriptionDetails(request: Map<String, String>) =
        apiService.getSubscriptionDetails(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun createOrder(request: String, enrollmentId: String , promoAmount: String ,
     promoCode: String,finalAmount: String,stateId: String) =
        apiService.createOrder(request, enrollmentId , promoAmount, promoCode , finalAmount,stateId,sharedPrefManager.getUserToken() ?: "")

    suspend fun verifySignature(request: SignVerificationRequest) =
        apiService.verifySignature(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun applyCoupon(request: String) = apiService.applyCoupon(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getNotificationList(request: String) = apiService.getNotificationList(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun deleteNotification(request: NotificationDeleteRequest) =
        apiService.deleteNotification(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getStateList(languageId: String) = apiService.getStateList(languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun updateProfileDetails(body: MultipartBody) = apiService.updateProfileDetails(body,sharedPrefManager.getUserToken() ?: "")

    /*suspend fun getRevisionVideoList(id: String, pageNo: Int) =
        apiService.getRevisionVideoList(id, pageNo)*/

    suspend fun getRevisionVideoList(request: Map<String, String>) =
        apiService.getRevisionVideoList(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getInstructionList(request: Map<String, String>) =
        apiService.getInstructionList(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getLatestUpdateDetail(id: String) =
        apiService.getLatestUpdateDetail(id,sharedPrefManager.getUserToken() ?: "")

    suspend fun getRevisionVideoDetail(video: String) =
        apiService.getRevisionVideoDetail(video,sharedPrefManager.getUserToken() ?: "")

    suspend fun geSearchedtRevisionVideoDetail(video: String) =
        apiService.geSearchedtRevisionVideoDetail(video,sharedPrefManager.getUserToken() ?: "")

    suspend fun doLikeVideo(request: LikeVideoRequest) =
        apiService.doLikeVideo(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun postFeedbackOnVideo(request: VideoFeedbackRequest) =
        apiService.postFeedbackOnVideo(request,sharedPrefManager.getUserToken() ?: "")

    suspend fun getModelPaperList(id: String, pageNo: Int ,  languageId: String) =
        apiService.getModelPaperList(id, pageNo , languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getHomeModelPaperList(pageNo: Int ,  languageId: String) =
        apiService.getHomeModelPaperList(pageNo ,  languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getPreviousYearPaperList(id: String, pageNo: Int, languageId: String) =
        apiService.getPreviousYearPaperList(id, pageNo , languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getHomePreviousYearPaperList(pageNo: Int , languageId: String) =
        apiService.getHomePreviousYearPaperList(pageNo, languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun downloadPdf(fileUrl: String) =
        apiService.downloadPdf(fileUrl,sharedPrefManager.getUserToken() ?: "")

    /*suspend fun getNotesList(id: String, pageNo: Int) =
        apiService.getNotesList(id, pageNo)*/

    suspend fun getNotesList(request: Map<String, String>) =
        apiService.getNotesList(request, sharedPrefManager.getUserToken() ?: "")

    suspend fun getPracticeSessionList(id: String) =
        apiService.getPracticeSessionList(id,sharedPrefManager.getUserToken() ?: "")

    suspend fun getLatestUpdateList(pageNo: Int , languageId: String) =
        apiService.getLatestUpdateList(pageNo , languageId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getTxnList(/*pageNo: Int*/) =
        apiService.getTxnList(sharedPrefManager.getUserToken() ?: "")

    suspend fun getSubjectWiseAnalysis(subjectId: String) =
        apiService.getSubjectWiseAnalysis(subjectId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getChapterWiseAnalysis(id: String) =
        apiService.getChapterWiseAnalysis(id,sharedPrefManager.getUserToken() ?: "")

    suspend fun getSubjectWiseChapterAnalysisList(subjectId: String) =
        apiService.getSubjectWiseChapterAnalysisList(subjectId,sharedPrefManager.getUserToken() ?: "")

    suspend fun getQuestionBankPaperList(id: String,languageId: String, pageNo: Int) =
        apiService.getQuestionBankPaperList(id,languageId, pageNo,sharedPrefManager.getUserToken() ?: "")

    suspend fun getVerifyUserEnrollmentPlan(subjectId: String, url: String) =
        apiService.getVerifyUserEnrollmentPlan(subjectId, url,sharedPrefManager.getUserToken() ?: "")

}