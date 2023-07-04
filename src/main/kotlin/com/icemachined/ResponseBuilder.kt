package com.icemachined

class ResponseBuilder {
    fun newResponse(request:Request) = Response("${request.request}, world!")
}