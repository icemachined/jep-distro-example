from pemja import findClass
import ast

class PythonService:
    def __init__(self, request):
        rootNode = ast.parse(request.getRequest())
        self.request = str(rootNode.body[0].name)


    def build(self):
        ResponseBuilder = findClass('com.icemachined.ResponseBuilder')
        Request = findClass('com.icemachined.Request')

        builder = ResponseBuilder()
        self.response = builder.newResponse( Request(self.request))
