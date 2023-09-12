from com.icemachined import ResponseBuilder
from com.icemachined import Request
import ast

class PythonService:
    def __init__(self, request):
        rootNode = ast.parse(request.getRequest())
        self.request = str(rootNode.body[0].name)


    def build(self):
        builder = ResponseBuilder()
        self.response = builder.newResponse( Request(self.request))
