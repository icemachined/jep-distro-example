import traceback
from java.util import ArrayList

def build(builder):
    builder.build()


def process_request(builder):
    try:
        build(builder)
    except IndexError as e:
        print(traceback.format_exc())
        raise e


def get_some_list(isJava: bool):
    if isJava:
        return ArrayList()
    else:
        return [1, 2, 3]
