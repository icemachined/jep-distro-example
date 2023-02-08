from ExampleModule import PythonService


def process_request(request):
    service = PythonService(request)
    service.build()

    response = service.response

    return response
