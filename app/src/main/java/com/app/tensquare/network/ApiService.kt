package com.app.tensquare.network

import com.app.tensquare.activity.DeleteAccountResponse
import com.app.tensquare.activity.LogoutResponse
import com.app.tensquare.response.RefreshTokenData
import com.app.tensquare.ui.analysis.ChapterAnalysisListResponse
import com.app.tensquare.ui.analysis.ChapterAnalysisResponse
import com.app.tensquare.ui.analysis.SubjectAnalysisResponse
import com.app.tensquare.ui.appdetail.AboutUsResponse
import com.app.tensquare.ui.appdetail.ContactUsResponse
import com.app.tensquare.ui.appdetail.FaqsResponse
import com.app.tensquare.ui.appdetail.PrivacyPolicyResponse
import com.app.tensquare.ui.forgotpassword.ForgotPasswordRequest
import com.app.tensquare.ui.home.*
import com.app.tensquare.ui.initialsetup.ClassListResponse
import com.app.tensquare.ui.initialsetup.GuestToken
import com.app.tensquare.ui.initialsetup.Language
import com.app.tensquare.ui.initialsetup.SubjectData
import com.app.tensquare.ui.instruction.InstructionData
import com.app.tensquare.ui.latestupdate.LatestUpdate
import com.app.tensquare.ui.login.LoginRequest
import com.app.tensquare.ui.login.LoginResponse
import com.app.tensquare.ui.login.SignupRequest
import com.app.tensquare.ui.login.SignupResponse
import com.app.tensquare.ui.notes.NotesListResponse
import com.app.tensquare.ui.notification.NotificationDeleteRequest
import com.app.tensquare.ui.notification.NotificationDeleteResponse
import com.app.tensquare.ui.notification.NotificationList
import com.app.tensquare.ui.notification.NotificationResponse
import com.app.tensquare.ui.otp.OtpRequest
import com.app.tensquare.ui.otp.OtpVerificationResponse
import com.app.tensquare.ui.paper.ModelPaperData
import com.app.tensquare.ui.paper.PreviousYearPaperData
import com.app.tensquare.ui.password.NewPasswordRequest
import com.app.tensquare.ui.password.PasswordResponse
import com.app.tensquare.ui.pdf.PdfViewerData
import com.app.tensquare.ui.profile.ProfileDetailResponse
import com.app.tensquare.ui.profile.StateListResponse
import com.app.tensquare.ui.profile.UpdateProfileResponse
import com.app.tensquare.ui.questionbank.*
import com.app.tensquare.ui.questionbankpaper.QuestionBankPaperData
import com.app.tensquare.ui.revisionvideo.*
import com.app.tensquare.ui.session.PracticeSession
import com.app.tensquare.ui.session.PracticeSessionListResponse
import com.app.tensquare.ui.subscription.CouponData
import com.app.tensquare.ui.subscription.EnrolmentSubject
import com.app.tensquare.ui.transaction.CreateOrderData
import com.app.tensquare.ui.transaction.SignVerificationRequest
import com.app.tensquare.ui.transaction.TxnListResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject
interface ApiService {
    @POST("question/questionBankForApp")
    suspend fun getQuestionBank(@Header("token") token: String): ResponseData<Question>

    @POST("user/login")
    suspend fun login(@Body loginRequest: LoginRequest,@Header("token") token: String): Response<LoginResponse>

    @FormUrlEncoded
    @POST("user/createGuestToken")
    suspend fun getGuestToken(@Field("deviceId") deviceId: String,@Header("token") token: String): ResponseData<GuestToken>

    @POST("user/verifyMobile")
    suspend fun verifyOTP(
        @Header("push_token") push_token: String,
        @Body request: OtpRequest,
        @Header("token") token: String
    ): Response<OtpVerificationResponse>

    @POST("user/resendOTP")
    suspend fun resendOTP(
        @Header("push_token") push_token: String,
        @Body request: JsonObject,
        @Header("token") token: String
    ): Response<LoginResponse>

    @POST("user/signup")
    suspend fun signup(
        @Header("push_token") push_token: String,
        @Body request: SignupRequest,
        @Header("token") token: String
    ): Response<SignupResponse>

    @DELETE("user/removeAccount")
    suspend fun deleteUserAccount(@Header("token") token: String):Response<DeleteAccountResponse>

    @PATCH("user/setUserPassword")
    suspend fun setPassword(@Body request: NewPasswordRequest,@Header("token") token: String): Response<PasswordResponse>

    @PATCH("user/updateLanguage")
    suspend fun updateLanguage(@Body request: UpdateLanguageRequest,@Header("token") token: String): Response<UpdateLanguageResponse>

    @PATCH("user/logout")
    suspend fun logout(@Header("token") token: String): Response<LogoutResponse>

    @POST("user/forgotPassword")
    suspend fun requestForPassword(@Body request: ForgotPasswordRequest,@Header("token") token: String): Response<LoginResponse>

    @POST("language-list")
    suspend fun getLanguageList(@Header("token") token: String): ResponseData<List<Language>>

    @FormUrlEncoded
    @POST("class/get-all")
    suspend fun getClassList(
        @Header("platform") platform: String = "android",
        @Field("languageId") languageId: String,
        @Header("token") token: String
    ): ResponseData<ClassListResponse>

    @FormUrlEncoded
    @POST("get-subject")
    suspend fun getSubjectList(
        @Field("id") id: String,
        @Field("languageId") languageId: String,
        @Header("token") token: String
    ): ResponseData<List<SubjectData>>

    @GET("user/refreshAccessToken")
    suspend fun getRefreshToken(@Header("token") token: String): ResponseData<RefreshTokenData>

    @POST("home/global-search")
    suspend fun searchSomething(@Body request: SearchRequest,@Header("token") token: String): ResponseData<SearchData>

    @POST("homePageData")
    suspend fun getHomeData(
        /* @Header("token") token: String,*/
        @Body request: HomeDataRequest,
        @Header("token") token: String
    ): ResponseData<HomeData>

    @POST("user/saveFeedback")
    suspend fun postFeedbackOnVideo(@Body request: VideoFeedbackRequest,@Header("token") token: String): Response<VideoFeedbackResponse>

    @GET("about-us")
    suspend fun getAboutUs(
//        @Field("languageId") languageId: String
        @Query("languageId") languageId: String,
        @Query("type") type: String,
        @Header("token") token: String
    ): Response<AboutUsResponse>

    @GET("privacy")
    suspend fun getPrivacyPolicy(
//        @Field("languageId") languageId: String
        @Query("languageId") languageId: String,
        @Query("type") type: String,
        @Header("token") token: String
    ): Response<PrivacyPolicyResponse>

    @GET("question-answer")
    suspend fun getFAQS(
        @Query("languageId") languageId: String,
        @Header("token") token: String
    ): Response<FaqsResponse>

    @FormUrlEncoded
    @POST("user-contact")
    suspend fun contactUs(@Field("message") message: String,@Header("token") token: String): Response<ContactUsResponse>

    @GET("user/getProfileDetails")
    suspend fun getProfileDetail(@Header("token") token: String): Response<ProfileDetailResponse>

    /* @GET("enrollment/getDataForApp")
     suspend fun getSubscriptionDetails(@QueryMap request: Map<String, String>): Response<SubscriptionDataResponse>*/

    @GET("enrollment/getDataForApp")
    suspend fun getSubscriptionDetails(@QueryMap request: Map<String, String>,@Header("token") token: String): ResponseData<List<EnrolmentSubject>>

    @FormUrlEncoded
    @POST("order/create")
    suspend fun createOrder(
        @Field("amount") amount: String, @Field("enrollmentId") enrollmentId: String,
        @Field("promoAmount") promoAmount: String, @Field("promoCode") promoCode: String,
        @Field("finalAmount") finalAmount: String,
        @Field("stateId") stateId: String,
        @Header("token") token: String
    ):
    ResponseData<CreateOrderData>

    @POST("order/verify-signature")
    suspend fun verifySignature(@Body request: SignVerificationRequest,@Header("token") token: String): ResponseData<CreateOrderData>

    @GET("user-notification")
    suspend fun getNotificationList(@Query("pageNo") pageNo: String,@Header("token") token: String): ResponseData<NotificationList>

    @GET("user-notification")
    suspend fun getNotificationList1(
        /*@Header("token") token: String,*/
        @Query("pageNo") pageNo: String,
        @Header("token") token: String
    ): NotificationResponse

    //@DELETE("user-notification")
    @HTTP(method = "DELETE", path = "user-notification", hasBody = true)
    suspend fun deleteNotification(@Body request: NotificationDeleteRequest,@Header("token") token: String): ResponseData<NotificationDeleteResponse>

    @GET("getStateByLanguageId")
    suspend fun getStateList(@Query("languageId") languageId: String,@Header("token") token: String): Response<StateListResponse>

    @PATCH("user/updateProfileDetails")
    suspend fun updateProfileDetails(@Body multiPartBody: MultipartBody?,@Header("token") token: String): Response<UpdateProfileResponse>

    /*@GET("revisionVideo/getDataBySubjectId")
    suspend fun getRevisionVideoList(
        @Query("id") id: String,
        @Query("pageNo") pageNo: Int
    ): Response<RevisionVideoListResponse>*/

    @GET("revisionVideo/getDataBySubjectId")
    suspend fun getRevisionVideoList(@QueryMap request: Map<String, String>,@Header("token") token: String): ResponseData<VideoListData>

    @GET("userInstruction")
    suspend fun getInstructionList(@QueryMap request: Map<String, String>,@Header("token") token: String): ResponseData<InstructionData>

    @GET("latestUpdate/getDetails/{id}")
    suspend fun getLatestUpdateDetail(
        @Path("id") id: String,
        @Header("token") token: String
    ): ResponseData<NewUpdate>

    @GET("getChapterBySubjectId")
    suspend fun getPracticeSessionList(
        @Query("subjectId") subjectId: String,
        @Header("token") token: String
    ): Response<PracticeSessionListResponse>


    @GET("revisionVideo/getDetails/{id}")
    suspend fun getRevisionVideoDetail(@Path("id") id: String,@Header("token") token: String): ResponseData<VideoDetailData>

    @GET("home/getDataById/{id}")
    suspend fun geSearchedtRevisionVideoDetail(@Path("id") id: String,@Header("token") token: String): ResponseData<SearchedVideoDetail>

    @POST("like-video")
    suspend fun doLikeVideo(@Body request: LikeVideoRequest,@Header("token") token: String): ResponseData<Data1>

    @GET("modelPaper/getDataBySubjectId")
    suspend fun getModelPaperList(
        @Query("id") id: String,
        @Query("pageNo") pageNo: Int,
        @Query("languageId") languageId : String,
        @Header("token") token: String
    ): ResponseData<ModelPaperData>

    @GET("modelPaper/getDataBySubjectId")
    suspend fun getHomeModelPaperList(
        @Query("pageNo") pageNo: Int,
        @Query("languageId") languageId : String,
        @Header("token") token: String
    ): ResponseData<ModelPaperData>

    @GET("latestUpdate/getDataBySubjectId")
    suspend fun getLatestUpdateList(
        @Query("pageNo") pageNo: Int,
        @Query("languageId") languageId : String,
        @Header("token") token: String
    ): ResponseData<LatestUpdate>

    @GET("previousYearPaper/getDataBySubjectId")
    suspend fun getPreviousYearPaperList(
        @Query("id") id: String,
        @Query("pageNo") pageNo: Int,
        @Query("languageId") languageId : String,
        @Header("token") token: String
    ): ResponseData<PreviousYearPaperData>

    @GET("previousYearPaper/getDataBySubjectId")
    suspend fun getHomePreviousYearPaperList(
        @Query("pageNo") pageNo: Int,
        @Query("languageId") languageId : String,
        @Header("token") token: String
    ): ResponseData<PreviousYearPaperData>

    @GET("enrollment/verifyCoupon")
    suspend fun applyCoupon(
        @Query("code") pageNo: String,
        @Query("stateId") languageId : String,
        @Query("subjectName") subjectName : String,
        @Header("token") token: String
    ): ResponseData<CouponData>

    @GET("enrollment/verifyCoupon")
    suspend fun applyCoupon(
        @Query("code") code: String,
        @Header("token") token: String
    ): ResponseData<CouponData>

    /*@GET("notesResources/getDataBySubjectId")
    suspend fun getNotesList(
        @Query("id") id: String,
        @Query("pageNo") pageNo: Int
    ): Response<NotesListResponse>*/

    @GET("notesResources/getDataBySubjectId")
    suspend fun getNotesList(
        @QueryMap request: Map<String, String>,
        @Header("token") token: String
    ): Response<NotesListResponse>

    /*@GET("question/getQuestionByChapterId")
    suspend fun getQuestionByChapterId(
        @Query("chapterIds") chapterIds: String
    ): Response<PracticeQuestionListResponse>*/
    @POST("question/getPracticeSessionList")
    suspend fun getPracticeSessionQuestionList(
        @Body answerSubmissionRequest: PracticeQuestionRequest,
        @Header("token") token: String
    ): ResponseData<PracticeQuestionListData>

    @POST("userTest/saveUserPracticeTimeSpend")
    suspend fun submitPracticeSessionTime(
        @Body request: PracticeSessionTimeRequest,
        @Header("token") token: String
    ): ResponseData<String>

    @POST("userTest/getUserQuestions")
    suspend fun getTestQuestionList(
        @Body testQuestionRequest: TestQuestionRequest,
        @Header("token") token: String
    ): ResponseData<TestQuestionList>

    @POST("userTest/saveUserAnswer")
    suspend fun submitTestAnswers(
        @Body request: TestAnswerSubmitRequest,
        @Header("token") token: String
    ): ResponseData<TestAnswerSubmission>

    @GET("question/getAnswerByQuestionId")
    suspend fun getAnswerSheetOfTest(
        @Query("id") id: String,
        @Header("token") token: String
    ): ResponseData<TestAnswerSheet>

    @GET("order/myTransaction")
    suspend fun getTxnList(@Header("token") token: String): Response<TxnListResponse>

    @GET("userTest/subjectWiseAnalysis")
    suspend fun getSubjectWiseAnalysis(@Query("subjectId") subjectId: String,
                                       @Header("token") token: String): Response<SubjectAnalysisResponse>

    @GET("userTest/chapterWiseAnalysis")
    suspend fun getChapterWiseAnalysis(@Query("chapterId") chapterId: String,@Header("token") token: String): Response<ChapterAnalysisResponse>

    @GET("userTest/getChapterBySubjectAndUserId/{subjectId}")
    suspend fun getSubjectWiseChapterAnalysisList(@Path("subjectId") subjectId: String,@Header("token") token: String): Response<ChapterAnalysisListResponse>

    @GET
    fun downloadPdf(@Url fileUrl: String,@Header("token") token: String): Call<ResponseBody>

    @GET("question-bank/get-list")
    suspend fun getQuestionBankPaperList(
        @Query("subjectId") id: String,
        @Query("languageId") languageId: String,
        @Query("pageNo") pageNo: Int,
        @Header("token") token: String
    ): ResponseData<QuestionBankPaperData>

    @GET("notesResources/verifyUserEnrollmentPlan")
    suspend fun getVerifyUserEnrollmentPlan(
        @Query("subjectId") subjectId: String,
        @Query("url") url: String,
        @Header("token") token: String
    ): ResponseData<PdfViewerData>

}