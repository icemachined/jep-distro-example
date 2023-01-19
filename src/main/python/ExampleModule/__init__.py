from com.icemachined import ResponseBuilder

class PythonService:
    def __init__(self, request):
        self.request = request


    def build(self):
        builder = ResponseBuilder()
        self.response = builder.newResponse( self.request )
