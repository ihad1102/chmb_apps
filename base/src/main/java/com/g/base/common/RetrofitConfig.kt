package com.g.base.common

import com.g.base.api.createRetrofitService

//const val BASE_URL = "http://zsxn.tomsung.cn:8080/"
//const val BASE_URL = "http://192.168.123.133:8080/app/" //zq
//const val BASE_URL = "http://192.168.123.63:8080/app/" //wh
//const val BASE_URL = "http://192.168.123.118:8080/app/" //hg
const val BASE_URL = "http://101.37.17.62:8085/app/" //正式
//const val BASE_URL = "http://121.40.214.181:8085/app/" //测试
val apiProvider by lazy { createRetrofitService(BASE_URL) }
val apiProviderMock by lazy { createRetrofitService("https://dsn.apizza.net/mock/9a7892f4810dadfc2558c4b9a60d74bd/") }
