from datetime import datetime

from com.icemachined import ResponseBuilder
from com.icemachined import Request
import ast

def extract_function_name(request):
    rootNode = ast.parse(request.getRequest())
    return str(rootNode.body[0].name)


def get_time():
    return "Time is " + str(datetime.now())


class PythonService:
    def __init__(self, request):
        self.request = extract_function_name(request)


    def build(self):
        builder = ResponseBuilder()
        self.response = builder.newResponse( Request(self.request))
