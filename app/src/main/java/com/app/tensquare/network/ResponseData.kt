package com.app.tensquare.network

import com.app.tensquare.base.ComingSoon

class ResponseData<T>(var status: String, var message: String, var data: T,var comingSoon: ComingSoon?= null)