import traceback

def build(builder):
    builder.build()

def process_request(builder):
    try:
        build(builder)
    except Exception as e:
        print(traceback.format_exc())
        raise e

