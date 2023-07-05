package com.icemachined

class ResponseBuilder {
    fun newResponse(request:Request) = Response("Function name is ${request.request}.")
}